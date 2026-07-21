package com.openclassrooms.tourguide.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParallelTaskExecutorTest {

    @Test
    void runInParallel_appliqueLActionAChaqueElement() {
        List<Integer> items = IntStream.rangeClosed(1, 1000).boxed().toList();
        Set<Integer> traites = ConcurrentHashMap.newKeySet();

        try (ParallelTaskExecutor executor = new ParallelTaskExecutor(50, 1)) {
            executor.runInParallel(items, traites::add);
        }

        assertEquals(1000, traites.size());
    }

    @Test
    void runInParallel_listeVide_neFaitRien() {
        AtomicInteger compteur = new AtomicInteger();

        try (ParallelTaskExecutor executor = new ParallelTaskExecutor(10, 1)) {
            executor.runInParallel(List.<Integer>of(), i -> compteur.incrementAndGet());
        }

        assertEquals(0, compteur.get());
    }

    @Test
    void runInParallel_uneErreurNInterromptPasLeReste() {
        List<Integer> items = IntStream.rangeClosed(1, 100).boxed().toList();
        Set<Integer> traites = ConcurrentHashMap.newKeySet();

        try (ParallelTaskExecutor executor = new ParallelTaskExecutor(10, 1)) {
            executor.runInParallel(items, i -> {
                if (i == 50) {
                    throw new IllegalStateException("échec simulé pour l'élément 50");
                }
                traites.add(i);
            });
        }

        assertEquals(99, traites.size());
        assertFalse(traites.contains(50));
    }

    @Test
    void runInParallel_reutiliseLeMemePool_surPlusieursAppels() {
        List<Integer> items = IntStream.rangeClosed(1, 100).boxed().toList();
        Set<String> threadsUtilises = ConcurrentHashMap.newKeySet();

        try (ParallelTaskExecutor executor = new ParallelTaskExecutor(4, 1)) {
            executor.runInParallel(items, i -> threadsUtilises.add(Thread.currentThread().getName()));
            executor.runInParallel(items, i -> threadsUtilises.add(Thread.currentThread().getName()));
        }

        // Un pool réutilisé ne mobilise jamais plus de threads que sa taille, même sur deux appels.
        assertTrue(threadsUtilises.size() <= 4);
    }
}
