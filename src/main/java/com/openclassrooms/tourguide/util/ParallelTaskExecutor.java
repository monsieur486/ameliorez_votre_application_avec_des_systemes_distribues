package com.openclassrooms.tourguide.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Exécute une même action sur chaque élément d'une liste, en parallèle, sur un
 * pool de threads dédié <b>réutilisable</b> détenu par l'instance.
 *
 * <p>Le pool est créé une seule fois à la construction et réemployé à chaque
 * appel de {@link #runInParallel(List, Consumer)} : il n'est plus recréé à
 * chaque cycle de traitement. Les threads sont des <i>daemons</i>, si bien qu'un
 * pool non fermé ne bloque jamais l'arrêt de la JVM. L'appel à {@link #close()}
 * arrête proprement le pool.</p>
 *
 * <p>Chaque élément est traité en isolation : une exception levée pour un élément
 * est journalisée puis ignorée, sans interrompre le traitement des autres.</p>
 *
 * <p><b>Exemple :</b>
 * {@code try (var executor = new ParallelTaskExecutor(200, 15)) {
 * executor.runInParallel(users, this::trackUserLocation); }}</p>
 */
public final class ParallelTaskExecutor implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParallelTaskExecutor.class);

    private final ExecutorService pool;
    private final long awaitMinutes;

    /**
     * Crée un exécuteur détenant un pool de threads fixe réutilisable.
     *
     * <p><b>Exemple :</b> {@code new ParallelTaskExecutor(200, 15)} crée un pool de
     * 200 threads daemon, arrêté avec un délai de grâce de 15 minutes.</p>
     *
     * @param poolSize     la taille du pool de threads
     * @param awaitMinutes le délai maximal d'attente (minutes) à la fermeture avant arrêt forcé
     */
    public ParallelTaskExecutor(int poolSize, long awaitMinutes) {
        this.awaitMinutes = awaitMinutes;
        this.pool = Executors.newFixedThreadPool(poolSize, daemonThreadFactory());
    }

    /**
     * Applique {@code action} à chacun des {@code items} en parallèle, puis bloque
     * jusqu'à la fin de toutes les tâches. Le pool est conservé pour les appels suivants.
     *
     * <p><b>Exemple :</b>
     * {@code executor.runInParallel(users, this::trackUserLocation);}</p>
     *
     * @param items  les éléments à traiter (liste éventuellement vide)
     * @param action l'action appliquée à chaque élément
     * @param <T>    le type des éléments traités
     */
    public <T> void runInParallel(List<T> items, Consumer<T> action) {
        CompletableFuture<?>[] futures = items.stream()
            .map(item -> CompletableFuture.runAsync(() -> appliquerEnIsolant(action, item), pool))
            .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(futures).join();
    }

    /**
     * Arrête proprement le pool : attente de la fin des tâches, puis arrêt forcé au-delà du délai.
     *
     * <p><b>Exemple :</b> appelé en fin de {@code try-with-resources} ou par le hook d'arrêt
     * de l'application pour libérer les threads du pool.</p>
     */
    @Override
    public void close() {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(awaitMinutes, TimeUnit.MINUTES)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // Applique l'action à un élément en isolant son éventuelle erreur du reste du lot.
    private static <T> void appliquerEnIsolant(Consumer<T> action, T item) {
        try {
            action.accept(item);
        } catch (RuntimeException e) {
            LOGGER.error("Échec du traitement parallèle de l'élément {}", item, e);
        }
    }

    // Fabrique des threads daemon pour que le pool ne retienne jamais la JVM à l'arrêt.
    private static ThreadFactory daemonThreadFactory() {
        return runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        };
    }
}
