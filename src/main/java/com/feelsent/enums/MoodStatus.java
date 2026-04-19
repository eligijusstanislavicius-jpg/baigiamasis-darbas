package com.feelsent.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MoodStatus {
    HAPPY("Laimingas"),
    SAD("Liūdnas"),
    TIRED("Pavargęs"),
    ENERGETIC("Energingas"),
    ANXIOUS("Nerimastingas"),
    SICK("Sergantis"),
    STRESSED("Stresas"),
    CALM("Ramus"),
    LAZY("Tingus"),
    HARD_TIME("Sunkus laikotarpis"),
    ON_VACATION("Atostogauju"),
    NOSTALGIC("Nostalgiškas");

    private final String label;
}
