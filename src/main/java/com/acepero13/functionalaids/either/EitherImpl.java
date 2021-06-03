package com.acepero13.functionalaids.either;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

final class EitherImpl {
    private EitherImpl() {
    }

    public static <E> Either<E, Object> left(Supplier<? extends E> err) {
        return new Left<>(err);
    }

    public static <A> Either<?, A> right(A value) {
        return new Right<>(value);
    }

    private static class Right<A> implements Either<Object, A> {

        private final A value;

        private Right(A value) {
            this.value = value;
        }

        @Override
        public <B> Either<Object, B> map(Function<? super A, ? extends B> mapper) {
            Objects.requireNonNull(mapper);
            return new Right<>(mapper.apply(value));
        }

        @Override
        @SuppressWarnings("unchecked")
        public <B> Either<Object, B> flatMap(Function<? super A, Either<? super Object, ? extends B>> mapper) {
            Objects.requireNonNull(mapper);
            return (Either<Object, B>) mapper.apply(value);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <B> Either<Object, B> orElse(Supplier<Either<?, ? extends B>> sup) {
            return (Either<Object, B>) this;
        }

        @Override
        public boolean isRight() {
            return true;
        }


        @Override
        public boolean isLeft() {
            return !isRight();
        }

        @Override
        @SuppressWarnings("unchecked")
        public <A1 extends A> Either<Object, A1> filterOrElse(Predicate<A> p, Supplier<A1> zero) {
            Objects.requireNonNull(p);
            return p.test(value)
                    ? (Either<Object, A1>) new Right<>(value)
                    : new Right<>(zero.get());
        }

        @Override
        public <C> C fold(Function<Object, C> fLeft, Function<A, C> fRight) {
            return fRight.apply(value);
        }

        @Override
        public Boolean forAll(Predicate<A> p) {
            return p.test(value);
        }

        @Override
        public void forEach(Consumer<A> consumer) {
            consumer.accept(value);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <A1 extends A> A1 getOrElse(Supplier<A1> or) {
            return (A1) value;
        }

        @Override
        public void ifRight(Consumer<A> consumer) {
            consumer.accept(value);
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Right)) return false;
            Right<?> right = (Right<?>) o;
            return value.equals(right.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    private static class Left<E> implements Either<E, Object> {


        private final Supplier<? extends E> err;

        private Left(Supplier<? extends E> err) {
            this.err = err;
        }


        @Override
        @SuppressWarnings("unchecked")
        public <B> Either<E, B> map(Function<? super Object, ? extends B> mapper) {
            return (Either<E, B>) new Left<>(err);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <B> Either<E, B> flatMap(Function<? super Object, Either<? super E, ? extends B>> mapper) {
            return (Either<E, B>) new Left<>(err);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <B> Either<E, B> orElse(Supplier<Either<? extends E, ? extends B>> sup) {
            return (Either<E, B>) sup.get();
        }


        @Override
        public boolean isRight() {
            return !isLeft();
        }


        @Override
        public boolean isLeft() {
            return true;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <A1> Either<E, A1> filterOrElse(Predicate<Object> p, Supplier<A1> zero) {
            return (Either<E, A1>) new Left<>(err);
        }

        @Override
        public <C> C fold(Function<E, C> fLeft, Function<Object, C> fRight) {
            Objects.requireNonNull(fLeft);
            return fLeft.apply(err.get());
        }

        @Override
        public Boolean forAll(Predicate<Object> p) {
            return true;
        }

        @Override
        public void forEach(Consumer<Object> consumer) {
            // Do nothing
        }

        @Override
        public <A1> A1 getOrElse(Supplier<A1> or) {
            return or.get();
        }

        @Override
        public void ifRight(Consumer<Object> consumer) {
            // Do nothing
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Left)) return false;
            Left<?> left = (Left<?>) o;
            return Objects.equals(err, left.err);
        }

        @Override
        public int hashCode() {
            return Objects.hash(err);
        }
    }


}
