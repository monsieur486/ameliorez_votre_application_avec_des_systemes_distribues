package com.openclassrooms.tourguide.service;

import com.openclassrooms.tourguide.dto.AttractionNearbyUserDto;
import com.openclassrooms.tourguide.exception.UserNotFoundException;
import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.tracker.Tracker;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.util.ParallelTaskExecutor;
import com.openclassrooms.tourguide.user.UserReward;
import gpsUtil.GpsUtil;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

/**
 * Service central du guide touristique : localisation, récompenses et offres de voyage.
 *
 * <p><b>Exemple :</b> orchestre le suivi des utilisateurs, le calcul de leurs récompenses
 * et la recherche des attractions proches à partir de leur dernière position.</p>
 */
@Service
public class TourGuideService {
    private final Logger logger = LoggerFactory.getLogger(TourGuideService.class);
    private final GpsUtil gpsUtil;
    private final RewardsService rewardsService;
    private final TripPricer tripPricer = new TripPricer();
    public final Tracker tracker;
    boolean testMode = true;

    /**
     * Taille du pool de threads utilisé pour le traitement parallèle des utilisateurs.
     */
    private static final int THREAD_POOL_SIZE = 200;
    /**
     * Délai maximal d'attente de la fin des tâches avant arrêt forcé du pool (minutes).
     */
    private static final long AWAIT_TERMINATION_MINUTES = 15;

    /**********************************************************************************
     *
     * Methods Below: For Internal Testing
     *
     **********************************************************************************/
    private static final String tripPricerApiKey = "test-server-api-key";

    /**
     * Construit le service, initialise les utilisateurs internes en mode test et démarre le suivi.
     *
     * @param gpsUtil        le fournisseur de localisation GPS
     * @param rewardsService le service de calcul des récompenses
     */
    public TourGuideService(GpsUtil gpsUtil, RewardsService rewardsService) {
        this.gpsUtil = gpsUtil;
        this.rewardsService = rewardsService;

        Locale.setDefault(Locale.US);

        if (testMode) {
            logger.info("TestMode enabled");
            logger.debug("Initializing users");
            initializeInternalUsers();
            logger.debug("Finished initializing users");
        }
        tracker = new Tracker(this);
        addShutDownHook();
    }

    /**
     * Renvoie les récompenses accumulées par un utilisateur.
     *
     * <p><b>Exemple :</b> pour un utilisateur ayant visité deux attractions récompensées,
     * renvoie la liste de ses deux récompenses.</p>
     *
     * @param user l'utilisateur concerné
     * @return la liste des récompenses de l'utilisateur
     */
    public List<UserReward> getUserRewards(User user) {
        return user.getUserRewards();
    }

    /**
     * Recherche un utilisateur par son nom.
     *
     * <p><b>Exemple :</b> {@code getUser("internalUser0")} renvoie l'utilisateur de test
     * correspondant ; {@code getUser("inconnu")} lève {@link UserNotFoundException}.</p>
     *
     * @param userName le nom de l'utilisateur recherché
     * @return l'utilisateur trouvé
     * @throws UserNotFoundException si aucun utilisateur ne correspond au nom fourni
     */
    public User getUser(String userName) {
        User user = internalUserMap.get(userName);
        if (user == null) {
            throw new UserNotFoundException(userName);
        }
        return user;
    }

    /**
     * Renvoie la localisation courante de l'utilisateur, en la calculant si nécessaire.
     *
     * <p><b>Exemple :</b> renvoie la dernière position visitée si elle existe, sinon
     * déclenche un nouveau suivi de position.</p>
     *
     * @param user l'utilisateur concerné
     * @return la localisation courante de l'utilisateur
     */
    public VisitedLocation getUserLocation(User user) {
        return (!user.getVisitedLocations().isEmpty()) ? user.getLastVisitedLocation()
            : trackUserLocation(user);
    }

    /**
     * Ajoute un utilisateur au référentiel interne s'il n'y figure pas déjà.
     *
     * <p><b>Exemple :</b> {@code addUser(user)} enregistre un nouvel utilisateur, mais
     * n'écrase pas un utilisateur portant déjà le même nom.</p>
     *
     * @param user l'utilisateur à ajouter
     */
    public void addUser(User user) {
        if (!internalUserMap.containsKey(user.getUserName())) {
            internalUserMap.put(user.getUserName(), user);
        }
    }

    /**
     * Renvoie l'ensemble des utilisateurs connus.
     *
     * <p><b>Exemple :</b> en mode test, renvoie les cent utilisateurs internes générés
     * au démarrage.</p>
     *
     * @return la liste de tous les utilisateurs
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(internalUserMap.values());
    }

    /**
     * Suit la position d'un utilisateur, l'enregistre et met à jour ses récompenses.
     *
     * <p><b>Exemple :</b> récupère la position GPS courante, l'ajoute à l'historique de
     * l'utilisateur puis recalcule ses récompenses.</p>
     *
     * @param user l'utilisateur à localiser
     * @return la nouvelle localisation visitée
     */
    public VisitedLocation trackUserLocation(User user) {
        VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
        user.addToVisitedLocations(visitedLocation);
        rewardsService.calculateRewards(user);
        return visitedLocation;
    }

