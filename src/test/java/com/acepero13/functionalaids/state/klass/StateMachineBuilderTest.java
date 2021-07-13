package com.acepero13.functionalaids.state.klass;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StateMachineBuilderTest {
    private static final CState.Init initial = new CState.Init();
    private static final CState.Running running = new CState.Running();
    private static final CState.Completed completed = new CState.Completed();
    private static final Class<CState.Init> INIT = CState.Init.class;
    private static final Class<CState.Running> RUNNING = CState.Running.class;
    private static final Class<CState.Completed> COMPLETED = CState.Completed.class;
    private static final Class<CEvent.Run> RUN_EVENT = CEvent.Run.class;
    private static final Class<CEvent.End> END_EVENT = CEvent.End.class;


    private StateMachine<CState, CEvent> stateMachine;

    @Test
    void createsStateMachineWitlessHierarchy() {
        stateMachine = new StateMachineBuilder<CState, CEvent>(new CState.Init())
                .addTransition(INIT, RUN_EVENT, (st) -> running)
                .addTransition(RUNNING, END_EVENT, (st) -> completed)
                .build();


        assertState(INIT);

        stateMachine.apply(new CEvent.Run());
        assertState(RUNNING);
        stateMachine.apply(new CEvent.End());
        assertState(COMPLETED);

    }

    @Test
    void executesOnExitWhenExitingTheGivenState() {
        AtomicBoolean exited = new AtomicBoolean(false);
        stateMachine = new StateMachineBuilder<CState, CEvent>(new CState.Init())
                .addTransition(INIT, RUN_EVENT, (st) -> running)
                .addTransition(RUNNING, END_EVENT, (st) -> completed)
                .onExit(RUNNING, (e) -> exited.set(true)) // TODO: Possible bug, it should not be called in order
                .build();

        stateMachine.apply(new CEvent.Run());
        stateMachine.apply(new CEvent.End());

        assertTrue(exited.get(), "onExit function was not called");


    }

    @Test
    void executesOnEnterWhenEnterTheGivenState() {
        AtomicBoolean exited = new AtomicBoolean(false);
        AtomicBoolean entered = new AtomicBoolean(false);
        stateMachine = new StateMachineBuilder<CState, CEvent>(new CState.Init())
                .addTransition(INIT, RUN_EVENT, (st) -> running)
                .addTransition(RUNNING, END_EVENT, (st) -> completed)
                .onExit(RUNNING, (e) -> exited.set(true)) // TODO: Possible bug, it should not be called in order
                .build();

        stateMachine.apply(new CEvent.Run());
        stateMachine.apply(new CEvent.End());

        assertFalse(exited.get(), "onExit function should not be called");
    }

    private void assertState(Class<? extends CState> expected) {
        CState actualState = stateMachine.getState().get();
        assertTrue(expected.isInstance(actualState), String.format("Expected state (%s) is not equal to current state (%s)", expected, actualState.getClass()));
    }

    public static abstract class CState {


        public static class Init extends CState {
        }

        public static class Running extends CState {
        }

        public static class Completed extends CState {
        }
    }

    public static abstract class CEvent {
        public static class Run extends CEvent {
        }

        public static class End extends CEvent {
        }

        public static class NotUsed extends CEvent {
        }
    }
}