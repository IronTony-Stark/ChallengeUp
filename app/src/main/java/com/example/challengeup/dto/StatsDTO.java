package com.example.challengeup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatsDTO {
    private String rank;
    private String totalRpEarned;
    private String challengesCreated;
    private String challengesCompleted;
    private String followers;
    private String following;
    private String achievements;
    private String challengesInProgress;
}
