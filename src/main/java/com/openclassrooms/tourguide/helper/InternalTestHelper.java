package com.openclassrooms.tourguide.helper;

/**
 * Aide de configuration fixant le nombre d'utilisateurs internes générés pour les tests.
 *
 * <p><b>Exemple :</b> {@code InternalTestHelper.setInternalUserNumber(100000)} permet de
 * générer cent mille utilisateurs pour un test de performance.</p>
 */
public class InternalTestHelper {

    // Set this default up to 100,000 for testing
    private static int internalUserNumber = 100;

    /**
     * Définit le nombre d'utilisateurs internes à générer.
     *
     * <p><b>Exemple :</b> {@code setInternalUserNumber(100)} configure la génération de
     * cent utilisateurs de test au prochain démarrage.</p>
     *
     * @param internalUserNumber le nombre d'utilisateurs internes à générer
     */
    public static void setInternalUserNumber(int internalUserNumber) {
        InternalTestHelper.internalUserNumber = internalUserNumber;
    }

    /**
     * Renvoie le nombre d'utilisateurs internes à générer.
     *
     * <p><b>Exemple :</b> {@code getInternalUserNumber()} renvoie 100 tant que la valeur
     * par défaut n'a pas été modifiée.</p>
     *
     * @return le nombre d'utilisateurs internes configuré
     */
    public static int getInternalUserNumber() {
        return internalUserNumber;
    }
}