    /**
     * Renvoie les cinq attractions les plus proches de la localisation fournie.
     *
     * <p><b>Exemple :</b> à partir de la position d'un utilisateur, renvoie les cinq
     * attractions les plus proches triées par distance croissante.</p>
     *
     * @param visitedLocation la localisation de référence de l'utilisateur
     * @param user            l'utilisateur concerné, pour le calcul des points
     * @return la liste des cinq attractions les plus proches
     */
    public List<AttractionNearbyUserDto> getNearByAttractions(VisitedLocation visitedLocation, User user) {
        return gpsUtil.getAttractions().stream()
            .map(attraction -> Map.entry(
                attraction,
                rewardsService.getDistance(attraction, visitedLocation.location)))
            .sorted(Comparator.comparingDouble(Map.Entry::getValue))
            .limit(5)
            .map(entry -> new AttractionNearbyUserDto(
                entry.getKey(),
                visitedLocation,
                rewardsService.getRewardPoints(entry.getKey(), user),
                entry.getValue()))
            .toList();
    }

    // Enregistre un hook d'arrêt de la JVM qui stoppe proprement le tracker.
    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                tracker.stopTracking();
            }
        });
    }

    // Database connection will be used for external users, but for testing purposes
    // internal users are provided and stored in memory
    private final Map<String, User> internalUserMap = new HashMap<>();

    /**
     * Calcule et renvoie au moins dix offres de voyage adaptées à l'utilisateur.
     *
     * <p><b>Exemple :</b> agrège les points de récompense de l'utilisateur puis interroge
     * TripPricer jusqu'à réunir dix fournisseurs distincts.</p>
     *
     * @param user l'utilisateur pour lequel rechercher des offres
     * @return la liste des offres de voyage retenues
     */
    public List<Provider> getTripDeals(User user) {
        int cumulativeRewardPoints = user.getUserRewards().stream()
            .mapToInt(UserReward::getRewardPoints)
            .sum();
        Set<Provider> providers = new HashSet<>();

        // On garantit au moins dix fournisseurs, quitte à rappeler TripPricer plusieurs fois.
        int attempts = 0;
        while (providers.size() < 10
            && attempts < 200) {
            attempts++;
            List<Provider> recupProviders = tripPricer.getPrice(
                tripPricerApiKey,
                user.getUserId(),
                user.getUserPreferences().getNumberOfAdults(), user.getUserPreferences().getNumberOfChildren(),
                user.getUserPreferences().getTripDuration(), cumulativeRewardPoints);
            for (Provider provider : recupProviders) {
                if (providers.size() < 10) {
                    providers.add(provider);
                }
            }
        }

        List<Provider> tripDeals = new ArrayList<>(providers);
        user.setTripDeals(tripDeals);
        return tripDeals;
    }

    // Génère en mémoire les utilisateurs internes de test avec un historique de positions.
    private void initializeInternalUsers() {
        IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
            String userName = "internalUser" + i;
            String phone = "000";
            String email = userName + "@tourGuide.com";
            User user = new User(UUID.randomUUID(), userName, phone, email);
            generateUserLocationHistory(user);

            internalUserMap.put(userName, user);
        });
        logger.debug("Created {} internal test users.", InternalTestHelper.getInternalUserNumber());
    }

    // Ajoute trois positions aléatoires à l'historique de l'utilisateur.
    private void generateUserLocationHistory(User user) {
        IntStream.range(0, 3).forEach(i -> {
            user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
                    new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
        });
    }

    // Tire une longitude aléatoire dans l'intervalle valide [-180, 180].
    private double generateRandomLongitude() {
        double leftLimit = -180;
        double rightLimit = 180;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    // Tire une latitude aléatoire dans l'intervalle valide de la projection Web Mercator.
    private double generateRandomLatitude() {
        double leftLimit = -85.05112878;
        double rightLimit = 85.05112878;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    // Tire un horodatage aléatoire au cours des trente derniers jours.
    private Date getRandomTime() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }

    /**
     * Suit en parallèle la localisation d'une liste d'utilisateurs.
     *
     * <p><b>Exemple :</b> appelée par le tracker pour recalculer d'un seul coup les
     * positions de tous les utilisateurs sur un pool de threads dédié.</p>
     *
     * @param users les utilisateurs à localiser
     */
    public void trackListUsersLocations(List<User> users) {
        ParallelTaskExecutor.runInParallel(users, this::trackUserLocation, THREAD_POOL_SIZE, AWAIT_TERMINATION_MINUTES);
    }

}
