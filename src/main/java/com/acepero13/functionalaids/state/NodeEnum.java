package com.acepero13.functionalaids.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

final class NodeEnum<S, E extends Event> implements Node<S, E> {
    private final Map<E, Node<S, E>> transitions = new HashMap<>();
    private final List<Consumer<E>> onExitListeners = new ArrayList<>();
    private final List<Consumer<E>> onEnterListeners = new ArrayList<>();

    private final S state;

    public static <S extends Enum<S>, E extends Event> Node<S, E> of(S from) {
        return new NodeEnum<>(from);
    }

    private NodeEnum(S state) {
        this.state = state;
    }

    public void connect(Node<S, E> toState, E byEvent) {
        transitions.put(byEvent, toState);
    }

    @Override
    public boolean canTransitionBy(E event) {



        boolean hasClassEvent = event instanceof ClassEvent && transitions.keySet().stream()
                .filter(t -> t.generic().isPresent())
                .map(t -> t.generic().get())
                .anyMatch(g ->  ((Class)g).isInstance(event));
        return transitions.containsKey(event) || hasClassEvent;
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

        if(newState == null) {
            Optional<Node<S, E>> opState = transitions.entrySet().stream()
                    .filter(t -> t.getKey().generic().isPresent())
                    .filter(t -> ((Class)t.getKey().generic().get()).isInstance(event))
                    .map(Map.Entry::getValue)
                    .findFirst();

            if(opState.isPresent()) {
                newState = opState.get();
            }
        }
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
