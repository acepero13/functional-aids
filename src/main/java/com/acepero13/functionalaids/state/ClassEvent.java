package com.acepero13.functionalaids.state;

import java.lang.reflect.GenericDeclaration;
import java.util.Optional;

public interface ClassEvent extends Event {
    static <E extends Event> E of(GenericDeclaration event) {
        return (E) new ClassEvent(){
            public Optional<GenericDeclaration> generic(){
                return Optional.of(event);
            }

        };
    }
}
