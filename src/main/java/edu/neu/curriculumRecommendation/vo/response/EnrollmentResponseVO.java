package edu.neu.curriculumRecommendation.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentResponseVO {

    private Long id;

    private Long studentId;

    private CourseResponseVO course;

    private String semester;

    private Integer year;

    private String grade;

    // GPA value corresponding to the grade (e.g., A- -> 3.667)
    private Double gradePoint;

    private String status;

    private LocalDate completedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

