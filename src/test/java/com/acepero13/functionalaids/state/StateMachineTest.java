package com.acepero13.functionalaids.state;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StateMachineTest {
    private static final String TAG = StateMachineTest.class.getSimpleName();

    enum EnumState {
        INIT, RUNNING, COMPLETED
    }

    enum EnumEvent implements Event{
        RUN, END
    }

    @Test
    void createsStateMachine() {
        StateMachine<EnumState, EnumEvent> stateMachine =
                new StateMachineBuilder<EnumState, EnumEvent>(EnumState.INIT)
                        .addTransition(EnumState.INIT, EnumEvent.RUN, EnumState.RUNNING)
                        .addTransition(EnumState.RUNNING, EnumEvent.END, EnumState.COMPLETED)
                        .build();


        assertEquals(EnumState.INIT, stateMachine.getState());
        stateMachine.apply(EnumEvent.RUN);
        assertEquals(EnumState.RUNNING, stateMachine.getState());
        stateMachine.apply(EnumEvent.END);
        assertEquals(EnumState.COMPLETED, stateMachine.getState());


    }

    @Test
    void createsStateMachineWithClassesInsteadOfEnum() {
        AtomicInteger actualEnter = new AtomicInteger(0);
        AtomicInteger actualExit = new AtomicInteger(0);

        StateMachine<EnumState, TypeEvent> stateMachine =
                new StateMachineBuilder<EnumState, TypeEvent>(EnumState.INIT)
                        .addTransition(EnumState.INIT, Run.class, EnumState.RUNNING)
                        .onExit(EnumState.INIT, (e) -> {actualExit.set(e.value);})
                        .onEnter(EnumState.RUNNING, (e) -> actualEnter.set(e.value))
                        .addTransition(EnumState.RUNNING,  Run.class, EnumState.COMPLETED)
                        .build();


        stateMachine.apply(new Run(1));

        assertEquals(1, actualEnter.get());
        assertEquals(1, actualExit.get());

    }

    private static class Run  extends TypeEvent{
        private int value;

        public Run(int i) {
            super(i);
        }
    }

    private static abstract class TypeEvent implements Event {
        public int value;

        public TypeEvent(int i) {
            value = i;
        }
    }

}