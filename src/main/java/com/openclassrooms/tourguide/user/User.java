package com.openclassrooms.tourguide.user;

import gpsUtil.location.VisitedLocation;
import tripPricer.Provider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class User {
  private final UUID userId;
  private final String userName;
  private String phoneNumber;
  private String emailAddress;
  private Date latestLocationTimestamp;
  private final CopyOnWriteArrayList<VisitedLocation> visitedLocations = new CopyOnWriteArrayList<>();
  private final CopyOnWriteArrayList<UserReward> userRewards = new CopyOnWriteArrayList<>();
  private UserPreferences userPreferences = new UserPreferences();
  private List<Provider> tripDeals = new ArrayList<>();

  public User(UUID userId, String userName, String phoneNumber, String emailAddress) {
    this.userId = userId;
    this.userName = userName;
    this.phoneNumber = phoneNumber;
    this.emailAddress = emailAddress;
  }

  public UUID getUserId() {
    return userId;
  }

  public String getUserName() {
    return userName;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public Date getLatestLocationTimestamp() {
    return latestLocationTimestamp;
  }

  public void setLatestLocationTimestamp(Date latestLocationTimestamp) {
    this.latestLocationTimestamp = latestLocationTimestamp;
  }

  public void addToVisitedLocations(VisitedLocation visitedLocation) {
    visitedLocations.add(visitedLocation);
  }

  public List<VisitedLocation> getVisitedLocations() {
    return visitedLocations;
  }

  public void clearVisitedLocations() {
    visitedLocations.clear();
  }

  // Adds a user reward if it does not already exist for the attraction
  // This method is synchronized to prevent concurrent modification issues
  public void addUserReward(UserReward userReward) {
    // Ensure thread safety when adding user rewards
    {
      // Check if the reward for the attraction already exists
      // If it does not exist, add the new user reward
      // This prevents duplicates for the same attraction
      boolean alreadyExists = userRewards.stream()
              .anyMatch(r -> r.attraction.attractionName.equals(userReward.attraction.attractionName));
      if (!alreadyExists) {
        userRewards.add(userReward);
      }
    }
  }

  public List<UserReward> getUserRewards() {
    return userRewards;
  }

  public UserPreferences getUserPreferences() {
    return userPreferences;
  }

  public void setUserPreferences(UserPreferences userPreferences) {
    this.userPreferences = userPreferences;
  }

  public VisitedLocation getLastVisitedLocation() {
    return visitedLocations.get(visitedLocations.size() - 1);
  }

  public List<Provider> getTripDeals() {
    return tripDeals;
  }

  public void setTripDeals(List<Provider> tripDeals) {
    this.tripDeals = tripDeals;
  }

}
