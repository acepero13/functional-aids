package com.acepero13.functionalaids.state;

import java.util.function.Consumer;

public interface Node<S, E extends Event> {

    void addOnExitListener(Consumer<E> consumer);

    void addOnEnterListener(Consumer<E> consumer);

    void notifyOnEnter(E event);
    S getState();

    void addOnExitListener(Runnable runnable);

    void addOnEnterListener(Runnable runnable);

    void connect(Node<S, E> toState, E event);

    boolean canTransitionBy(E event);

    Node<S,E> fire(E event);
}
