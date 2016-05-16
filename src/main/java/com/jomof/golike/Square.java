package com.jomof.golike;

import org.jetbrains.annotations.NotNull;

/**
 * A abstract board square.
 */
abstract class Square {
    @NotNull
    final public String name;

    @SuppressWarnings("unused")
    Square(@NotNull String name) {
        this.name = name;
    }
}
