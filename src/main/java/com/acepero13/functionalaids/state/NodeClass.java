package com.acepero13.functionalaids.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

final class NodeClass<S, E extends Event> implements Node<S, E> {
    private final Map<E, Node<S, E>> transitions = new HashMap<>();
    private final List<Consumer<E>> onExitListeners = new ArrayList<>();
    private final List<Consumer<E>> onEnterListeners = new ArrayList<>();
    private final S state;

    public static <S extends Enum<S>, E extends Event> Node<S, E> of(S from) {
        return new NodeClass<>(from);
    }

    private NodeClass(S state) {
        this.state = state;
    }

    public void connect(Node<S, E> toState, E byEvent) {
        transitions.put(byEvent, toState);
    }

    @Override
    public boolean canTransitionBy(E event) {
        return true;
    }

    public void addOnExitListener(Consumer<E> consumer) {
        onExitListeners.add(consumer);
    }

    public void addOnEnterListener(Consumer<E> consumer) {
        onEnterListeners.add(consumer);
    }

    @Override
    public void notifyOnEnter(E event) {
        onEnterListeners.forEach(l -> l.accept(event));
    }

    public Node<S, E> fire(E event) {
        // TODO: Check if possible
        onExitListeners.forEach(c -> c.accept(event));

        Node<S, E> newState = transitions.get(event);

        newState.notifyOnEnter(event);
        return newState;
    }

    public S getState() {
        return this.state;
    }

    @Override
    public void addOnExitListener(Runnable runnable) {
        onExitListeners.add((e) -> runnable.run());
    }

    @Override
    public void addOnEnterListener(Runnable runnable) {
        onEnterListeners.add((e) -> runnable.run());
    }


}
