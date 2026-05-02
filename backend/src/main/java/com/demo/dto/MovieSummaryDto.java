package com.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieSummaryDto {
    private Long id;
    private String title;
    private Integer productionYear;
    private String thumbnailUrl;
}
