package com.acepero13.functionalaids.state;

import java.lang.reflect.GenericDeclaration;
import java.util.Optional;

public interface Event {
    default Optional<GenericDeclaration> generic(){
        return Optional.empty();
    }


}
