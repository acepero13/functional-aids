package com.acepero13.functionalaids.state;

import java.lang.reflect.GenericDeclaration;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;

public class StateMachineBuilder<S extends Enum<S>, E extends Event> {
    private final HashMap<S, Node<S, E>> nodes = new HashMap<>();
    private final Node<S, E> root;

    public StateMachineBuilder(S initialState) {
        this.root = Node.root(initialState);
        nodes.put(initialState, root);
    }

    public StateMachineBuilder<S, E> addTransition(S from, E event, S to) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(event);
        Objects.requireNonNull(to);

        Node<S, E> fromState = nodes.getOrDefault(from, Node.of(from));
        Node<S, E> toState = nodes.getOrDefault(to, Node.of(to));


        fromState.connect(toState, event);
        nodes.put(from, fromState);
        nodes.put(to, toState);
        return this;
    }


    public StateMachine<S, E> build() {
        return new StateMachineImp<S, E>(root);
    }


    public StateMachineBuilder<S, E> addTransition(S from, GenericDeclaration event, S to) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(event);
        Objects.requireNonNull(to);

        Node<S, E> fromState = nodes.getOrDefault(from, Node.of(from));
        Node<S, E> toState = nodes.getOrDefault(to, Node.of(to));
        fromState.connect(toState, event);
        nodes.put(from, fromState);
        nodes.put(to, toState);
        return this;
    }

    public StateMachineBuilder<S, E> onExit(S state, Consumer<E> consumer) {
        Node<S, E> node = nodes.getOrDefault(state, Node.of(state));
        node.addOnExitListener(consumer);
        return this;
    }

    public StateMachineBuilder<S, E> onEnter(S state, Consumer<E> consumer) {
        Node<S, E> node = nodes.getOrDefault(state, Node.of(state));
        node.addOnEnterListener(consumer);
        return this;
    }

    public StateMachineBuilder<S, E> onExit(S state, Runnable consumer) {
        Node<S, E> node = nodes.getOrDefault(state, Node.of(state));
        node.addOnExitListener(consumer);
        return this;
    }

    public StateMachineBuilder<S, E> onEnter(S state, Runnable consumer) {
        Node<S, E> node = nodes.getOrDefault(state, Node.of(state));
        node.addOnEnterListener(consumer);
        return this;
    }
}
