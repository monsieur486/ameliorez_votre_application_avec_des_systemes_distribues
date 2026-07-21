package com.openclassrooms.tourguide.user;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

/**
 * Récompense obtenue par un utilisateur pour la visite d'une attraction.
 *
 * <p><b>Exemple :</b> associe une position visitée à une attraction et au nombre de
 * points de récompense attribués.</p>
 */
public class UserReward {

    public final VisitedLocation visitedLocation;
    public final Attraction attraction;
    private int rewardPoints;

    /**
     * Construit une récompense avec ses points.
     *
     * @param visitedLocation la localisation visitée à l'origine de la récompense
     * @param attraction      l'attraction récompensée
     * @param rewardPoints    le nombre de points attribués
     */
    public UserReward(VisitedLocation visitedLocation, Attraction attraction, int rewardPoints) {
        this.visitedLocation = visitedLocation;
        this.attraction = attraction;
        this.rewardPoints = rewardPoints;
    }

    /**
     * Construit une récompense sans points attribués.
     *
     * @param visitedLocation la localisation visitée à l'origine de la récompense
     * @param attraction      l'attraction récompensée
     */
    public UserReward(VisitedLocation visitedLocation, Attraction attraction) {
        this.visitedLocation = visitedLocation;
        this.attraction = attraction;
    }

    /**
     * Définit le nombre de points de récompense.
     *
     * @param rewardPoints le nombre de points à affecter
     */
    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    /**
     * Renvoie le nombre de points de récompense.
     *
     * @return le nombre de points de récompense
     */
    public int getRewardPoints() {
        return rewardPoints;
    }

}
