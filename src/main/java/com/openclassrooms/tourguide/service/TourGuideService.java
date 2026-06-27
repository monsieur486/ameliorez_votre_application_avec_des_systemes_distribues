package com.openclassrooms.tourguide.service;

import com.openclassrooms.tourguide.TourGuideConfiguration;
import com.openclassrooms.tourguide.dto.AttractionNearbyUserDto;
import com.openclassrooms.tourguide.exception.ParallelProcessingException;
import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;
import gpsUtil.GpsUtil;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import jakarta.annotation.PreDestroy;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * Service principal de TourGuide : suivi de la localisation des utilisateurs,
 * attractions proches et offres de voyage. En mode test, une base d'utilisateurs
 * internes est générée en mémoire.
 *
 * <p><b>Exemple :</b> {@code getUserLocation(user)} retourne la dernière
 * localisation connue, ou la calcule si l'historique est vide.</p>
 */
@Service
public class TourGuideService {

  private static final String TRIP_PRICER_API_KEY = "test-server-api-key";
  private static final int TRIP_DEALS_TARGET = 10;
  private static final Logger LOGGER = LoggerFactory.getLogger(TourGuideService.class);

  public final Tracker tracker;
  private final GpsUtil gpsUtil;
  private final RewardsService rewardsService;
  private final TripPricer tripPricer = new TripPricer();
  private final Random random = new Random();
  // Les utilisateurs externes viendraient d'une base ; en test, ils sont en mémoire.
  private final Map<String, User> internalUserMap = new HashMap<>();

  // Pool de threads borné pour le suivi parallèle des localisations.
  private final ExecutorService executor = Executors.newFixedThreadPool(
          TourGuideConfiguration.DEFAULT_TOUR_GUIDE_SERVICE_NUMBER_OF_THREADS);

  /**
   * Construit le service, initialise les utilisateurs internes en mode test et
   * démarre le suivi périodique.
   *
   * <p><b>Exemple :</b> {@code new TourGuideService(new GpsUtil(), rewardsService)}.</p>
   *
   * @param gpsUtil        fournisseur des localisations et attractions
   * @param rewardsService service de calcul des récompenses
   */
  public TourGuideService(GpsUtil gpsUtil, RewardsService rewardsService) {
    this.gpsUtil = gpsUtil;
    this.rewardsService = rewardsService;

    Locale.setDefault(Locale.US);

    if (TourGuideConfiguration.IS_TEST_MODE) {
      LOGGER.info("mode test activé");
      initializeInternalUsers();
    }
    tracker = new Tracker(this);
    addShutDownHook();
  }

  /**
   * Retourne les récompenses d'un utilisateur.
   *
   * <p><b>Exemple :</b> {@code getUserRewards(user)} retourne la liste, vide si
   * aucune récompense n'a encore été attribuée.</p>
   *
   * @param user utilisateur concerné
   * @return la liste des récompenses
   */
  public List<UserReward> getUserRewards(User user) {
    return user.getUserRewards();
  }

  /**
   * Retourne la localisation courante d'un utilisateur : la dernière connue, ou
   * une nouvelle localisation suivie si l'historique est vide.
   *
   * <p><b>Exemple :</b> pour un nouvel utilisateur, déclenche un suivi et retourne
   * la localisation obtenue.</p>
   *
   * @param user utilisateur concerné
   * @return la localisation courante
   */
  public VisitedLocation getUserLocation(User user) {
    return user.getVisitedLocations().isEmpty()
            ? trackUserLocation(user)
            : user.getLastVisitedLocation();
  }

  /**
   * Retourne un utilisateur par son nom.
   *
   * <p><b>Exemple :</b> {@code getUser("jon")} retourne l'utilisateur « jon », ou
   * {@code null} s'il est inconnu.</p>
   *
   * @param userName nom de l'utilisateur recherché
   * @return l'utilisateur correspondant, ou {@code null}
   */
  public User getUser(String userName) {
    return internalUserMap.get(userName);
  }

  /**
   * Retourne tous les utilisateurs connus.
   *
   * <p><b>Exemple :</b> {@code getAllUsers()} retourne la liste des utilisateurs
   * internes en mode test.</p>
   *
   * @return la liste des utilisateurs
   */
  public List<User> getAllUsers() {
    return new ArrayList<>(internalUserMap.values());
  }

  /**
   * Ajoute un utilisateur s'il n'est pas déjà présent.
   *
   * <p><b>Exemple :</b> un second ajout du même nom d'utilisateur est ignoré.</p>
   *
   * @param user utilisateur à ajouter
   */
  public void addUser(User user) {
    if (!internalUserMap.containsKey(user.getUserName())) {
      internalUserMap.put(user.getUserName(), user);
    }
  }

  /**
   * Retourne les offres de voyage d'un utilisateur (au moins dix fournisseurs).
   *
   * <p><b>Exemple :</b> {@code getTripDeals(user)} retourne dix offres en
   * appelant TripPricer autant de fois que nécessaire.</p>
   *
   * @param user utilisateur concerné
   * @return la liste des offres
   */
  public List<Provider> getTripDeals(User user) {
    int cumulativeRewardPoints = user.getUserRewards().stream()
            .mapToInt(UserReward::getRewardPoints)
            .sum();
    Set<Provider> providers = new HashSet<>();

    // On garantit au moins dix fournisseurs, quitte à rappeler TripPricer plusieurs fois.
    int attempts = 0;
    while (providers.size() < TRIP_DEALS_TARGET && attempts < TourGuideConfiguration.MAX_TRIP_DEALS_ATTEMPTS) {
      attempts++;
      List<Provider> recupProviders = tripPricer.getPrice(TRIP_PRICER_API_KEY, user.getUserId(),
              user.getUserPreferences().getNumberOfAdults(), user.getUserPreferences().getNumberOfChildren(),
              user.getUserPreferences().getTripDuration(), cumulativeRewardPoints);
      for (Provider provider : recupProviders) {
        if (providers.size() < TRIP_DEALS_TARGET) {
          providers.add(provider);
        }
      }
    }

    return new ArrayList<>(providers);
  }

