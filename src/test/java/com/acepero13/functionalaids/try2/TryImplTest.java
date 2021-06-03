package com.acepero13.functionalaids.try2;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TryImplTest {
    private final Try<Integer> err = Try.failure(() -> new Exception("error"));

    private final Try<Integer> one = Try.success(1);

    @Test
    void mapAnExceptionReturnsSameException() {
        assertEquals(err, err.map(i -> i + 1));
    }

    @Test
    void incrementUsingMap() {
        Try<Integer> expected = Try.success(2);
        assertEquals(expected, one.map(i -> i + 1));
    }

    @Test
    void flatMapAnExceptionReturnsSameException() {
        assertEquals(err, err.flatMap(i -> err));
    }

    @Test
    void incrementUsingFlatMap() {
        Try<Integer> expected = Try.success(2);
        assertEquals(expected, one.flatMap(i -> Try.success(i + 1)));
    }

    @Test
    void newExceptionCreatedWhileInFlatMapFromSuccess() {
        Try<Integer> newError = Try.failure(() -> new Exception("error 2"));
        assertEquals(newError, one.flatMap(i -> newError));
    }

    @Test
    void onAFailureReturnTheDefaultValue() {
        assertEquals(one, err.orElse(() -> one));
    }

    @Test
    void onSuccessReturnTheCurrentValue() {
        assertEquals(one, one.orElse(() -> err));
    }

    @Test
    void filterAFailureShouldReturnSameFailure() {
        assertEquals(err, err.filter(i -> i > 2));
    }

    @Test
    void filterReturnsSameSuccessIfPredicateHolds() {
        assertEquals(one, one.filter(i -> i == 1));
    }

    @Test
    void filterReturnsFailureIfPredicateDoesNotHold() {
        assertThrows(Exception.class, () -> one.filter(i -> i > 1).getSilent());
    }

    @Test
    void getDefaultFromAFailure() {
        assertEquals(10, err.getOrElse(() -> 10));
    }

    @Test
    void getValueFromSuccess() {
        assertEquals(1, one.getOrElse(() -> 10));
    }

    @Test
    void forEachOnSuccessShouldExecuteFunction() {
        AtomicReference<Integer> reference = new AtomicReference<>(0);
        one.forEach(reference::set);
        assertEquals(1, reference.get());
    }

    @Test
    void forEachOnSuccessShouldDoNothing() {
        AtomicReference<Integer> reference = new AtomicReference<>(0);
        err.forEach(reference::set);
        assertEquals(0, reference.get());
    }

    @Test
    void foldApplyFunctionToFailure() {
        Integer actual = err.fold(t -> 100, t -> t + 1);
        assertEquals(100, actual);
    }

    @Test
    void foldOptionalOfFailureReturnsEmpty() {
        Optional<Integer> actual = err.foldOptional(t -> null, t -> t + 1);
        assertEquals(Optional.empty(), actual);
    }

    @Test
    void foldOptionalOfSuccessReturnsOptional() {
        Optional<Integer> actual = one.foldOptional(t -> null, t -> t + 1);
        assertEquals(Optional.of(2), actual);
    }

    @Test
    void foldOptionalOfSuccessReturningNullReturnsOptional() {
        Optional<Integer> actual = one.foldOptional(t -> null, t ->null);
        assertEquals(Optional.empty(), actual);
    }


    @Test
    void foldWithStreams() {
        List<Integer> ints = Arrays.asList(1, 2, 5, 0);

        List<Integer> res = ints.stream()
                .map(i -> Try.of(() -> 10 / i))
                //.map(t -> t.fold(i -> Optional.empty(), Optional::of)) // Here the type is unknown (Stream<Optional<? extends Object>), that's why we need foldOptional
                .map(t -> t.foldOptional(i -> null, i -> i))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(i -> i * 10)
                .collect(Collectors.toList());

        assertEquals(Arrays.asList(100, 50, 20), res);
    }

    @Test
    void foldApplyFunctionToSuccess() {
        Integer actual = one.fold(t -> 100, t -> t + 1);
        assertEquals(2, actual);
    }

    @Test
    void successIsMarkedAsSuccess() {
        assertTrue(one.isSuccess());
        assertFalse(one.isFailure());
    }

    @Test
    void failureIsMarkedAsFailure() {
        assertTrue(err.isFailure());
        assertFalse(err.isSuccess());
    }

    @Test
    void executesFunctionOnSuccess() {
        one.ifSuccess(i -> assertEquals(1, i));
        assertTrue(true);
    }


    @Test
    void executesFunctionOnFailure() {
        err.ifFailure(i -> assertTrue(true));
        assertTrue(true);
    }

    @Test
    void doesNotExecuteFailureFunctionOnSuccess() {
        one.ifFailure(i -> fail("Should not execute this function"));
        assertTrue(true);
    }


    @Test
    void doesNotExecuteSuccessFunctionOnFailure() {
        err.ifSuccess(i -> fail("Should not execute this function"));
        assertTrue(true);
    }

    @Test
    void getFromSuccessSilently() {
        assertEquals(1, one.getSilent());
    }

    @Test
    void getFromSuccess() throws Throwable {
        assertEquals(1, one.get());
    }

    @Test
    void getFromFailureSilently() {
        assertThrows(RuntimeException.class, err::getSilent);
    }

    @Test
    void getFromFailure() {
        assertThrows(Exception.class, err::get);
    }

    @Test
    void successHashCode() {
        assertEquals(32, one.hashCode());
    }

    @SuppressWarnings("ObviousNullCheck")
    @Test
    void failureHashCode() {
        assertNotNull(err.hashCode());
    }

    @Test
    void createsFailureWithoutKnowingWhetherSuccessOrFailure() {
        //noinspection divzero ,NumericOverflow (It is intended this way, to test runtime exceptions)
        assertTrue(Try.of(() -> 10 / 0).isFailure());
    }

    @Test
    void createsSuccessWithoutKnowingWhetherSuccessOrFailure() {
        assertTrue(Try.of(() -> 1 / 2).isSuccess());
    }

    @Test
    void failureReturnsEmptyOptional() {
        assertEquals(Optional.empty(), err.toOptional());
    }

    @Test
    void successReturnsOptionalWithValue() {
        assertEquals(1, one.toOptional().orElse(20));
    }


}