package com.acepero13.functionalaids.state;

import java.lang.reflect.GenericDeclaration;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;

public final class StateMachineBuilder<S extends Enum<S>, E extends Event> {
    private final HashMap<S, Node<S, E>> nodes = new HashMap<>();
    private final Node<S, E> root;

    StateMachineBuilder(S initialState) {
        this.root = Node.of(initialState);
        nodes.put(initialState, root);
    }

    public StateMachineBuilder<S, E> addTransition(S from, E event, S to) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(event);
        Objects.requireNonNull(to);


        Node<S, E> fromState = getOrAddNode(from);
        Node<S, E> toState = getOrAddNode(to);

        fromState.connect(toState, event);

        return this;
    }


    public StateMachine<S, E> build() {
        return new StateMachine<>(root);
    }


    public StateMachineBuilder<S, E> addTransition(S from, GenericDeclaration event, S to) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(event);
        Objects.requireNonNull(to);

        Node<S, E> fromState = getOrAddNode(from);
        Node<S, E> toState = getOrAddNode(to);
        fromState.connect(toState, ClassEvent.of(event));

        return this;
    }

    public StateMachineBuilder<S, E> onExit(S state, Consumer<E> consumer) {
        Node<S, E> node = getOrAddNode(state);

        node.addOnExitListener(consumer);
        return this;
    }


    public StateMachineBuilder<S, E> onEnter(S state, Consumer<E> consumer) {
        Node<S, E> node = getOrAddNode(state);
        node.addOnEnterListener(consumer);
        return this;
    }

    public StateMachineBuilder<S, E> onExit(S state, Runnable runnable) {
        Node<S, E> node = getOrAddNode(state);
        node.addOnExitListener(runnable);
        return this;
    }

    public StateMachineBuilder<S, E> onEnter(S state, Runnable consumer) {
        Node<S, E> node = getOrAddNode(state);
        node.addOnEnterListener(consumer);
        return this;
    }

    private Node<S, E> getOrAddNode(S state) {
        Node<S, E> node = nodes.get(state);
        if (node == null) {
            node = Node.of(state);
            nodes.put(state, node);
        }
        return node;
    }


}
