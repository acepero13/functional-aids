package com.acepero13.functionalaids.tuple;

/**
 * A tuple of 2 elements
 *
 * <p>
 * Ex: ("Hello", "World") is a tuple of two Strings
 * </p>
 *
 * <p>
 * Ex: ("John Doe", 99.99) is a tuple of a String and a Float
 * </p>
 *
 *
 * To create a Tuple we call the static method <b>of</b>. For example:
 * <pre>Tuple.of("Joe Doe", 99.99)</pre>
 * To retrieve the elements we call the <b>first</b> and <b>second</b> method respectively.
 *
 *
 * @param <F> Type of the first parameter
 * @param <S> Type of the second parameter
 */
public interface Triple3<F, S, T> {
    static <F, S, T> Triple3<F, S, T> of(F fist, S second, T third) {
        return new Triple3Impl<>(fist, second, third);
    }

    /**
     * The fist element of the tuple
     *
     * @return A projection of element 1 of this Product.
     */
    F first();

    /**
     * The second element of the tuple
     *
     * @return A projection of element 3 of this Product.
     */
    S second();

    /**
     * The third element of the tuple
     *
     * @return A projection of element 3 of this Product.
     */
    T third();

    /**
     * Invoke the specified function with the two value.
     * Params:
     * consumer
     * @param  applier Block to be executed
     */
    void apply(TupleApplicable<F, S, T> applier);

    @FunctionalInterface
    interface TupleApplicable<F, S, T> {
        void apply(F first, S second, T third);
    }

}
