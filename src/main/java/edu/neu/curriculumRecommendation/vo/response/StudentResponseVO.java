package edu.neu.curriculumRecommendation.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Student Response VO
 * Response object for student information returned to frontend
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponseVO {

    private Long id;

    private Long userId;

    private String firstName;

    private String lastName;

    private String studentId;

    private String major;

    private Double gpa;

    private Integer enrollmentYear;

    private String careerInterests;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

