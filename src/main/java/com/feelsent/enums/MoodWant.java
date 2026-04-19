package com.feelsent.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MoodWant {
    CHEER_ME_UP("Pralinksmink mane"),
    SUPPORT_ME("Palaikyk mane"),
    INSPIRE_ME("Įkvėpk mane"),
    GOOD_DAY("Geros dienos"),
    SWEET_DREAMS("Saldžių sapnų"),
    SURPRISE_ME("Nustebink mane"),
    CALM_ME_DOWN("Nuramink mane"),
    JUST_BE_THERE("Tyliai šalia");

    private final String label;
}
