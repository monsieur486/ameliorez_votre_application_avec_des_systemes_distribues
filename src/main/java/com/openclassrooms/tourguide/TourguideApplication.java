package com.openclassrooms.tourguide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée de l'application Spring Boot TourGuide.
 *
 * <p><b>Exemple :</b> {@code java -jar tourguide.jar} démarre le serveur web et
 * le suivi périodique des utilisateurs.</p>
 */
@SpringBootApplication
public class TourguideApplication {

  /**
   * Démarre l'application.
   *
   * <p><b>Exemple :</b> appelé par la JVM au lancement du jar.</p>
   *
   * @param args arguments de ligne de commande
   */
  public static void main(String[] args) {
    SpringApplication.run(TourguideApplication.class, args);
  }

}
