package com.acepero13.functionalaids.either;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Represents a value of one of two possible types (a disjoint union). An instance of Either is an instance of <b>Left</b>
 * or <b>Right</b>.
 * <p>
 * A common use of Either is as an alternative to scala.Option for dealing with possibly missing values.
 *
 * @param <E> Type of the Left element
 * @param <A> Type of the Right element
 */
public interface Either<E, A> {

    @SuppressWarnings("unchecked")
    static <A, E> Either<E, A> left(Supplier<? extends E> err) {
        return (Either<E, A>) EitherImpl.left(err);
    }


    @SuppressWarnings("unchecked")
    static <A, E> Either<E, A> right(A value) {
        return (Either<E, A>) EitherImpl.right(value);
    }

    /**
     * The given function is applied if this is a Right.
     *
     * @param mapper the function to be applied
     * @param <B>    the result type of the new value after applying mapper
     * @return The result of the given function after being applied, wrapped in an Eiter
     */
    <B> Either<E, B> map(Function<? super A, ? extends B> mapper);

    /**
     * Binds the given function across Right.
     *
     * @param mapper The function to bind across Right.
     * @param <B>    the result type of the new value after applying mapper
     * @return An Either representing result of the given function after being applied
     */
    <B> Either<E, B> flatMap(Function<? super A, Either<? super E, ? extends B>> mapper);

    /**
     * Returns this Right or the given argument if this is a Left.
     *
     * @param sup A supplier function that returns a value in case this is a Left
     * @param <B> the result type
     * @return A right element. Either the original value, or the default value in case this is a Left
     */
    <B> Either<E, B> orElse(Supplier<Either<? extends E, ? extends B>> sup);



    /**
     * Returns true if this is a Right, false otherwise.
     *
     * @return true if is Right or successful, false otherwise
     */

    boolean isRight();


    /**
     * Returns true if this is a Left, false otherwise.
     *
     * @return true if is Left or failure, false otherwise
     */

    boolean isLeft();

    /**
     * Returns Right with the existing value of Right if this is a Right and the given predicate p holds for the right value,
     * or Left(zero) if this is a Right and the given predicate p does not hold for the right value,
     * or Left with the existing value of Left if this is a Left.
     *
     * @param <A1> Type of the Right element. It must be A or a subclass of A
     * @param p Predicate
     * @param zero default value to return if <i>p</i> evaluates to false in case this is Right.
     * @return An Either result
     */
    <A1 extends A> Either<E, A1> filterOrElse(Predicate<A> p, Supplier<A1> zero);


    /**
     * Applies fLeft if this is a Left or fRight if this is a Right.
     *
     * @param fLeft  function to apply if this is Left
     * @param fRight function to apply if this is Right
     * @param <C>    type of input parameter for fLeft
     * @return The applied value
     */
    <C> C fold(Function<E, C> fLeft, Function<A, C> fRight);

    /**
     * Boolean Returns true if Left or returns the result of the application of the given predicate to the Right value.
     *
     * @param p predicate
     * @return true if predicate applies or this is Left, false otherwise
     */
    Boolean forAll(Predicate<A> p);

    /**
     * Executes the given side-effecting function if this is a Right.
     *
     * @param consumer the side-effect function to  execute
     */

    void forEach(Consumer<A> consumer);

    /**
     * Returns the value from this Right or the given argument if this is a Left.
     *
     * @param <A1> Type/Subtype of the value
     * @param or default value in case this is a Left
     * @return The value contained in Right in case this is a Right, or otherwise
     */

    <A1 extends A> A1 getOrElse(Supplier<A1> or);

    /**
     * Applies consumer in case this is right
     *
     * @param consumer to be applied
     */
    void ifRight(Consumer<A> consumer);


}
