package com.feelsent.dto;

import com.feelsent.enums.MoodStatus;
import com.feelsent.enums.MoodWant;
import com.feelsent.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private MoodStatus moodStatus;
    private String moodStatusLabel;
    private MoodWant moodWant;
    private String moodWantLabel;
    private Integer points;
    private Role role;
    private LocalDateTime lastLoginAt;
}
