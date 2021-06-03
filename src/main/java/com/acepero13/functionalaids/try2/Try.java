package com.acepero13.functionalaids.try2;

import com.acepero13.functionalaids.either.Either;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * The Try type represents a computation that may either result in an exception, or return a successfully computed value.
 * It's similar to, but semantically different from the {@link Either} type
 */
public interface Try<T> {
    /**
     * Returns a Failure instance. A Throwable is provided.
     *
     * @param error exception
     * @param <T>   Type of the non-existent value
     * @param <U>   Type of the possible Throwable item
     * @return Failure instance
     */
    static <T, U extends Throwable> Try<T> failure(Supplier<U> error) {
        return TryImpl.failure(error);
    }

    /**
     * Returns a Success instance.
     *
     * @param value the value to be wrapped
     * @param <T>   Type of the non-existent value
     * @return Success instance
     */
    static <T> Try<T> success(T value) {
        return TryImpl.success(value);
    }

    /**
     * Returns either a Success or a Failure depending of the evaluation of <i>ofThrowable</i>.
     *
     * @param ofThrowable an instance to be evaluated which may raise an exception. In case an exception is raised, it
     *                    is capture and the function returns an instance of Failure. If no exception was raised, then
     *                    it returns an instance of Success
     * @param <T>         The type to be wrapped
     * @return An instance of Success or Failure depending on whether the evaluation of <i>ofThrowable</i> was successful or not.
     */
    static <T> Try<T> of(Supplier<T> ofThrowable) {
        try {
            return Try.success(ofThrowable.get());
        } catch (Exception e) {
            return Try.failure(() -> e);
        }

    }


    /**
     * Maps the given function to the value from <b>this</b> Success or returns <b>this</b> if <b>this</b> is a Failure.
     *
     * @param mapper function to be applied
     * @param <U>    Type of the function's result
     * @return a new Try wrapping the value calculated by mapper
     */
    <U> Try<U> map(Function<? super T, ? extends U> mapper);

    /**
     * Returns the given function applied to the value from <b>this</b> Success or returns <b>this</b> if <b>this</b> is a Failure.
     *
     * @param mapper function to be applied
     * @param <U>    Type of the function's result
     * @return a new Try wrapping the value calculated by mapper
     */
    <U> Try<U> flatMap(Function<? super T, ? extends Try<U>> mapper);

    <U> Try<U> orElse(Supplier<Try<? extends U>> or);

    /**
     * Converts <b>this</b> to a Failure if the predicate is not satisfied.
     * @param p Predicate
     * @return if predicate holds, return <b>this</b>, failure otherwise
     */
    Try<T> filter(Predicate<T> p);


    /**
     * Returns the value from <b>this</b> Success or the given default argument if <b>this</b> is a Failure.
     *
     * @param or  default value in case <b>this</b> is a Failure
     * @param <U> Type of element to return
     * @return The value if <b>this</b> is Success, default (or) otherwise
     */
    <U extends T> U getOrElse(Supplier<U> or);

    /**
     * Applies the given function f if <b>this</b> is a Success, otherwise returns Unit if <b>this</b> is a Failure.
     *
     * @param consumer side-effect function.
     */
    void forEach(Consumer<T> consumer);

    /**
     * Applies fFail if <b>this</b> is a Failure or fSuccess if <b>this</b> is a Success.
     *
     * @param fFail    function to apply in case <b>this</b> is a Failure
     * @param fSuccess function to apply in case <b>this</b> is a Success
     * @param <U>      return type
     * @return the value after applying either function
     */
    <U> U fold(Function<Throwable, U> fFail, Function<T, U> fSuccess);
    /**
     * Applies fFail if <b>this</b> is a Failure or fSuccess if <b>this</b> is a Success.
     * This function differs from {@code Try#fold} in that instead of returning a value, it returns an optional.
     * Recommended for working with streams and Optionals, since it helps the compiler to infer the types better
     * than with {@code Try#fold}.
     *
     * Example:
     *
     * <pre>
     *     ints.stream()
     *           .map(i -- Try.of(() -- 10 / i))
     *           .map(t -- t.fold(i -- Optional.empty(), Optional::of))
     *
     *
     *
     *     Here the compiler cannot infer that the mapped type is Optional[Integer]. In this case, it infers the type:
     *     Optional[? extends Object]
     * </pre>
     *
     * If we use foldOptional:
     *
     * <pre>
     *     ints.stream()
     *                 .map(i -- Try.of(() -- 10 / i))
     *                 .map(t -- t.foldOptional(i -- null, i -- i))
     *
     *
     *     It can correctly infer the Optional[Integer]
     * </pre>
     * @since 1.0
     * @param fFail    function to apply in case <b>this</b> is a Failure
     * @param fSuccess function to apply in case <b>this</b> is a Success
     * @param <U>      return type
     * @return An optional of the  value after applying either function.
     */
    <U> Optional<U> foldOptional(Function<Throwable, U> fFail, Function<T, U> fSuccess);

    /**
     * Returns true if the Try is a Success, false otherwise.
     *
     * @return true if <b>this</b> a Success, false otherwise
     */
    boolean isSuccess();

    /**
     * Returns true if the Try is a Failure, false otherwise.
     *
     * @return true if <b>this</b> is a Failure, true otherwise
     */
    boolean isFailure();


    /**
     * Executes side effect function in case <b>this</b> is a success
     *
     * @param consumer function to apply
     */
    void ifSuccess(Consumer<T> consumer);

    /**
     * Executes side effect function in case <b>this</b> is a failure.
     *
     * @param consumer function to apply
     */
    void ifFailure(Consumer<Throwable> consumer);


    /**
     * Returns the value from <b>this</b> Success or throws the exception if <b>this</b> is a Failure.
     * The exception raised is an instance of RuntimeException. This means it is an unchecked exception
     * Use <b>this</b> method if you really are not very interested in capturing the exception all the time. Otherwise use better
     * {@code Try#get}
     *
     * @return the value of success, exception otherwise
     */
    T getSilent() throws RuntimeException;

    /**
     * Returns the value from <b>this</b> Success or throws the exception if <b>this</b> is a Failure.
     * @throws Throwable the throwable found in case <b>this</b> is a Failure
     *
     * @return the value of success, exception otherwise
     */
    T get() throws Throwable;

    /**
     * Returns an optional value from the try. If <b>this</b> is Failure, it returns {@link Optional#empty()}
     *
     * @return Optional with the current value or empty if Failure
     */
    Optional<T> toOptional();
}
