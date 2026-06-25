package com.openclassrooms.tourguide;

public class TourGuideConfiguration {
  private TourGuideConfiguration() {
    /* This utility class should not be instantiated */
  }

  public static final boolean IS_TEST_MODE = true;
  public static final int NUMBER_OF_USERS = 100; // 100.000 pour tests complets
  public static final int DEFAULT_PROXIMITY_BUFFER = 10;
  public static final int DEFAULT_PROXIMITY_RANGE = 200;

  public static final int NEAR_BY_ATTRACTION_NUMBER = 5;

  public static final int DEFAULT_REWARDS_SERVICE_NUMBER_OF_THREADS = 200;
  public static final int DEFAULT_TOUR_GUIDE_SERVICE_NUMBER_OF_THREADS = 200;

  public static final int MAX_TRIP_DEALS_ATTEMPTS = 100;

}
