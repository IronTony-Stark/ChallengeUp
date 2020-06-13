package com.example.challengeup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopChallengeDTO {
    private String rank;
    private String name;
    private String avatar;
    private String accepted;
    private String completed;
    private String liked;
}
