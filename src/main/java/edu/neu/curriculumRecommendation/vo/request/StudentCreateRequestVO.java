package edu.neu.curriculumRecommendation.vo.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Student Create Request VO
 * Request object for creating a new student
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentCreateRequestVO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String studentId;

    private String major;

    @DecimalMin(value = "0.0", message = "GPA must be at least 0.0")
    @DecimalMax(value = "4.0", message = "GPA must not exceed 4.0")
    private Double gpa;

    private Integer enrollmentYear;

    private String careerInterests;
}

