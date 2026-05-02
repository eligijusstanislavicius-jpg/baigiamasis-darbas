package com.feelsent.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RelationshipType {
    MOTHER("Mama"),
    FATHER("Tėtis"),
    SON("Sūnus"),
    DAUGHTER("Duktė"),
    PARTNER("Partneris"),
    HUSBAND("Vyras"),
    WIFE("Žmona"),
    FRIEND("Draugas"),
    BROTHER("Brolis"),
    SISTER("Sesuo"),
    GRANDFATHER("Senelis"),
    GRANDMOTHER("Močiutė"),
    SON_IN_LAW("Zentas"),
    DAUGHTER_IN_LAW("Marti"),
    ALL("Visiems");

    private final String label;
}
