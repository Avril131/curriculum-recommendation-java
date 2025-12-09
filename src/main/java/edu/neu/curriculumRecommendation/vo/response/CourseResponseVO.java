package edu.neu.curriculumRecommendation.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Course Response VO
 * Response object for course information returned to frontend
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponseVO {

    private Long id;

    private String courseCode;

    private String courseName;

    private String description;

    private Integer credits;

    private String difficulty;

    private String department;

    private String semester;

    private Boolean isActive;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

