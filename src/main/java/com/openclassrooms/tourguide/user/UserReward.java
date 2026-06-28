package com.openclassrooms.tourguide.user;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

/**
 * Récompense gagnée par un utilisateur pour avoir été à proximité d'une attraction :
 * localisation visitée, attraction concernée et nombre de points attribués.
 *
 * <p><b>Exemple :</b> {@code new UserReward(localisation, attraction, 100)} mémorise
 * 100 points pour la visite de l'attraction.</p>
 */
public class UserReward {

  public final VisitedLocation visitedLocation;
  public final Attraction attraction;
  private final int rewardPoints;

  /**
   * Construit une récompense.
   *
   * <p><b>Exemple :</b> {@code new UserReward(localisation, attraction, 100)}.</p>
   *
   * @param visitedLocation localisation visitée ayant déclenché la récompense
   * @param attraction      attraction récompensée
   * @param rewardPoints    nombre de points attribués
   */
  public UserReward(VisitedLocation visitedLocation, Attraction attraction, int rewardPoints) {
    this.visitedLocation = visitedLocation;
    this.attraction = attraction;
    this.rewardPoints = rewardPoints;
  }

  public int getRewardPoints() {
    return rewardPoints;
  }

}
