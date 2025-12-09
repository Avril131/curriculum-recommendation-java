package edu.neu.curriculumRecommendation.vo.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentRequestVO {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @NotBlank(message = "Semester is required")
    private String semester; // FALL, SPRING, SUMMER

    @NotNull(message = "Year is required")
    private Integer year;

    // Optional: letter grade
    private String grade;

    @NotBlank(message = "Status is required")
    private String status; // COMPLETED, IN_PROGRESS, DROPPED

    // Optional: completion date
    private LocalDate completedAt;
}

