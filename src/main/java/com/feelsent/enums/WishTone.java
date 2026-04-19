package com.feelsent.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WishTone {
    SIMPLE("Paprastas"),
    SUPPORTIVE("Palaikantis"),
    FUNNY("Juokingas"),
    ROMANTIC("Romantiškas"),
    BIRTHDAY("Gimtadieninis");

    private final String label;
}
