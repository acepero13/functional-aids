package com.acepero13.functionalaids.either;

import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class EitherTest {

    private final Either<String, Integer> one = Either.right(1);
    private final Either<String, Integer> err = Either.left(() -> "Error");

    @Test
    void incrementSuccessfully() {
        Either<String, Integer> actual = one.map(i -> i + 1);
        Either<String, Integer> expected = Either.right(2);
        assertEquals(expected, actual);
    }

    @Test
    void failToIncrement() {
        Either<String, Integer> actual = err.map(i -> i + 1);
        assertEquals(err, actual);
    }

    @Test
    void incrementUsingFlatMap() {
        Either<String, Integer> actual = one.flatMap(i -> Either.right(i + 1));
        Either<String, Integer> expected = Either.right(2);
        assertEquals(expected, actual);
    }

    @Test
    void failToIncrementUsingFlatMap() {
        Either<String, Integer> errorOccurred = Either.left(() -> "Error occurred");
        Either<String, Integer> actual = one.flatMap(i -> errorOccurred);
        assertEquals(errorOccurred, actual);
    }

    @Test
    void flatMapOnLeft() {
        Either<String, Integer> actual = err.flatMap(i -> one);
        assertEquals(err, actual);
    }

    @Test
    void rightIsSuccessful() {
        assertTrue(one.isRight());
        assertFalse(one.isLeft());
    }

    @Test
    void rightIsFailure() {
        assertFalse(err.isRight());
        assertTrue(err.isLeft());
    }

    @Test
    void orElseReturnsSameWhenIsRight() {
        Either<String, Integer> actual = one.map(i -> i + 1);
        Either<String, Integer> expected = Either.right(2);
        assertEquals(expected, actual.orElse(() -> err));
    }

    @Test
    void orElseReturnsDefaultWhenIsLeft() {
        assertEquals(one, err.orElse(() -> one));
    }

    @Test
    void filterOrElseReturnsElseWhenLeft() {
        assertEquals(err, err.filterOrElse(i -> true, () -> 1));
    }

    @Test
    void filterOrElseReturnsLeftWhenConditionDoesNotHoldAndIsARight() {
        final Either<String, Integer> expected = Either.right(10);
        assertEquals(expected, one.filterOrElse(i -> i > 1, () -> 10));
    }

    @Test
    void filterOrElseReturnsSameValueWhenConditionHolds() {
        assertEquals(one, one.filterOrElse(i -> i == 1, () -> 10));
    }

    @Test
    void foldAppliesFunctionWhenRight() {
        Integer actual = one.fold((s) -> 1, (i) -> i + 1);
        assertEquals(2, actual);
    }

    @Test
    void foldAppliesFunctionWhenLeft() {
        Integer actual = err.fold((s) -> 1, (i) -> i + 1);
        assertEquals(1, actual);
    }


    @Test
    void forAllReturnsTrueWhenLeft() {
        assertTrue(err.forAll(p -> false));
    }

    @Test
    void forAllReturnsTrueWhenPredicateInRightHolds() {
        assertTrue(one.forAll(i -> i == 1));
    }

    @Test
    void forAllReturnsFalseWhenPredicateDoesNotHoldInRight() {
        assertFalse(one.forAll(i -> i > 1));
    }

    @Test
    void forEachExecutesSideEffect() {
        AtomicReference<Integer> reference = new AtomicReference<>();

        one.forEach(reference::set);

        assertEquals(1, reference.get());
    }

    @Test
    void forEachInLeftDoesNothing() {
        AtomicReference<Integer> reference = new AtomicReference<>(0);

        err.forEach(reference::set);
        assertEquals(0, reference.get());
    }

    @Test
    void getOrElseForRight() {
        Either<String, Integer> actual = one.map(i -> i + 1);
        assertEquals(2, actual.getOrElse(() -> 100));
    }

    @Test
    void getOrElseForLeftShouldReturnElse() {
        assertEquals(100, err.getOrElse(() -> 100));
    }

    @Test
    void hashCodeForRight() {
        assertEquals(32, one.hashCode());
    }

    @Test
    void hashCodeForLeft() {
        //noinspection ObviousNullCheck
        assertNotNull(err.hashCode());
    }

    @Test
    void transformCatIntoDog() {
        Either<String, Animal> cat = Either.right(new Cat());

        Either<String, Dog> eDog = cat.map(c -> new Dog(c.weightInKg + 10));

        assertEquals(Either.right(new Dog(20f)), eDog);
    }

    @Test
    void transformCatIntoDogUsingFlatMap() {
        Either<String, Animal> cat = Either.right(new Cat());

        Either<String, Dog> eDog = cat.flatMap(c -> Either.right(new Dog(c.weightInKg + 10)));

        assertEquals(Either.right(new Dog(20f)), eDog);
    }

    @Test
    void handleDoesNotRaiseException() {
        Either<Exception, Integer> two = Either.left(() -> new Exception("Error occurred"));
        assertEquals(1, two.map(i -> i + 1).getOrElse(() -> 1));

    }

    @Test
    void ifRight() {
        one.ifRight(v -> assertEquals(1, v));
        assertNotNull(one);
    }

    @Test
    void ifRightOnALeftDoesNothing() {
        err.ifRight(v -> fail());
        assertNotNull(err);
    }

    // Subclasses

    private static abstract class Animal {
        private final float weightInKg;

        Animal(float weightInKg) {
            this.weightInKg = weightInKg;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Animal)) return false;
            Animal animal = (Animal) o;
            return Float.compare(animal.weightInKg, weightInKg) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(weightInKg);
        }
    }

    private static class Cat extends Animal {
        Cat() {
            super((float) 10.0);
        }
    }

    private static class Dog extends Animal {
        Dog(float weightInKg) {
            super(weightInKg);
        }
    }


}