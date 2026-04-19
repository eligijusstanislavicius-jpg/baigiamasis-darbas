package com.feelsent.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SendMode {
    SIMPLE("Paprastas"),
    GUESS("Atspėk"),
    PASSIVE("Tylus");

    private final String label;
}
