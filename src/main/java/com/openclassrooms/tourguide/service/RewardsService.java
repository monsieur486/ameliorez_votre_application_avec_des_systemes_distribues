package com.openclassrooms.tourguide.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.openclassrooms.tourguide.configuration.ApplicationConfiguration;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;

@Service
public class RewardsService {

	private final ExecutorService executor = Executors.newFixedThreadPool(
					ApplicationConfiguration.REWARDS_PARALLEL_THREAD_NUMBER);

	// proximity in miles
	private final int defaultProximityBuffer = ApplicationConfiguration.DEFAULT_PROXIMITY_BUFFER;
	private int proximityBuffer = defaultProximityBuffer;
  private final GpsUtil gpsUtil;
	private final RewardCentral rewardsCentral;
	
	public RewardsService(GpsUtil gpsUtil, RewardCentral rewardCentral) {
		this.gpsUtil = gpsUtil;
		this.rewardsCentral = rewardCentral;
	}
	
	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}
	
	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}

	public void calculateRewards(User user) {
		List<VisitedLocation> userLocations;
		synchronized (user.getVisitedLocations()) {
			userLocations = new ArrayList<>(user.getVisitedLocations());
		}

		List<Attraction> attractions = gpsUtil.getAttractions();

		Set<String> alreadyRewarded;
		synchronized (user.getUserRewards()) {
			alreadyRewarded = user.getUserRewards().stream()
							.map(r -> r.getAttraction().attractionName)
							.collect(Collectors.toSet());
		}

		for (VisitedLocation visitedLocation : userLocations) {
			for (Attraction attraction : attractions) {
				if (!alreadyRewarded.contains(attraction.attractionName) && nearAttraction(visitedLocation, attraction)) {
					user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
				}
			}
		}
	}

	public void calculateRewardsParallel(List<User> users) {
		List<Callable<Void>> tasks = users.stream()
				.map(user -> (Callable<Void>) () -> {
					calculateRewards(user);
					return null;
				})
				.collect(Collectors.toList());

		try {
			executor.invokeAll(tasks);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Error calculating rewards in parallel", e);
		}

	}

	@PreDestroy
	public void shutdownExecutor() {
		executor.shutdown();
	}
	
	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
    int attractionProximityRange = ApplicationConfiguration.ATTRACTION_PROXIMITY_RANGE;
    return !(getDistance(attraction, location) > attractionProximityRange);
	}
	
	private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return !(getDistance(attraction, visitedLocation.location) > proximityBuffer);
	}
	
	private int getRewardPoints(Attraction attraction, User user) {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}
	
	public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
    return ApplicationConfiguration.STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
	}

}
