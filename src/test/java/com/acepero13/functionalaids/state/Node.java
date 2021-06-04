package com.acepero13.functionalaids.state;

import java.lang.reflect.GenericDeclaration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

final class Node<S, E> {
    private final List<Runnable> onEnterListeners = new ArrayList<>();
    private final List<Runnable> onExitListeners = new ArrayList<>();

    private final List<Consumer<E>> onEnterListenersConsumer = new ArrayList<>();
    private final List<Consumer<E>> onExitListenersConsumer = new ArrayList<>();
    private final Map<E, Node<S, E>> transitions = new HashMap<>();
    private final S state;
    private final Map<GenericDeclaration, Node<S, E>> transitionsGeneric = new HashMap<GenericDeclaration, Node<S, E>>();

    private Node(S state) {
        this.state = state;
    }

    public static <S extends Enum<S>, E extends Event> Node<S, E> root(S root) {
        return new Node<>(root);
    }

    public static <S extends Enum<S>, E extends Event> Node<S, E> of(S from) {
        return new Node<>(from);
    }

    public void connect(Node<S, E> toState, E byEvent) {
        transitions.put(byEvent, toState);
    }

    public void connect(Node<S, E> toState, GenericDeclaration byEvent) {
        this.transitionsGeneric.put(byEvent, toState);
    }


    public void addOnExitListener(Runnable consumer) {
        Objects.requireNonNull(consumer);
        this.onExitListeners.add(consumer);

    }

    public void addOnEnterListener(Runnable consumer) {
        Objects.requireNonNull(consumer);
        this.onEnterListeners.add(consumer);
    }

    public boolean canTransitionBy(E event) {
        boolean res = transitionsGeneric.keySet().stream().anyMatch(g -> ((Class)g).isInstance(event));
        return transitions.containsKey(event) || res;
    }

    public Node<S, E> fire(E event) {
        // TODO: Check if possible
        onExitListenersConsumer.forEach(c -> c.accept(event));
        onExitListeners.forEach(Runnable::run);
        Node<S, E> newState = transitions.get(event);
        if(newState == null) {
            for (Map.Entry<GenericDeclaration, Node<S,E>> entry: transitionsGeneric.entrySet()) {
                if(((Class)entry.getKey()).isInstance(event)) {
                    newState = entry.getValue();
                }
            }

        }

        newState.onEnterListeners.forEach(Runnable::run);
        newState.onEnterListenersConsumer.forEach(c -> c.accept(event));
        return newState;
    }

    public S getState() {
        return this.state;
    }

    public void addOnExitListener(Consumer<E> consumer) {
        onExitListenersConsumer.add(consumer);
    }

    public void addOnEnterListener(Consumer<E> consumer) {
        onEnterListenersConsumer.add(consumer);
    }
}
