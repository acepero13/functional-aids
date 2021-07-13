package com.acepero13.functionalaids.state.klass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class StateMachineBuilder<S, E> {

    private final S root;
    private final Transitions<S, E> transitions;

    public StateMachineBuilder(S initialState) {
        this.root = initialState;
        this.transitions = new Transitions<>();
    }


    public StateMachineBuilder<S, E> addTransition(Class<? extends S> from, Class<? extends E> event, UnaryOperator<S> transition) {

        transitions.add(new Trigger<>(from, event), transition);

        return this;

    }

    public StateMachine<S, E> build() {
        return new StateMachine<>(transitions, root);
    }

    public StateMachineBuilder<S, E> onExit(Class<? extends S> state, Consumer<E> consumer) {
        transitions.addOnExitListener(state, consumer);
        return this;
    }

    private static class Trigger<S, E> {
        private final Class<? extends S> from;
        private final Class<? extends E> event;
        private final List<Consumer<E>> onExitListeners = new ArrayList<>();

        Trigger(Class<? extends S> from, Class<? extends E> event) {
            this.from = from;
            this.event = event;
        }

        public void addOnExitListener(Consumer<E> consumer) {
            this.onExitListeners.add(consumer);
        }

        public void notifyListeners(E event) {
            this.onExitListeners.forEach(l -> l.accept(event));
        }
    }

    static class Transitions<S, E> {
        private final HashMap<Trigger<S, E>, UnaryOperator<S>> transitions = new HashMap<>();

        public void add(Trigger<S, E> trigger, UnaryOperator<S> transition) {
            this.transitions.put(trigger, transition);
        }


        public Optional<S> trigger(S state, E event) {

            transitions.keySet().stream()
                    .filter(t -> t.event.isInstance(event))
                    .filter(t -> t.from.isInstance(state))
                    .findFirst()
                    .ifPresent(t -> t.notifyListeners(event));

            return transitions.entrySet().stream()
                    .filter(t -> t.getKey().event.isInstance(event))
                    .filter(t -> t.getKey().from.isInstance(state))
                    .findFirst()
                    .map(x -> x.getValue().apply(state));

        }

        public void addOnExitListener(Class<? extends S> state, Consumer<E> consumer) {
            transitions.entrySet().stream()
                    .filter(t -> t.getKey().from.equals(state))
                    .findFirst()
                    .ifPresent(t -> t.getKey().addOnExitListener(consumer));
        }
    }


}
