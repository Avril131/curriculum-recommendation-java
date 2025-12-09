package edu.neu.curriculumRecommendation.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Recommendation Response VO
 * Contains recommended course info for presentation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationResponseVO {

    // Can be null for on-the-fly recommendations
    private Long id;

    private Long courseId;

    private String courseCode;

    private String courseName;

    private String description;

    private Integer credits;

    private String difficulty;

    // 0.0 - 100.0
    private Double matchScore;

    private String reason;

    // PENDING, ACCEPTED, REJECTED
    private String status;

    // Can be null for on-the-fly recommendations
    private LocalDateTime recommendedAt;

    // Full prerequisite chain (all levels), sorted by level asc
    private List<PrerequisiteCourseVO> prerequisiteChain;

    // Missing prerequisites that the student has not completed
    private List<PrerequisiteCourseVO> missingPrerequisites;

    // Whether all prerequisite requirements are satisfied
    private Boolean allPrerequisitesMet;
}

