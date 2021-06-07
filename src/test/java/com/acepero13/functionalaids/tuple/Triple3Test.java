package com.acepero13.functionalaids.tuple;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Triple3Test {


    @Test
    void createsSimpleTriple() {
        Triple3<Integer, Integer, Integer> trip = Triple3.of(1, 2, 3);
        assertEquals(1, trip.first());
        assertEquals(2, trip.second());
        assertEquals(3, trip.third());
    }

    @Test
    void useFunctionalInterface() {
        final String expected = "hello";
        Triple3.of(1, 2, expected).apply((_1, _2, _3) -> {
            assertEquals(1, _1);
            assertEquals(2, _2);
            assertEquals(expected, _3);
        });
    }
}