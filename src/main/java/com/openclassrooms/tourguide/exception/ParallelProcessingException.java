package com.openclassrooms.tourguide.exception;

/**
 * Exception non vérifiée signalant l'échec d'un traitement exécuté en parallèle
 * (suivi de localisation ou calcul de récompenses sur une liste d'utilisateurs).
 *
 * <p><b>Exemple :</b> si une tâche asynchrone lève une exception, l'agrégation
 * {@code CompletableFuture.allOf(...).get()} la propage, et le service la
 * convertit en {@code ParallelProcessingException} pour ne jamais l'avaler.</p>
 */
public class ParallelProcessingException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Construit l'exception avec un message et la cause d'origine.
   *
   * <p><b>Exemple :</b> {@code new ParallelProcessingException("échec du suivi", cause)}
   * conserve la trace de la cause pour le diagnostic.</p>
   *
   * @param message message décrivant le traitement en échec
   * @param cause   exception d'origine ayant provoqué l'échec
   */
  public ParallelProcessingException(String message, Throwable cause) {
    super(message, cause);
  }
}
