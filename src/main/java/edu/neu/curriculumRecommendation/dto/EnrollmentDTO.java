package edu.neu.curriculumRecommendation.dto;

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
public class EnrollmentDTO {

    private Long id;

    private Long studentId;

    private Long courseId;

    private String semester;

    private Integer year;

    private String grade;

    private String status;

    private LocalDate completedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

