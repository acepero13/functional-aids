package com.acepero13.functionalaids.state;

public interface StateMachine<S extends Enum<S>, E extends Event> {
    void apply(E run);

    S getState();
}
