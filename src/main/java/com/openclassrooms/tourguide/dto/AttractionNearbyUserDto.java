package com.openclassrooms.tourguide.dto;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

/**
 * Représentation, exposée par l'API, d'une attraction proche d'un utilisateur :
 * nom et coordonnées de l'attraction, coordonnées de l'utilisateur, distance en
 * miles et points de récompense.
 *
 * <p><b>Exemple :</b> sérialisé en JSON sous la forme
 * {@code {"attractionName":"Disneyland","distance":12.3,"rewardPoints":100,...}}.</p>
 *
 * @param attractionName      nom de l'attraction
 * @param attractionLatitude  latitude de l'attraction
 * @param attractionLongitude longitude de l'attraction
 * @param userLatitude        latitude de l'utilisateur
 * @param userLongitude       longitude de l'utilisateur
 * @param distance            distance en miles entre l'utilisateur et l'attraction
 * @param rewardPoints        points de récompense pour la visite de l'attraction
 */
public record AttractionNearbyUserDto(
        String attractionName,
        double attractionLatitude,
        double attractionLongitude,
        double userLatitude,
        double userLongitude,
        double distance,
        int rewardPoints) {

  /**
   * Construit le DTO à partir des objets du domaine.
   *
   * <p><b>Exemple :</b> {@code new AttractionNearbyUserDto(attraction, localisation, 100, 12.3)}
   * recopie les coordonnées de l'attraction et de la localisation.</p>
   *
   * @param attraction      attraction proche
   * @param visitedLocation localisation courante de l'utilisateur
   * @param rewardPoints    points de récompense de l'attraction
   * @param distance        distance en miles entre l'utilisateur et l'attraction
   */
  public AttractionNearbyUserDto(Attraction attraction,
                                 VisitedLocation visitedLocation,
                                 int rewardPoints,
                                 double distance) {
    this(attraction.attractionName,
            attraction.latitude,
            attraction.longitude,
            visitedLocation.location.latitude,
            visitedLocation.location.longitude,
            distance,
            rewardPoints);
  }
}
