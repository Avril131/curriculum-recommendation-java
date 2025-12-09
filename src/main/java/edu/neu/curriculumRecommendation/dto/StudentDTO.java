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
public class StudentDTO {

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

