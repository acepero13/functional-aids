package com.acepero13.functionalaids.state;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CoinStateMachineTest {

    /**
     * Inserting a coin into a locked machine will cause it to unlock if there’s any candy left.
     * Turning the knob on an unlocked machine will cause it to dispense candy and become locked.
     * Turning the knob on a locked machine or inserting a coin into an unlocked machine does nothing.
     * A machine that’s out of candy ignores all inputs.
     */
    private final UnaryOperator<STATE> NO_CHANGE = (STATE st) -> st;
    private final StateMachine<STATE, Evt> stateMachine = new StateMachineBuilder<STATE, Evt>(STATE.LOCK(10, 5))
            .addTransition(STATE.LOCKED, Evt.COIN, (STATE st) -> STATE.UNLOCK(st.coins + 1, st.candies), (STATE st) -> st.candies > 0) // Condition
            .addTransition(STATE.UNLOCKED, Evt.TURN, (STATE st) -> STATE.LOCK(st.coins, st.candies - 1))
            .addTransition(STATE.LOCKED, Evt.TURN, NO_CHANGE)
            .addTransition(STATE.UNLOCKED, Evt.COIN, NO_CHANGE)
            .build();





    @Test
    void initialStateShouldBeLocked() {
        assertEquals(STATE.LOCKED, stateMachine.getState());
    }

    @Test
    void insertACoinInALockMachineWithCandiesUnlocksIt() {
        stateMachine.apply(Evt.COIN);
        assertEquals(STATE.UNLOCKED, stateMachine.getState());
    }

    @Test void sequenceOfActions(){
        //val inputs = List(Turn, Coin, Turn, Coin, Turn, Coin, Turn, Coin, Turn)
        List<Evt> eventList = Arrays.asList( Evt.TURN, Evt.COIN, Evt.TURN, Evt.COIN, Evt.TURN, Evt.COIN, Evt.TURN, Evt.COIN, Evt.TURN);
        eventList.forEach(stateMachine::apply);
        STATE actual = stateMachine.getState();
        assertEquals(STATE.LOCKED, actual);
        assertEquals(14, actual.coins);
        assertEquals(1, actual.candies);
    }

    @Test
    void turningTheKnobInAnUnlockedMachineDispensesCandyAndBecomesLocked() {
        stateMachine.apply(Evt.COIN);
        assertEquals(STATE.UNLOCKED, stateMachine.getState());
        stateMachine.apply(Evt.TURN);
        STATE actual = stateMachine.getState();
        assertEquals( STATE.LOCKED, actual);
        assertEquals(4, actual.candies);
    }

    @Test void turningTheKnobOnALockedMachineDoesNothing(){
        stateMachine.apply(Evt.TURN);
        assertEquals(STATE.LOCKED, stateMachine.getState());
    }

    @Test void insertingACoinInAnUnlockedMachineDoesNothing(){
        stateMachine.apply(Evt.COIN);
        assertEquals(STATE.UNLOCKED, stateMachine.getState());
        stateMachine.apply(Evt.COIN);
        assertEquals(STATE.UNLOCKED, stateMachine.getState());
    }

    @Test void insertingACoinIntoALockedMachineWithoutCandiesIgnoresCommand(){
        final StateMachine<STATE, Evt> stateMachineNoCandies = new StateMachineBuilder<STATE, Evt>(STATE.LOCK(10, 0))
                .addTransition(STATE.LOCKED, Evt.COIN, (STATE st) -> STATE.UNLOCK(st.coins + 1, st.candies), (STATE st) -> st.candies > 0) // Condition
                .addTransition(STATE.UNLOCKED, Evt.TURN, (STATE st) -> STATE.LOCK(st.coins, st.candies - 1))
                .addTransition(STATE.LOCKED, Evt.TURN, NO_CHANGE)
                .addTransition(STATE.UNLOCKED, Evt.COIN, NO_CHANGE)
                .build();

        stateMachineNoCandies.apply(Evt.COIN);
        STATE actual = stateMachineNoCandies.getState();
        assertEquals(STATE.LOCKED, actual);
        assertEquals(10, actual.coins);
        assertEquals(0, actual.candies);
    }

    enum STATE {
        LOCKED(0, 0, true), UNLOCKED(0, 0, false);
        private int coins;
        private int candies;
        private final boolean locked;

        STATE(int coins, int candies, boolean locked) {
            this.coins = coins;
            this.candies = candies;
            this.locked = locked;
        }


        public static STATE LOCK(int coins, int candies) {
            STATE st = LOCKED;
            st.coins = coins;
            st.candies = candies;
            return st;
        }

        public static STATE UNLOCK(int coins, int candies) {
            STATE st = UNLOCKED;
            st.coins = coins;
            st.candies = candies;
            return st;
        }
    }

    enum Evt implements Event {
        COIN, TURN
    }


}
