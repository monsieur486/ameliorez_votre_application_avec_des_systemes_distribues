package com.openclassrooms.tourguide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée de l'application Spring Boot TourGuide.
 *
 * <p><b>Exemple :</b> lancée via {@code java -jar tourguide.jar}, elle démarre le
 * serveur web et expose les endpoints du guide touristique.</p>
 */
@SpringBootApplication
public class TourguideApplication {

    /**
     * Démarre l'application Spring Boot TourGuide.
     *
     * <p><b>Exemple :</b> {@code java -jar tourguide.jar} lance le contexte Spring
     * et met le serveur en écoute des requêtes HTTP.</p>
     *
     * @param args les arguments de ligne de commande transmis à Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication.run(TourguideApplication.class, args);
    }

}
