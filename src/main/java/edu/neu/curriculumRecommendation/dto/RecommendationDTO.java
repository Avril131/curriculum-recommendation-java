package edu.neu.curriculumRecommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationDTO {

    private Long id;

    private Long studentId;

    private Long courseId;

    // Redundant fields for display
    private String courseCode;

    private String courseName;

    private Double matchScore;

    private String reason;

    private LocalDateTime recommendedAt;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

