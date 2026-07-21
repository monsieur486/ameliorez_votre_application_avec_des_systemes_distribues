package com.openclassrooms.tourguide.exception;

/**
 * Exception levée lorsqu'aucun utilisateur ne correspond au nom recherché.
 *
 * <p><b>Exemple :</b> getUser("inconnu") lève UserNotFoundException, traduite en 404 par le
 * GlobalExceptionHandler.</p>
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Construit l'exception avec le nom d'utilisateur introuvable.
     *
     * <p><b>Exemple :</b> new UserNotFoundException("jon").</p>
     *
     * @param userName le nom d'utilisateur introuvable
     */
    public UserNotFoundException(String userName) {
        super("Utilisateur introuvable : " + userName);
    }
}
