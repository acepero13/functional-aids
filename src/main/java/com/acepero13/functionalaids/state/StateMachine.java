package com.acepero13.functionalaids.state;

import java.util.Objects;
import java.util.Optional;

public final class StateMachine<S extends Enum<S>, E extends Event>  {

    private Node<S, E> state;

    public StateMachine(Node<S, E> root) {
        this.state = root;
    }

    public Optional<Node<S, E>> apply(E event) {
        Objects.requireNonNull(event);
        if(state.canTransitionBy(event)) {
            Optional<Node<S, E>> newState = state.fire(event);
            newState.ifPresent(seNode -> this.state = seNode);
            return newState;
        }
        return Optional.empty();
    }

    public S getState() {
        return state.getState();
    }
}
