package com.openclassrooms.tourguide.service;

import com.openclassrooms.tourguide.user.User;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Tâche de fond suivant périodiquement la localisation de tous les utilisateurs.
 *
 * <p><b>Exemple :</b> démarrée à la construction du {@link TourGuideService}, elle
 * relance un suivi toutes les cinq minutes jusqu'à l'appel de
 * {@link #stopTracking()}.</p>
 */
public class Tracker extends Thread {
  private static final long TRACKING_POLLING_INTERVAL = TimeUnit.MINUTES.toSeconds(5);
  private static final Logger LOGGER = LoggerFactory.getLogger(Tracker.class);

  private final ExecutorService executorService = Executors.newSingleThreadExecutor();
  private final TourGuideService tourGuideService;
  private volatile boolean stop;

  /**
   * Construit le tracker et démarre immédiatement le suivi périodique.
   *
   * <p><b>Exemple :</b> {@code new Tracker(tourGuideService)}.</p>
   *
   * @param tourGuideService service utilisé pour suivre les utilisateurs
   */
  public Tracker(TourGuideService tourGuideService) {
    this.tourGuideService = tourGuideService;
    executorService.submit(this);
  }

  /**
   * Arrête définitivement le thread de suivi.
   *
   * <p><b>Exemple :</b> {@code tracker.stopTracking()} interrompt la boucle et
   * libère le thread de fond.</p>
   */
  public void stopTracking() {
    stop = true;
    executorService.shutdownNow();
  }

  @Override
  public void run() {
    StopWatch stopWatch = new StopWatch();
    while (true) {
      if (Thread.currentThread().isInterrupted() || stop) {
        LOGGER.debug("arrêt du tracker");
        break;
      }

      List<User> users = tourGuideService.getAllUsers();
      LOGGER.debug("début du suivi de {} utilisateurs", users.size());
      stopWatch.start();
      tourGuideService.trackListUsersLocations(users);
      stopWatch.stop();
      LOGGER.debug("suivi terminé en {} secondes pour {} utilisateurs",
              TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()), users.size());
      stopWatch.reset();
      try {
        LOGGER.debug("tracker en sommeil");
        TimeUnit.SECONDS.sleep(TRACKING_POLLING_INTERVAL);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      }
    }
  }
}
