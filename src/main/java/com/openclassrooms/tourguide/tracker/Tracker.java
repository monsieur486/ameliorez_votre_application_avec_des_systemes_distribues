package com.openclassrooms.tourguide.tracker;

import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.user.User;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Thread de fond qui suit périodiquement la localisation de tous les utilisateurs.
 *
 * <p><b>Exemple :</b> démarré à la création du TourGuideService, il recalcule les
 * positions toutes les cinq minutes jusqu'à son arrêt.</p>
 */
public class Tracker extends Thread {
    private final Logger logger = LoggerFactory.getLogger(Tracker.class);
    private static final long trackingPollingInterval = TimeUnit.MINUTES.toSeconds(5);
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final TourGuideService tourGuideService;
    private boolean stop = false;

    public Tracker(TourGuideService tourGuideService) {
        this.tourGuideService = tourGuideService;

        executorService.submit(this);
    }

    /**
     * Arrête définitivement le thread de suivi.
     *
     * <p><b>Exemple :</b> appelé par le shutdown hook de l'application pour interrompre
     * le suivi avant la fermeture du contexte Spring.</p>
     */
    public void stopTracking() {
        stop = true;
        executorService.shutdownNow();
    }

    /**
     * Exécute la boucle de suivi : recalcule les positions puis attend l'intervalle.
     *
     * <p><b>Exemple :</b> tourne en continu et journalise le temps écoulé à chaque
     * cycle de suivi jusqu'à réception d'un signal d'arrêt.</p>
     */
    @Override
    public void run() {
        StopWatch stopWatch = new StopWatch();
        while (true) {
            if (Thread.currentThread().isInterrupted() || stop) {
                logger.debug("Tracker stopping");
                break;
            }

            List<User> users = tourGuideService.getAllUsers();
            logger.debug("Begin Tracker. Tracking {} users.", users.size());
            stopWatch.start();
            tourGuideService.trackListUsersLocations(users);
            stopWatch.stop();
            logger.debug("Tracker Time Elapsed: {} seconds.", TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
            stopWatch.reset();
            try {
                logger.debug("Tracker sleeping");
                TimeUnit.SECONDS.sleep(trackingPollingInterval);
            } catch (InterruptedException e) {
                break;
            }
        }

    }
}
