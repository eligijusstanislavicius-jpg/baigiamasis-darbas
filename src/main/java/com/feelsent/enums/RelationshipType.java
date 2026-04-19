package com.feelsent.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RelationshipType {
    MOTHER("Mama"),
    FATHER("Tėtis"),
    CHILD("Vaikas"),
    PARTNER("Partneris"),
    FRIEND("Draugas"),
    BROTHER("Brolis"),
    SISTER("Sesuo"),
    GRANDFATHER("Senelis"),
    GRANDMOTHER("Močiutė");

    private final String label;
}
