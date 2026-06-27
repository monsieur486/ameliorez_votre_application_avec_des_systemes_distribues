package com.openclassrooms.tourguide;

import com.openclassrooms.tourguide.dto.AttractionNearbyUserDto;
import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.service.RewardsService;
import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.user.UserReward;
import gpsUtil.GpsUtil;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rewardCentral.RewardCentral;
import tripPricer.Provider;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests du contrôleur REST {@link TourGuideController}, vérifiant que chaque
 * endpoint délègue correctement au service.
 */
public class TestTourGuideController {

  private TourGuideService tourGuideService;
  private TourGuideController controller;

  @BeforeEach
  public void setUp() {
    GpsUtil gpsUtil = new GpsUtil();
    RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
    InternalTestHelper.setInternalUserNumber(1);
    tourGuideService = new TourGuideService(gpsUtil, rewardsService);
    controller = new TourGuideController(tourGuideService);
  }

  @AfterEach
  public void tearDown() {
    tourGuideService.tracker.stopTracking();
  }

  @Test
  public void index() {
    assertEquals("Greetings from TourGuide!", controller.index());
  }

  @Test
  public void getLocation() {
    VisitedLocation location = controller.getLocation("internalUser0");
    assertNotNull(location);
    assertNotNull(location.location);
  }

  @Test
  public void getNearbyAttractions() {
    List<AttractionNearbyUserDto> attractions = controller.getNearbyAttractions("internalUser0");
    assertEquals(5, attractions.size());
  }

  @Test
  public void getRewards() {
    List<UserReward> rewards = controller.getRewards("internalUser0");
    assertNotNull(rewards);
  }

  @Test
  public void getTripDeals() {
    List<Provider> providers = controller.getTripDeals("internalUser0");
    assertEquals(10, providers.size());
  }
}
