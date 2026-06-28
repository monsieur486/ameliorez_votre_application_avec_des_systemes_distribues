package com.openclassrooms.tourguide.helper;

import com.openclassrooms.tourguide.TourGuideConfiguration;

/**
 * Aide de test contrôlant le nombre d'utilisateurs internes générés en mémoire.
 *
 * <p><b>Exemple :</b> {@code InternalTestHelper.setInternalUserNumber(0)} évite de
 * générer des utilisateurs pour un test unitaire ciblé.</p>
 */
public final class InternalTestHelper {

  // Valeur ajustable jusqu'à 100 000 pour les tests de charge.
  private static int internalUserNumber = TourGuideConfiguration.NUMBER_OF_USERS;

  private InternalTestHelper() {
    // Classe utilitaire : instanciation interdite.
  }

  /**
   * Retourne le nombre d'utilisateurs internes à générer.
   *
   * <p><b>Exemple :</b> {@code getInternalUserNumber()} retourne la valeur
   * courante.</p>
   *
   * @return le nombre d'utilisateurs internes
   */
  public static int getInternalUserNumber() {
    return internalUserNumber;
  }

  /**
   * Définit le nombre d'utilisateurs internes à générer.
   *
   * <p><b>Exemple :</b> {@code setInternalUserNumber(100)} génère cent
   * utilisateurs.</p>
   *
   * @param internalUserNumber nombre d'utilisateurs internes
   */
  public static void setInternalUserNumber(int internalUserNumber) {
    InternalTestHelper.internalUserNumber = internalUserNumber;
  }
}
