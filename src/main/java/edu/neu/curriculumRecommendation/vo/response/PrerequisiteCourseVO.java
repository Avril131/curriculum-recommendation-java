package edu.neu.curriculumRecommendation.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Prerequisite Course VO
 * Represents prerequisite course chain, completion status, and depth level
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrerequisiteCourseVO {

    private Long courseId;

    private String courseCode;

    private String courseName;

    private Integer credits;

    // Whether the student has completed this prerequisite
    private Boolean isCompleted;

    // Prerequisite depth level: 1=direct, 2=indirect, etc.
    private Integer level;
}

