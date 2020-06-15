package com.example.challengeup.dto;

import com.example.challengeup.backend.OrderBy;
import com.example.challengeup.backend.OrderDirection;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeSearchDTO {
    private String query;
    private List<String> categories;
    private Integer liked;
    private Integer accepted;
    private Integer completed;
    private Integer rp;
    private OrderBy orderBy;
    private OrderDirection direction;
}
