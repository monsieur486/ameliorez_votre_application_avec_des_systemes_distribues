package com.openclassrooms.tourguide.service;

import com.openclassrooms.tourguide.TourGuideConfiguration;
import com.openclassrooms.tourguide.dto.AttractionNearbyUserDto;
import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.tracker.Tracker;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;
import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
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
  boolean testMode = true;
  private final Logger logger = LoggerFactory.getLogger(TourGuideService.class);

  // Executor service for parallel tracking of user locations
  // Using a fixed thread pool to limit the number of concurrent threads
  private final ExecutorService executor = Executors.newFixedThreadPool(
          TourGuideConfiguration.DEFAULT_TOUR_GUIDE_SERVICE_NUMBER_OF_THREADS);

  public TourGuideService(GpsUtil gpsUtil, RewardsService rewardsService) {
    this.gpsUtil = gpsUtil;
    this.rewardsService = rewardsService;

    Locale.setDefault(Locale.US);

    if (TourGuideConfiguration.IS_TEST_MODE) {
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
    VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ? user.getLastVisitedLocation()
            : trackUserLocation(user);
    return visitedLocation;
  }

  public User getUser(String userName) {
    return internalUserMap.get(userName);
  }

  public List<User> getAllUsers() {
    return internalUserMap.values().stream().collect(Collectors.toList());
  }

  public void addUser(User user) {
    if (!internalUserMap.containsKey(user.getUserName())) {
      internalUserMap.put(user.getUserName(), user);
    }
  }

  public List<Provider> getTripDeals(User user) {
    int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
    Set<Provider> providers = new HashSet<>();

    // Ensure we have at least 10 providers, even if it means calling the TripPricer multiple times
    while (providers.size() < 10) {
      List<Provider> recupProviders = tripPricer.getPrice(tripPricerApiKey, user.getUserId(),
              user.getUserPreferences().getNumberOfAdults(), user.getUserPreferences().getNumberOfChildren(),
              user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
      for (Provider provider : recupProviders) {
        if (providers.size() < 10) {
          providers.add(provider);
        }
      }
    }

    return new ArrayList<>(providers);
  }

  public VisitedLocation trackUserLocation(User user) {
    VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
    user.addToVisitedLocations(visitedLocation);
    rewardsService.calculateRewards(user);
    return visitedLocation;
  }

  // Track the location of a list of users in parallel
  public void trackListUsersLocations(List<User> users) {
    List<CompletableFuture<Void>> futures = users.stream()
            .map(user -> CompletableFuture.runAsync(() -> trackUserLocation(user), executor))
            .toList();

    CompletableFuture<Void> allDone = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    try {
      allDone.get();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Parallel tracking interrupted", e);
    } catch (ExecutionException e) {
      throw new RuntimeException("Exception during parallel tracking", e);
    }
  }

  @PreDestroy
  public void shutdownExecutor() {
    executor.shutdown();
  }

  public List<AttractionNearbyUserDto> getNearByAttractions(VisitedLocation visitedLocation, User user) {
    List<AttractionNearbyUserDto> nearbyAttractions = new ArrayList<>();
    for (Attraction attraction : gpsUtil.getAttractions()) {
      if (nearbyAttractions.size() < TourGuideConfiguration.NEAR_BY_ATTRACTION_NUMBER) {
        int rewardPoints = rewardsService.getRewardPoints(attraction, user);
        double distance = rewardsService.getDistance(attraction, visitedLocation.location);
        AttractionNearbyUserDto attractionNearbyUserDto = new AttractionNearbyUserDto(attraction, visitedLocation, rewardPoints, distance);
        nearbyAttractions.add(attractionNearbyUserDto);
      }
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

}
