package com.example.challengeup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeDTO {
    private String name;
    private String description;
    private String accepted;
    private String completed;
    private String liked;
}
