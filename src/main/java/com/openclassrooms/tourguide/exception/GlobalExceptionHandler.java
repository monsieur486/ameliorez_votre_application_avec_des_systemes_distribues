package com.openclassrooms.tourguide.exception;

import com.openclassrooms.tourguide.dto.ApiError;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Gestionnaire global des exceptions de l'API : traduit les erreurs en réponses HTTP
 * uniformes (corps ApiError).
 *
 * <p><b>Exemple :</b> une ConstraintViolationException est traduite en réponse 400.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Traduit une ressource utilisateur introuvable en réponse 404.
     *
     * <p><b>Exemple :</b> un nom d'utilisateur inconnu produit un corps ApiError au statut 404.</p>
     *
     * @param exception l'exception levée
     * @param request   la requête à l'origine de l'erreur
     * @return la réponse 404 au format ApiError
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(UserNotFoundException exception,
                                                       HttpServletRequest request) {
        logger.warn("Utilisateur introuvable sur {} : {}", request.getRequestURI(), exception.getMessage());
        return build(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    /**
     * Traduit un paramètre invalide (contrainte de validation violée) en réponse 400.
     *
     * <p><b>Exemple :</b> un userName vide produit un corps ApiError au statut 400.</p>
     *
     * @param exception l'exception levée
     * @param request   la requête à l'origine de l'erreur
     * @return la réponse 400 au format ApiError
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException exception,
                                                              HttpServletRequest request) {
        logger.warn("Paramètre invalide sur {} : {}", request.getRequestURI(), exception.getMessage());
        return build(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }

    /**
     * Traduit un paramètre de requête manquant en réponse 400.
     *
     * <p><b>Exemple :</b> l'absence du paramètre userName produit un corps ApiError 400.</p>
     *
     * @param exception l'exception levée
     * @param request   la requête à l'origine de l'erreur
     * @return la réponse 400 au format ApiError
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParameter(MissingServletRequestParameterException exception,
                                                           HttpServletRequest request) {
        logger.warn("Paramètre manquant sur {} : {}", request.getRequestURI(), exception.getMessage());
        return build(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }

    /**
     * Traduit toute erreur inattendue en réponse 500, sans divulguer de détail technique.
     *
     * <p><b>Exemple :</b> une exception non prévue produit un corps ApiError 500 au message
     * neutre.</p>
     *
     * @param exception l'exception levée
     * @param request   la requête à l'origine de l'erreur
     * @return la réponse 500 au format ApiError
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception exception, HttpServletRequest request) {
        logger.error("Erreur interne sur {}", request.getRequestURI(), exception);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur interne est survenue.", request);
    }

    // Construit la réponse HTTP uniforme (ApiError) pour un statut et un message donnés.
    private ResponseEntity<ApiError> build(HttpStatus status, String message, HttpServletRequest request) {
        ApiError body = new ApiError(status.value(), status.getReasonPhrase(), message, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
