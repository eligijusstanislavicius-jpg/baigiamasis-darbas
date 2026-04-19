package com.feelsent.dto;

import com.feelsent.enums.MoodStatus;
import com.feelsent.enums.MoodWant;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private MoodStatus moodStatus;
    private String moodStatusLabel;
    private MoodWant moodWant;
    private String moodWantLabel;
    private Integer points;
}
