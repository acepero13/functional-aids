package com.acepero13.functionalaids.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

final class Node<S, E extends Event> {
    private final Map<E, Node<S, E>> transitions = new HashMap<>();
    private final List<Consumer<E>> onExitListeners = new ArrayList<>();
    private final List<Consumer<E>> onEnterListeners = new ArrayList<>();

    private final S state;

    static <S extends Enum<S>, E extends Event> Node<S, E> of(S from) {
        return new Node<>(from);
    }

    private Node(S state) {
        this.state = state;
    }

    void connect(Node<S, E> toState, E byEvent) {
        transitions.put(byEvent, toState);
    }

    boolean canTransitionBy(E event) {
        return transitions.keySet().stream().anyMatch(Filter.of(event));
    }


    void addOnExitListener(Consumer<E> consumer) {
        onExitListeners.add(consumer);
    }

    void addOnEnterListener(Consumer<E> consumer) {
        onEnterListeners.add(consumer);
    }

    void notifyOnEnter(E event) {
        onEnterListeners.forEach(l -> l.accept(event));
    }

    Optional<Node<S, E>> fire(E event) {
        onExitListeners.forEach(c -> c.accept(event));

        Optional<Node<S, E>> newState = transitions.keySet().stream()
                .filter(Filter.of(event))
                .findFirst()
                .map(transitions::get);

        newState.ifPresent(s ->  s.notifyOnEnter(event));

        return newState;
    }

    S getState() {
        return this.state;
    }

    void addOnExitListener(Runnable runnable) {
        onExitListeners.add((e) -> runnable.run());
    }

    void addOnEnterListener(Runnable runnable) {
        onEnterListeners.add((e) -> runnable.run());
    }


    private static abstract class Filter {
        protected final Event incoming;

        private Filter(Event incoming) {
            this.incoming = incoming;
        }

        private static Predicate<Event> of(Event incoming) {
            Filter filter = (incoming.getClass().isEnum()) ? new EnumFilter(incoming) : new ClassFilter(incoming);
            return filter.predicate();
        }

        protected abstract Predicate<Event> predicate();

        private static class EnumFilter extends Filter {

            EnumFilter(Event incoming) {
                super(incoming);
            }

            @Override
            protected Predicate<Event> predicate() {
                return (e) -> e == incoming;
            }
        }

        private static class ClassFilter extends Filter {

            ClassFilter(Event incoming) {
                super(incoming);
            }

            @Override
            @SuppressWarnings("unchecked")
            protected Predicate<Event> predicate() {
                return (e) -> e.generic()
                        .map(g -> (Class<Event>) g)
                        .filter(cl -> cl.isInstance(incoming))
                        .isPresent();
            }
        }
    }
}
