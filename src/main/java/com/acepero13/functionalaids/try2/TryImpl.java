package com.acepero13.functionalaids.try2;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

final class TryImpl {
    private TryImpl() {
    }

    public static <T, U extends Throwable> Try<T> failure(Supplier<U> error) {
        return new Failure<>(error);
    }

    public static <T> Try<T> success(T value) {
        return new Success<>(value);
    }

    private static class Success<T> implements Try<T> {

        private final T value;

        public Success(T value) {
            this.value = value;
        }

        @Override
        public <U> Try<U> map(Function<? super T, ? extends U> mapper) {
            Objects.requireNonNull(mapper);
            return new Success<>(mapper.apply(value));
        }

        @Override
        public <U> Try<U> flatMap(Function<? super T, ? extends Try<U>> mapper) {
            Objects.requireNonNull(mapper);
            return mapper.apply(value);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <U> Try<U> orElse(Supplier<Try<? extends U>> or) {
            return (Try<U>) this;
        }

        @Override
        public Try<T> filter(Predicate<T> p) {
            return p.test(value)
                    ? this
                    : new Failure<>(() -> new Exception("Predicate not satisfied"));
        }

        @Override
        @SuppressWarnings("unchecked")
        public <U extends T> U getOrElse(Supplier<U> or) {
            return (U) value;
        }

        @Override
        public void forEach(Consumer<T> consumer) {
            Objects.requireNonNull(consumer);
            consumer.accept(value);
        }

        @Override
        public <U> U fold(Function<Throwable, U> fFail, Function<T, U> fSuccess) {
            Objects.requireNonNull(fSuccess);
            return fSuccess.apply(value);
        }

        @Override
        public <U> Optional<U> foldOptional(Function<Throwable, U> fFail, Function<T, U> fSuccess) {
            return Optional.ofNullable(fold(fFail, fSuccess));
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public boolean isFailure() {
            return false;
        }

        @Override
        public void ifSuccess(Consumer<T> consumer) {
            forEach(consumer);
        }

        @Override
        public void ifFailure(Consumer<Throwable> consumer) {
            // Do nothing
        }

        @Override
        public T getSilent() {
            return get();
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public Optional<T> toOptional() {
            return Optional.of(value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Success)) return false;
            Success<?> success = (Success<?>) o;
            return value.equals(success.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    private static class Failure<T, E extends Throwable> implements Try<T> {

        private final Supplier<E> errSupplier;

        private Failure(Supplier<E> errSupplier) {
            Objects.requireNonNull(errSupplier);
            this.errSupplier = errSupplier;
        }


        @Override
        @SuppressWarnings("unchecked")
        public <U> Try<U> map(Function<? super T, ? extends U> mapper) {
            return (Try<U>) this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <U> Try<U> flatMap(Function<? super T, ? extends Try<U>> mapper) {
            return (Try<U>) this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <U> Try<U> orElse(Supplier<Try<? extends U>> or) {
            Objects.requireNonNull(or);
            return (Try<U>) or.get();
        }

        @Override
        public Try<T> filter(Predicate<T> p) {
            return this;
        }

        @Override
        public <U extends T> U getOrElse(Supplier<U> or) {
            Objects.requireNonNull(or);
            return or.get();
        }

        @Override
        public void forEach(Consumer<T> consumer) {
            // Do nothing
        }

        @Override
        public <U> U fold(Function<Throwable, U> fFail, Function<T, U> fSuccess) {
            Objects.requireNonNull(fFail);
            return fFail.apply(errSupplier.get());
        }

        @Override
        public <U> Optional<U> foldOptional(Function<Throwable, U> fFail, Function<T, U> fSuccess) {
            Objects.requireNonNull(fFail);
            return Optional.ofNullable(fFail.apply(errSupplier.get()));
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public boolean isFailure() {
            return true;
        }

        @Override
        public void ifSuccess(Consumer<T> consumer) {
            // Do nothing
        }

        @Override
        public void ifFailure(Consumer<Throwable> consumer) {
            Objects.requireNonNull(consumer);
            consumer.accept(errSupplier.get());
        }

        @Override
        public T getSilent() throws RuntimeException {
            throw new RuntimeException(errSupplier.get());
        }

        @Override
        public T get() throws Throwable {
            throw errSupplier.get();
        }

        @Override
        public Optional<T> toOptional() {
            return Optional.empty();
        }

        @Override
        public boolean equals(Object o) {
            return this == o;

        }

        @Override
        public int hashCode() {
            return Objects.hash(errSupplier);
        }
    }
}
