package com.acepero13.functionalaids.tuple;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Tuple2Test {
    @Test
    void createsSimpleTuple() {
        Tuple2<Integer, Integer> tup = Tuple2.of(1, 2);
        assertEquals(1, tup.first());
        assertEquals(2, tup.second());
    }

    @Test
    void useFunctionalInterface() {
        Tuple2.of(1, 2).apply((_1, _2) -> {
            assertEquals(1, _1);
            assertEquals(2, _2);
        });
    }

}