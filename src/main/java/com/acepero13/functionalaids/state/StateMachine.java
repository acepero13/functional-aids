package com.acepero13.functionalaids.state;

import java.util.Optional;

public class StateMachine<S extends Enum<S>, E extends Event>  {


    private Node<S, E> state;

    public StateMachine(Node<S, E> root) {
        this.state = root;
    }

    public void apply(E event) {
        if(state.canTransitionBy(event)) {
            Optional<Node<S, E>> newState = state.fire(event);
            newState.ifPresent(seNode -> this.state = seNode);
        }
    }

    public S getState() {
        return state.getState();
    }
}
