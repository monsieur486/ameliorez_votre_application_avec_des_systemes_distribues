package com.openclassrooms.tourguide.service;

import com.openclassrooms.tourguide.configuration.ApplicationConfiguation;
import com.openclassrooms.tourguide.dto.AttractionNearbyUserDto;
import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.tracker.Tracker;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;
import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

@Service
public class TourGuideService {
  /**********************************************************************************
   *
   * Methods Below: For Internal Testing
   *
   **********************************************************************************/
  private static final String tripPricerApiKey = "test-server-api-key";
  public final Tracker tracker;
  private final GpsUtil gpsUtil;
  private final RewardsService rewardsService;
  private final TripPricer tripPricer = new TripPricer();
  // Database connection will be used for external users, but for testing purposes
  // internal users are provided and stored in memory
  private final Map<String, User> internalUserMap = new HashMap<>();
  private final Logger logger = LoggerFactory.getLogger(TourGuideService.class);
  boolean testMode = ApplicationConfiguation.TEST_MODE;

  public TourGuideService(GpsUtil gpsUtil, RewardsService rewardsService) {
    this.gpsUtil = gpsUtil;
    this.rewardsService = rewardsService;

    Locale.setDefault(Locale.US);

    if (testMode) {
      logger.info("TestMode enabled");
      logger.debug("Initializing users");
      initializeInternalUsers();
      logger.debug("Finished initializing users");
    }
    tracker = new Tracker(this);
    addShutDownHook();
  }

  public List<UserReward> getUserRewards(User user) {
    return user.getUserRewards();
  }

  public VisitedLocation getUserLocation(User user) {
    return (!user.getVisitedLocations().isEmpty()) ? user.getLastVisitedLocation()
            : trackUserLocation(user);
  }

  public User getUser(String userName) {
    return internalUserMap.get(userName);
  }

  public User getUserById(UUID userId) {
    return internalUserMap.values().stream().filter(user -> user.getUserId().equals(userId)).findFirst().orElse(null);
  }

  public List<User> getAllUsers() {
    return new ArrayList<>(internalUserMap.values());
  }

  public void addUser(User user) {
    if (!internalUserMap.containsKey(user.getUserName())) {
      internalUserMap.put(user.getUserName(), user);
    }
  }

  public List<Provider> getTripDeals(User user) {
    int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(UserReward::getRewardPoints).sum();
    List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(),
            user.getUserPreferences().getNumberOfAdults(), user.getUserPreferences().getNumberOfChildren(),
            user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
    user.setTripDeals(providers);
    return providers;
  }

  public VisitedLocation trackUserLocation(User user) {
    VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
    user.addToVisitedLocations(visitedLocation);
    rewardsService.calculateRewards(user);
    return visitedLocation;
  }

  public List<AttractionNearbyUserDto> getNearByAttractions(VisitedLocation visitedLocation, User user) {
    List<AttractionNearbyUserDto> nearbyAttractions = new ArrayList<>();
    for (Attraction attraction : gpsUtil.getAttractions()) {
      int rewardPoints = rewardsService.getRewardPoints(attraction, user);
      double distance = rewardsService.getDistance(attraction, visitedLocation.location);
      AttractionNearbyUserDto attractionNearbyUserDto = new AttractionNearbyUserDto(attraction, visitedLocation, rewardPoints, distance);
      nearbyAttractions.add(attractionNearbyUserDto);
    }

    nearbyAttractions.sort(Comparator.comparing(AttractionNearbyUserDto::getDistance));

    if (nearbyAttractions.size() > 5) {
      nearbyAttractions.sort(Comparator.comparing(AttractionNearbyUserDto::getDistance));
      nearbyAttractions = nearbyAttractions.subList(0, 5);
    }

    return nearbyAttractions;
  }

  private void addShutDownHook() {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        tracker.stopTracking();
      }
    });
  }

  private void initializeInternalUsers() {
    IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
      String userName = "internalUser" + i;
      String phone = "000";
      String email = userName + "@tourGuide.com";
      User user = new User(UUID.randomUUID(), userName, phone, email);
      generateUserLocationHistory(user);

      internalUserMap.put(userName, user);
    });
    logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
  }

  private void generateUserLocationHistory(User user) {
    IntStream.range(0, 3).forEach(i -> {
      user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
              new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
    });
  }

  private double generateRandomLongitude() {
    double leftLimit = -180;
    double rightLimit = 180;
    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
  }

  private double generateRandomLatitude() {
    double leftLimit = -85.05112878;
    double rightLimit = 85.05112878;
    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
  }

  private Date getRandomTime() {
    LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
    return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
  }

  public void parallelTrackAllUsersLocation(List<User> allUsers) {
    ForkJoinPool customThreadPool = new ForkJoinPool(ApplicationConfiguation.MAX_THREAD_TRACK);

    customThreadPool.submit(() -> {
      allUsers.stream().parallel().forEach(this::trackUserLocation);
    }).join();

    customThreadPool.shutdown();
  }

}
