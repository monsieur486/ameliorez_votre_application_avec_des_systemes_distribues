package com.openclassrooms.tourguide.service;

import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;
import com.openclassrooms.tourguide.util.ParallelTaskExecutor;
import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.springframework.stereotype.Service;
import rewardCentral.RewardCentral;

import java.util.List;

/**
 * Service de calcul des récompenses liées aux attractions visitées par les utilisateurs.
 *
 * <p><b>Exemple :</b> pour chaque attraction située dans le rayon de proximité d'une
 * position visitée, attribue une récompense à l'utilisateur.</p>
 */
@Service
public class RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

    // proximity in miles
    private final int defaultProximityBuffer = 10;
    private int proximityBuffer = defaultProximityBuffer;
    private final GpsUtil gpsUtil;
    private final RewardCentral rewardsCentral;

    /**
     * Taille du pool de threads utilisé pour le calcul parallèle des récompenses.
     */
    private static final int THREAD_POOL_SIZE = 200;
    /**
     * Délai maximal d'attente de la fin des tâches avant arrêt forcé du pool (minutes).
     */
    private static final long AWAIT_TERMINATION_MINUTES = 20;

    /**
     * Construit le service avec ses collaborateurs de localisation et de récompenses.
     *
     * @param gpsUtil       le fournisseur de localisation GPS
     * @param rewardCentral le fournisseur de points de récompense
     */
    public RewardsService(GpsUtil gpsUtil, RewardCentral rewardCentral) {
        this.gpsUtil = gpsUtil;
        this.rewardsCentral = rewardCentral;
    }

    /**
     * Définit le rayon de proximité (en miles) utilisé pour l'attribution des récompenses.
     *
     * <p><b>Exemple :</b> {@code setProximityBuffer(Integer.MAX_VALUE)} rend toutes les
     * attractions éligibles quelle que soit la distance.</p>
     *
     * @param proximityBuffer le rayon de proximité en miles
     */
    public void setProximityBuffer(int proximityBuffer) {
        this.proximityBuffer = proximityBuffer;
    }

    /**
     * Réinitialise le rayon de proximité à sa valeur par défaut.
     *
     * <p><b>Exemple :</b> appelée après un test ayant élargi le rayon pour revenir aux
     * dix miles par défaut.</p>
     */
    public void setDefaultProximityBuffer() {
        proximityBuffer = defaultProximityBuffer;
    }

    /**
     * Attribue à l'utilisateur une récompense pour chaque attraction proche non encore récompensée.
     *
     * <p><b>Exemple :</b> parcourt les positions visitées et ajoute une récompense pour
     * chaque attraction située dans le rayon de proximité.</p>
     *
     * @param user l'utilisateur dont les récompenses sont calculées
     */
    public void calculateRewards(User user) {
        List<VisitedLocation> userLocations = user.getVisitedLocations();
        List<Attraction> attractions = gpsUtil.getAttractions();

        for (VisitedLocation visitedLocation : userLocations) {
            for(Attraction attraction : attractions) {
                if (user.getUserRewards().stream().noneMatch(
                    r -> r.attraction.attractionName.equals(attraction.attractionName))) {
                    if(nearAttraction(visitedLocation, attraction)) {
                        user.addUserReward(
                            new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user))
                        );
                    }
                }
            }
        }
    }

    /**
     * Calcule en parallèle les récompenses d'une liste d'utilisateurs.
     *
     * <p><b>Exemple :</b> traite d'un seul coup tous les utilisateurs sur un pool de
     * threads dédié pour accélérer le calcul de masse.</p>
     *
     * @param users les utilisateurs dont les récompenses sont calculées
     */
    public void calculateRewardsForListUsers(List<User> users) {
        ParallelTaskExecutor.runInParallel(users, this::calculateRewards, THREAD_POOL_SIZE, AWAIT_TERMINATION_MINUTES);
    }

    /**
     * Indique si une localisation se trouve dans le rayon d'influence d'une attraction.
     *
     * <p><b>Exemple :</b> renvoie {@code true} si la distance entre la localisation et
     * l'attraction est inférieure ou égale à 200 miles.</p>
     *
     * @param attraction l'attraction de référence
     * @param location   la localisation à tester
     * @return {@code true} si la localisation est dans le rayon d'influence, sinon {@code false}
     */
    public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
        int attractionProximityRange = 200;
        return !(getDistance(attraction, location) > attractionProximityRange);
    }

    // Indique si une position visitée est dans le rayon de proximité de l'attraction.
    private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
        return !(getDistance(attraction, visitedLocation.location) > proximityBuffer);
    }

    /**
     * Renvoie le nombre de points de récompense d'une attraction pour un utilisateur.
     *
     * @param attraction l'attraction concernée
     * @param user       l'utilisateur concerné
     * @return le nombre de points attribués
     */
    int getRewardPoints(Attraction attraction, User user) {
        return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
    }

    /**
     * Calcule la distance en miles entre deux localisations géographiques.
     *
     * <p><b>Exemple :</b> {@code getDistance(paris, londres)} renvoie la distance
     * orthodromique entre les deux points en miles terrestres.</p>
     *
     * @param loc1 la première localisation
     * @param loc2 la seconde localisation
     * @return la distance en miles entre les deux localisations
     */
    public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        return STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
    }

}
