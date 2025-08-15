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

  // Adds a visited location to the user's list of visited locations
  // This method is synchronized to ensure thread safety when adding locations
  // It uses a CopyOnWriteArrayList to allow safe concurrent modifications
  public void addToVisitedLocations(VisitedLocation visitedLocation) {
     {
      // Update the latest location timestamp to the current time
      this.latestLocationTimestamp = new Date();
      // Add the visited location to the list of visited locations
      // This ensures that the user's visited locations are always up-to-date
      visitedLocations.add(visitedLocation);
    }
  }

  // Returns a copy of the list of visited locations to prevent external modification
  // This method provides a safe way to access the user's visited locations
  public List<VisitedLocation> getVisitedLocations() {
    return List.copyOf(visitedLocations);
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

  // Returns a copy of the list of user rewards to prevent external modification
  // This method provides a safe way to access the user's rewards
  public List<UserReward> getUserRewards() {
    return List.copyOf(userRewards);
  }

  public UserPreferences getUserPreferences() {
    return userPreferences;
  }

  public void setUserPreferences(UserPreferences userPreferences) {
    this.userPreferences = userPreferences;
  }

  public VisitedLocation getLastVisitedLocation() {
    // Returns the last visited location of the user
    // If the user has no visited locations, return null
    if (visitedLocations.isEmpty()) {
      return null;
    }
    return visitedLocations.get(visitedLocations.size() - 1);
  }

  public List<Provider> getTripDeals() {
    return tripDeals;
  }

  public void setTripDeals(List<Provider> tripDeals) {
    this.tripDeals = tripDeals;
  }

}
