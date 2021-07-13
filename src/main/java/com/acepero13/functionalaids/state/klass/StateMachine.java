package com.acepero13.functionalaids.state.klass;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class StateMachine<S, E> {

    private final AtomicReference<S> state = new AtomicReference<>();
    private final StateMachineBuilder.Transitions<S, E> transitions;


    public StateMachine(StateMachineBuilder.Transitions<S, E> transitions, S root) {
        this.state.set(root);
        this.transitions = transitions;
    }

    public Optional<S> getState() {
        return Optional.of(state.get());
    }

    public void apply(E event) {
        Objects.requireNonNull(event);
        transitions
                .trigger(state.get(), event)
                .ifPresent(state::set);

    }
}
