package com.openclassrooms.tourguide.service;

import com.openclassrooms.tourguide.TourGuideConfiguration;
import com.openclassrooms.tourguide.exception.ParallelProcessingException;
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
import rewardCentral.RewardCentral;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Service de calcul des récompenses : détermine, pour chaque utilisateur, les
 * attractions proches de ses localisations visitées et leur attribue des points.
 *
 * <p><b>Exemple :</b> {@code calculateRewards(user)} ajoute une récompense par
 * attraction située dans le rayon de proximité d'une localisation visitée.</p>
 */
@Service
public class RewardsService {
  private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
  private static final Logger LOGGER = LoggerFactory.getLogger(RewardsService.class);

  private final GpsUtil gpsUtil;
  private final RewardCentral rewardsCentral;
  // Rayon de proximité (en miles) en deçà duquel une attraction est récompensée.
  private int proximityBuffer = TourGuideConfiguration.DEFAULT_PROXIMITY_BUFFER;

  // Pool de threads borné pour le calcul parallèle des récompenses.
  private final ExecutorService executor = Executors.newFixedThreadPool(
          TourGuideConfiguration.DEFAULT_REWARDS_SERVICE_NUMBER_OF_THREADS);

  /**
   * Construit le service avec ses dépendances externes.
   *
   * <p><b>Exemple :</b> {@code new RewardsService(new GpsUtil(), new RewardCentral())}.</p>
   *
   * @param gpsUtil       fournisseur des attractions et des localisations
   * @param rewardCentral fournisseur des points de récompense
   */
  public RewardsService(GpsUtil gpsUtil, RewardCentral rewardCentral) {
    this.gpsUtil = gpsUtil;
    this.rewardsCentral = rewardCentral;
  }

  /**
   * Définit le rayon de proximité utilisé pour attribuer les récompenses.
   *
   * <p><b>Exemple :</b> {@code setProximityBuffer(Integer.MAX_VALUE)} fait qu'une
   * attraction est toujours considérée comme proche.</p>
   *
   * @param proximityBuffer rayon de proximité en miles
   */
  public void setProximityBuffer(int proximityBuffer) {
    this.proximityBuffer = proximityBuffer;
  }

  /**
   * Calcule et ajoute les récompenses d'un utilisateur pour chaque attraction
   * proche d'une de ses localisations visitées, sans doublon par attraction.
   *
   * <p><b>Exemple :</b> un utilisateur ayant visité un lieu à moins de 10 miles
   * d'une attraction obtient une récompense pour cette attraction.</p>
   *
   * @param user utilisateur dont on calcule les récompenses
   */
  public void calculateRewards(User user) {
    List<VisitedLocation> userLocations = user.getVisitedLocations();
    List<Attraction> attractions = gpsUtil.getAttractions();

    // On mémorise dans un Set les attractions déjà récompensées (recherche O(1)),
    // plutôt que de re-parcourir toutes les récompenses pour chaque paire.
    Set<String> rewardedAttractionNames = user.getUserRewards().stream()
            .map(r -> r.attraction.attractionName)
            .collect(Collectors.toCollection(HashSet::new));

    for (VisitedLocation visitedLocation : userLocations) {
      for (Attraction attraction : attractions) {
        if (!rewardedAttractionNames.contains(attraction.attractionName)
                && nearAttraction(visitedLocation, attraction)) {
          user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
          rewardedAttractionNames.add(attraction.attractionName);
        }
      }
    }
    LOGGER.debug("récompenses calculées pour l'utilisateur {}", user.getUserName());
  }

  /**
   * Calcule en parallèle les récompenses d'une liste d'utilisateurs.
   *
   * <p><b>Exemple :</b> {@code calculateRewardsListUsers(users)} traite tous les
   * utilisateurs via un pool de threads borné et attend la fin de tous les calculs.</p>
   *
   * @param users utilisateurs à traiter
   * @throws ParallelProcessingException si un calcul échoue ou est interrompu
   */
  public void calculateRewardsListUsers(List<User> users) {
    LOGGER.info("calcul des récompenses pour {} utilisateurs", users.size());
    List<CompletableFuture<Void>> futures = users.stream()
            .map(user -> CompletableFuture.runAsync(() -> calculateRewards(user), executor))
            .toList();

    CompletableFuture<Void> allDone = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    try {
      allDone.get();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ParallelProcessingException("calcul des récompenses interrompu", e);
    } catch (ExecutionException e) {
      LOGGER.error("échec du calcul parallèle des récompenses", e);
      throw new ParallelProcessingException("échec du calcul parallèle des récompenses", e);
    }
  }

  /**
   * Arrête proprement le pool de threads à la destruction du bean.
   *
   * <p><b>Exemple :</b> appelé automatiquement par Spring lors de l'arrêt du
   * contexte applicatif.</p>
   */
  @PreDestroy
  public void shutdownExecutor() {
    executor.shutdown();
  }

  /**
   * Indique si une attraction est dans le rayon de proximité d'une localisation.
   *
   * <p><b>Exemple :</b> {@code isWithinAttractionProximity(attraction, attraction)}
   * retourne {@code true} (distance nulle).</p>
   *
   * @param attraction attraction évaluée
   * @param location   localisation de référence
   * @return {@code true} si la distance est inférieure ou égale au rayon
   */
  public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
    return getDistance(attraction, location) <= TourGuideConfiguration.DEFAULT_PROXIMITY_RANGE;
  }

  // Indique si une attraction est assez proche d'une localisation visitée pour être récompensée.
  private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
    return getDistance(attraction, visitedLocation.location) <= proximityBuffer;
  }

  /**
   * Retourne le nombre de points de récompense d'une attraction pour un utilisateur.
   *
   * <p><b>Exemple :</b> {@code getRewardPoints(attraction, user)} interroge
   * RewardCentral et retourne les points associés.</p>
   *
   * @param attraction attraction concernée
   * @param user       utilisateur concerné
   * @return le nombre de points de récompense
   */
  public int getRewardPoints(Attraction attraction, User user) {
    return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
  }

  /**
   * Calcule la distance en miles entre deux localisations (formule orthodromique).
   *
   * <p><b>Exemple :</b> {@code getDistance(loc, loc)} retourne {@code 0.0}.</p>
   *
   * @param loc1 première localisation
   * @param loc2 seconde localisation
   * @return la distance en miles
   */
  public double getDistance(Location loc1, Location loc2) {
    double lat1 = Math.toRadians(loc1.latitude);
    double lon1 = Math.toRadians(loc1.longitude);
    double lat2 = Math.toRadians(loc2.latitude);
    double lon2 = Math.toRadians(loc2.longitude);

    double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
            + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

    double nauticalMiles = 60 * Math.toDegrees(angle);
    return STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
  }

}