  /**
   * Suit la localisation d'un utilisateur, l'ajoute à son historique et calcule
   * ses récompenses.
   *
   * <p><b>Exemple :</b> {@code trackUserLocation(user)} retourne la localisation
   * fraîchement obtenue auprès de GpsUtil.</p>
   *
   * @param user utilisateur à suivre
   * @return la localisation suivie
   */
  public VisitedLocation trackUserLocation(User user) {
    VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
    user.addToVisitedLocations(visitedLocation);
    rewardsService.calculateRewards(user);
    return visitedLocation;
  }

  /**
   * Suit en parallèle la localisation d'une liste d'utilisateurs.
   *
   * <p><b>Exemple :</b> {@code trackListUsersLocations(users)} traite tous les
   * utilisateurs via un pool de threads borné et attend la fin du traitement.</p>
   *
   * @param users utilisateurs à suivre
   * @throws ParallelProcessingException si le suivi échoue ou est interrompu
   */
  public void trackListUsersLocations(List<User> users) {
    LOGGER.info("suivi parallèle de {} utilisateurs", users.size());
    List<CompletableFuture<Void>> futures = users.stream()
            .map(user -> CompletableFuture.runAsync(() -> trackUserLocation(user), executor))
            .toList();

    CompletableFuture<Void> allDone = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    try {
      allDone.get();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ParallelProcessingException("suivi parallèle interrompu", e);
    } catch (ExecutionException e) {
      LOGGER.error("échec du suivi parallèle des localisations", e);
      throw new ParallelProcessingException("échec du suivi parallèle des localisations", e);
    }
  }

  /**
   * Arrête proprement le pool de threads à la destruction du bean.
   *
   * <p><b>Exemple :</b> appelé automatiquement par Spring à l'arrêt du contexte.</p>
   */
  @PreDestroy
  public void shutdownExecutor() {
    executor.shutdown();
  }

  /**
   * Retourne les cinq attractions les plus proches de l'utilisateur, triées par
   * distance croissante, avec leurs points de récompense.
   *
   * <p><b>Exemple :</b> {@code getNearByAttractions(localisation, user)} retourne
   * cinq attractions, quelle que soit leur distance réelle.</p>
   *
   * @param visitedLocation localisation courante de l'utilisateur
   * @param user            utilisateur concerné
   * @return les cinq attractions les plus proches
   */
  public List<AttractionNearbyUserDto> getNearByAttractions(VisitedLocation visitedLocation, User user) {
    // On trie toutes les attractions par distance croissante (calcul peu coûteux),
    // on garde les N plus proches, puis on récupère les points pour ces N seulement.
    return gpsUtil.getAttractions().stream()
            .sorted(Comparator.comparingDouble(
                    attraction -> rewardsService.getDistance(attraction, visitedLocation.location)))
            .limit(TourGuideConfiguration.NEAR_BY_ATTRACTION_NUMBER)
            .map(attraction -> new AttractionNearbyUserDto(
                    attraction,
                    visitedLocation,
                    rewardsService.getRewardPoints(attraction, user),
                    rewardsService.getDistance(attraction, visitedLocation.location)))
            .toList();
  }

  // Enregistre un hook d'arrêt JVM qui stoppe le suivi périodique.
  private void addShutDownHook() {
    Runtime.getRuntime().addShutdownHook(new Thread(tracker::stopTracking));
  }

  // Génère les utilisateurs internes de test avec un historique de localisations.
  private void initializeInternalUsers() {
    IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
      String userName = "internalUser" + i;
      String phone = "000";
      String email = userName + "@tourGuide.com";
      User user = new User(UUID.randomUUID(), userName, phone, email);
      generateUserLocationHistory(user);

      internalUserMap.put(userName, user);
    });
    LOGGER.debug("{} utilisateurs internes de test créés", InternalTestHelper.getInternalUserNumber());
  }

  // Ajoute trois localisations aléatoires à l'historique d'un utilisateur.
  private void generateUserLocationHistory(User user) {
    IntStream.range(0, 3).forEach(i ->
            user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
                    new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime())));
  }

  // Tire une longitude aléatoire valide.
  private double generateRandomLongitude() {
    double leftLimit = -180;
    double rightLimit = 180;
    return leftLimit + random.nextDouble() * (rightLimit - leftLimit);
  }

  // Tire une latitude aléatoire valide.
  private double generateRandomLatitude() {
    double leftLimit = -85.05112878;
    double rightLimit = 85.05112878;
    return leftLimit + random.nextDouble() * (rightLimit - leftLimit);
  }

  // Tire un instant aléatoire dans les trente derniers jours.
  private Date getRandomTime() {
    LocalDateTime localDateTime = LocalDateTime.now().minusDays(random.nextInt(30));
    return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
  }

}
