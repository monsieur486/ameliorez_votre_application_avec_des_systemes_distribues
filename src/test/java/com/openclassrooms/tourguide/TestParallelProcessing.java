package com.openclassrooms.tourguide;

import com.openclassrooms.tourguide.exception.ParallelProcessingException;
import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.service.RewardsService;
import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.user.User;
import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.Test;
import rewardCentral.RewardCentral;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests des traitements parallèles : suivi de localisations et calcul de
 * récompenses sur une liste d'utilisateurs, chemins nominal et d'erreur.
 */
public class TestParallelProcessing {

  @Test
  public void trackListUsersLocations() {
    GpsUtil gpsUtil = new GpsUtil();
    RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
    InternalTestHelper.setInternalUserNumber(0);
    TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

    List<User> users = List.of(
            new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com"),
            new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com"));

    tourGuideService.trackListUsersLocations(users);
    tourGuideService.tracker.stopTracking();

    users.forEach(user -> assertFalse(user.getVisitedLocations().isEmpty()));
  }

  @Test
  public void calculateRewardsListUsers() {
    GpsUtil gpsUtil = new GpsUtil();
    RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
    rewardsService.setProximityBuffer(Integer.MAX_VALUE);
    InternalTestHelper.setInternalUserNumber(0);
    TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

    Attraction attraction = gpsUtil.getAttractions().get(0);
    List<User> users = List.of(
            new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com"),
            new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com"));
    users.forEach(u -> u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new Date())));

    rewardsService.calculateRewardsListUsers(users);
    tourGuideService.tracker.stopTracking();

    users.forEach(user -> assertFalse(user.getUserRewards().isEmpty()));
  }

  @Test
  public void trackListUsersLocationsPropageLEchec() {
    GpsUtil gpsUtil = new GpsUtil();
    RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
    InternalTestHelper.setInternalUserNumber(0);
    TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

    List<User> usersAvecNull = new ArrayList<>();
    usersAvecNull.add(null);

    assertThrows(ParallelProcessingException.class,
            () -> tourGuideService.trackListUsersLocations(usersAvecNull));
    tourGuideService.tracker.stopTracking();
  }

  @Test
  public void calculateRewardsListUsersPropageLEchec() {
    GpsUtil gpsUtil = new GpsUtil();
    RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

    assertThrows(ParallelProcessingException.class,
            () -> rewardsService.calculateRewardsListUsers(Collections.singletonList(null)));
  }

  @Test
  public void calculateRewardsListUsersListeVide() {
    GpsUtil gpsUtil = new GpsUtil();
    RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

    rewardsService.calculateRewardsListUsers(Collections.emptyList());

    assertTrue(true, "un traitement parallèle sur liste vide ne lève rien");
  }
}
