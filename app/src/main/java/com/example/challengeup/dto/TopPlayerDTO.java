package com.example.challengeup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopPlayerDTO {
    private String rank;
    private String avatar;
    private String name;
    private String rp;
}
