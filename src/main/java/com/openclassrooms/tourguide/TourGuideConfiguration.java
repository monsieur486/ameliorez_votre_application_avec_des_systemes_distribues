package com.openclassrooms.tourguide;

/**
 * Constantes de configuration de TourGuide (mode test, seuils de proximité,
 * tailles de pools de threads, plafonds). Classe utilitaire non instanciable.
 *
 * <p><b>Exemple :</b> {@code TourGuideConfiguration.NEAR_BY_ATTRACTION_NUMBER}
 * vaut 5 (nombre d'attractions proches retournées).</p>
 */
public final class TourGuideConfiguration {

  /** Active la génération d'utilisateurs internes en mémoire (mode test). */
  public static final boolean IS_TEST_MODE = true;
  /** Nombre d'utilisateurs internes générés (jusqu'à 100 000 pour les tests de charge). */
  public static final int NUMBER_OF_USERS = 100;
  /** Rayon de proximité par défaut (miles) pour attribuer une récompense. */
  public static final int DEFAULT_PROXIMITY_BUFFER = 10;
  /** Portée maximale (miles) pour considérer une attraction « à proximité ». */
  public static final int DEFAULT_PROXIMITY_RANGE = 200;
  /** Nombre d'attractions proches retournées par l'API. */
  public static final int NEAR_BY_ATTRACTION_NUMBER = 5;
  /** Taille du pool de threads du service de récompenses. */
  public static final int DEFAULT_REWARDS_SERVICE_NUMBER_OF_THREADS = 200;
  /** Taille du pool de threads du service principal. */
  public static final int DEFAULT_TOUR_GUIDE_SERVICE_NUMBER_OF_THREADS = 200;
  /** Nombre maximal d'appels à TripPricer pour atteindre dix offres. */
  public static final int MAX_TRIP_DEALS_ATTEMPTS = 100;

  private TourGuideConfiguration() {
    // Classe utilitaire : instanciation interdite.
  }

}
