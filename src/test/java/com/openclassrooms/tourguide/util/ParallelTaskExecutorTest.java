package com.openclassrooms.tourguide.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParallelTaskExecutorTest {

    @Test
    void runInParallel_appliqueLActionAChaqueElement() {
        List<Integer> items = IntStream.rangeClosed(1, 1000).boxed().toList();
        Set<Integer> traites = ConcurrentHashMap.newKeySet();

        ParallelTaskExecutor.runInParallel(items, traites::add, 50, 1);

        assertEquals(1000, traites.size());
    }

    @Test
    void runInParallel_listeVide_neFaitRien() {
        AtomicInteger compteur = new AtomicInteger();

        ParallelTaskExecutor.runInParallel(List.<Integer>of(), i -> compteur.incrementAndGet(), 10, 1);

        assertEquals(0, compteur.get());
    }
}
