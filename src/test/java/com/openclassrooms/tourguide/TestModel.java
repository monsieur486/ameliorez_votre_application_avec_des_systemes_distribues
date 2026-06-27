package com.openclassrooms.tourguide;

import com.openclassrooms.tourguide.dto.AttractionNearbyUserDto;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserPreferences;
import com.openclassrooms.tourguide.user.UserReward;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests des objets du modèle et du DTO : accesseurs, mutateurs et construction.
 */
public class TestModel {

  @Test
  public void userAccesseursEtMutateurs() {
    UUID id = UUID.randomUUID();
    User user = new User(id, "jon", "000", "jon@tourGuide.com");

    user.setPhoneNumber("111");
    user.setEmailAddress("jon2@tourGuide.com");
    UserPreferences preferences = new UserPreferences();
    user.setUserPreferences(preferences);

    assertEquals(id, user.getUserId());
    assertEquals("jon", user.getUserName());
    assertEquals("111", user.getPhoneNumber());
    assertEquals("jon2@tourGuide.com", user.getEmailAddress());
    assertEquals(preferences, user.getUserPreferences());
  }

  @Test
  public void userPreferencesMutateurs() {
    UserPreferences preferences = new UserPreferences();
    preferences.setTripDuration(7);
    preferences.setNumberOfAdults(2);
    preferences.setNumberOfChildren(3);

    assertEquals(7, preferences.getTripDuration());
    assertEquals(2, preferences.getNumberOfAdults());
    assertEquals(3, preferences.getNumberOfChildren());
  }

  @Test
  public void userRewardConservePoints() {
    Location location = new Location(10.0, 20.0);
    VisitedLocation visitedLocation = new VisitedLocation(UUID.randomUUID(), location, new Date());
    Attraction attraction = new Attraction("Disneyland", "Anaheim", "CA", 33.8, -117.9);

    UserReward reward = new UserReward(visitedLocation, attraction, 100);

    assertEquals(100, reward.getRewardPoints());
    assertEquals(attraction, reward.attraction);
    assertEquals(visitedLocation, reward.visitedLocation);
  }

  @Test
  public void attractionNearbyUserDtoExposeLesChamps() {
    Attraction attraction = new Attraction("Disneyland", "Anaheim", "CA", 33.8, -117.9);
    Location location = new Location(40.0, -75.0);
    VisitedLocation visitedLocation = new VisitedLocation(UUID.randomUUID(), location, new Date());

    AttractionNearbyUserDto dto = new AttractionNearbyUserDto(attraction, visitedLocation, 100, 12.3);

    assertEquals(attraction.attractionName, dto.attractionName());
    assertEquals(attraction.latitude, dto.attractionLatitude());
    assertEquals(attraction.longitude, dto.attractionLongitude());
    assertEquals(location.latitude, dto.userLatitude());
    assertEquals(location.longitude, dto.userLongitude());
    assertEquals(12.3, dto.distance());
    assertEquals(100, dto.rewardPoints());
  }
}
