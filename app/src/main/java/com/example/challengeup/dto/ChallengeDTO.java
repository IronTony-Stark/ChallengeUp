package com.example.challengeup.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class ChallengeDTO {
    private String name;
    private String description;
    private String accepted;
    private String completed;
    private String liked;
    private String rp;
    private String reportCount;
}
