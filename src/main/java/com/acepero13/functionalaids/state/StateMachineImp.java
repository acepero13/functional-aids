package com.acepero13.functionalaids.state;

public class StateMachineImp<S extends Enum<S>, E extends Event> implements StateMachine<S, E> {


    private Node<S, E> state;

    public StateMachineImp(Node<S, E> root) {
        this.state = root;
    }

    @Override
    public void apply(E event) {
        if(state.canTransitionBy(event)) {
            this.state = state.fire(event);
        }
    }

    @Override
    public S getState() {
        return state.getState();
    }
}
