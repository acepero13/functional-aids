package com.acepero13.functionalaids.tuple;

final class Tuple2Impl<F, S> implements Tuple2<F, S> {
    private final F first;
    private final S second;

    Tuple2Impl(F fist, S second) {
        this.first = fist;
        this.second = second;
    }

    @Override
    public F first() {
        return first;
    }

    @Override
    public S second() {
        return second;
    }

    @Override
    public void apply(TupleApplicable<F, S> applier) {
        applier.apply(first, second);
    }

}
