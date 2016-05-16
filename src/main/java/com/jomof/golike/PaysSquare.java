package com.jomof.golike;

import org.jetbrains.annotations.NotNull;

/**
 * A square that pays the user a given amount when it is passed.
 */
class PaysSquare extends Square {
    final int amount;

    @SuppressWarnings("unused")
    PaysSquare(@NotNull String name, @NotNull int amount) {
        super(name);
        this.amount = amount;
    }
}
