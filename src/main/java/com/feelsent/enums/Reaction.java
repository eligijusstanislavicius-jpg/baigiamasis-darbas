package com.feelsent.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Reaction {
    WARMED_UP("🤗", "Sušildė"),
    COMFORTED("🫂", "Paguodė"),
    INSPIRED("✨", "Įkvėpė"),
    CHEERED_UP("😊", "Pradžiugino"),
    SURPRISED("🎉", "Nustebino"),
    CALMED("🕊️", "Nuramino");

    private final String emoji;
    private final String label;
}
