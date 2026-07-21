package com.openclassrooms.tourguide.dto;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

/**
 * Attraction proche d'un utilisateur, enrichie de ses coordonnées, de celles de
 * l'utilisateur, de la distance et des points de récompense.
 *
 * <p><b>Exemple :</b> renvoyé par getNearbyAttractions pour chacune des cinq attractions
 * les plus proches.</p>
 *
 * @param attractionName      nom de l'attraction
 * @param attractionLatitude  latitude de l'attraction
 * @param attractionLongitude longitude de l'attraction
 * @param userLatitude        latitude de l'utilisateur
 * @param userLongitude       longitude de l'utilisateur
 * @param distance            distance en miles entre l'utilisateur et l'attraction
 * @param rewardPoints        points de récompense pour la visite
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
     * Construit le DTO à partir des objets métier.
     *
     * <p><b>Exemple :</b> new AttractionNearbyUserDto(attraction, visitedLocation, 100, 12.5).</p>
     *
     * @param attraction      l'attraction concernée
     * @param visitedLocation la localisation de l'utilisateur
     * @param rewardPoints    les points de récompense
     * @param distance        la distance en miles
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
