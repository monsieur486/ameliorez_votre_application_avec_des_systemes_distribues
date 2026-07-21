package com.openclassrooms.tourguide.dto;

/**
 * Corps d'erreur uniforme renvoyé par l'API en cas d'exception.
 *
 * <p><b>Exemple :</b> {@code {"status":404,"error":"Not Found","message":"Utilisateur
 * introuvable : jon","path":"/getLocation"}}.</p>
 *
 * @param status  code HTTP de la réponse
 * @param error   libellé du statut HTTP
 * @param message message décrivant l'erreur
 * @param path    chemin de la requête à l'origine de l'erreur
 */
public record ApiError(
        int status,
        String error,
        String message,
        String path) {
}
