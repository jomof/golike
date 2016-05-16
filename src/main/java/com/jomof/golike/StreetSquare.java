package com.jomof.golike;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A street square
 */
class StreetSquare extends Square {
    final int price;
    @NotNull
    final List<Integer> rents;
    @NotNull
    final PropertyColor color;

    @SuppressWarnings("unused")
    StreetSquare(@NotNull String name, int price, @NotNull List<Integer> rents, @NotNull PropertyColor color) {
        super(name);
        this.price = price;
        this.rents = rents;
        this.color = color;
    }
}
