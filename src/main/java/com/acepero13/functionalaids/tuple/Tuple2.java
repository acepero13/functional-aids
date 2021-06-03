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
public interface Tuple2<F, S> {
    static <F, S> Tuple2<F, S> of(F fist, S second) {
        return new Tuple2Impl<>(fist, second);
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
     * @return A projection of element 2 of this Product.
     */
    S second();

    /**
     * Invoke the specified function with the two value.
     * Params:
     * consumer
     * @param  applier Block to be executed
     */
    void apply(TupleApplicable<F, S> applier);

    @FunctionalInterface
    interface TupleApplicable<F, S> {
        void apply(F first, S second);
    }

}
