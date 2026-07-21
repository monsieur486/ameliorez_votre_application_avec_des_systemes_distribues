package com.openclassrooms.tourguide.util;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Exécute une même action sur chaque élément d'une liste, en parallèle,
 * sur un pool de threads dédié qui est ensuite arrêté proprement.
 *
 * <p>Cet utilitaire factorise le motif « pool fixe + {@code runAsync} par
 * élément + attente globale + arrêt » partagé par les traitements de masse
 * (suivi de localisation, calcul de récompenses).</p>
 */
public final class ParallelTaskExecutor {

    // Classe utilitaire : pas d'instanciation.
    private ParallelTaskExecutor() {
    }

    /**
     * Applique {@code action} à chacun des {@code items} en parallèle, puis
     * bloque jusqu'à la fin de toutes les tâches avant d'arrêter le pool.
     *
     * <p><b>Exemple :</b>
     * {@code ParallelTaskExecutor.runInParallel(users, this::trackUserLocation, 200, 15);}</p>
     *
     * @param items        les éléments à traiter (liste éventuellement vide)
     * @param action       l'action appliquée à chaque élément
     * @param poolSize      la taille du pool de threads
     * @param awaitMinutes le délai maximal d'attente (minutes) avant arrêt forcé
     * @param <T>          le type des éléments traités
     */
    public static <T> void runInParallel(List<T> items, Consumer<T> action, int poolSize, long awaitMinutes) {
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        try {
            CompletableFuture<?>[] futures = items.stream()
                .map(item -> CompletableFuture.runAsync(() -> action.accept(item), executor))
                .toArray(CompletableFuture[]::new);
            CompletableFuture.allOf(futures).join();
        } finally {
            shutdownAndAwait(executor, awaitMinutes);
        }
    }

    // Arrête proprement le pool : attente de la fin des tâches, puis arrêt forcé au-delà du délai.
    private static void shutdownAndAwait(ExecutorService executor, long awaitMinutes) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(awaitMinutes, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
