package com.openclassrooms.tourguide.dto;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

public class AttractionNearbyUserDto {
  private String attractionName;
  private double attractionLatitude;
  private double attractionLongitude;
  private double userLatitude;
  private double userLongitude;
  private double distance;
  private int rewardPoints;

  public AttractionNearbyUserDto() {
  }

  public AttractionNearbyUserDto(Attraction attraction,
                                 VisitedLocation visitedLocation,
                                 int rewardPoints,
                                 double distance) {
    this.attractionName = attraction.attractionName;
    this.attractionLatitude = attraction.latitude;
    this.attractionLongitude = attraction.longitude;
    this.userLatitude = visitedLocation.location.latitude;
    this.userLongitude = visitedLocation.location.longitude;
    this.distance = distance;
    this.rewardPoints = rewardPoints;
  }

  public String getAttractionName() {
    return attractionName;
  }

  public void setAttractionName(String attractionName) {
    this.attractionName = attractionName;
  }

  public double getAttractionLatitude() {
    return attractionLatitude;
  }

  public void setAttractionLatitude(double attractionLatitude) {
    this.attractionLatitude = attractionLatitude;
  }

  public double getAttractionLongitude() {
    return attractionLongitude;
  }

  public void setAttractionLongitude(double attractionLongitude) {
    this.attractionLongitude = attractionLongitude;
  }

  public double getUserLatitude() {
    return userLatitude;
  }

  public void setUserLatitude(double userLatitude) {
    this.userLatitude = userLatitude;
  }

  public double getUserLongitude() {
    return userLongitude;
  }

  public void setUserLongitude(double userLongitude) {
    this.userLongitude = userLongitude;
  }

  public double getDistance() {
    return distance;
  }

  public void setDistance(double distance) {
    this.distance = distance;
  }

  public int getRewardPoints() {
    return rewardPoints;
  }

  public void setRewardPoints(int rewardPoints) {
    this.rewardPoints = rewardPoints;
  }

  @Override
  public String toString() {
    return "AttractionNearbyUserDto{" +
            "attractionName='" + attractionName + '\'' +
            ", attractionLatitude=" + attractionLatitude +
            ", attractionLongitude=" + attractionLongitude +
            ", userLatitude=" + userLatitude +
            ", userLongitude=" + userLongitude +
            ", distance=" + distance +
            ", rewardPoints=" + rewardPoints +
            '}';
  }
}
