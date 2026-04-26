package com.feelsent.enums;

public enum NotificationType {
    FRIEND_REQUEST,          // gautas draugystės pakvietimas
    FRIEND_REQUEST_ACCEPTED, // draugystės pakvietimas priimtas
    NEW_MESSAGE,             // gautas naujas palinkėjimas
    MESSAGE_REACTED,         // gavėjas sureagavo į tavo palinkėjimą
    GUESS_CORRECT,           // gavėjas teisingai atspėjo tavo palinkėjimo toną
    MESSAGE_EXPIRED,         // gavėjas nesureagavo per 2 dienas – žinutė ištrinta
    FRIEND_REMOVED,          // tave pašalino iš draugų sąrašo
    MESSAGE_LIMIT_SET,       // draugas apribojo tavo žinučių siuntimą
    ADMIN_MESSAGE,           // pranešimas nuo administratoriaus
    CONTACT_ADMIN            // vartotojo žinutė administratoriui
}