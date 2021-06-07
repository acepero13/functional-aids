package com.acepero13.functionalaids.tuple;

final class Triple3Impl<F, S, T> implements Triple3<F, S, T> {
    private final F first;
    private final S second;
    private final T third;

    public Triple3Impl(F fist, S second, T third) {
        this.first = fist;
        this.second = second;
        this.third = third;
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
    public T third() {
        return third;
    }

    @Override
    public void apply(TupleApplicable<F, S, T> applier) {
        applier.apply(first, second, third);
    }
}
