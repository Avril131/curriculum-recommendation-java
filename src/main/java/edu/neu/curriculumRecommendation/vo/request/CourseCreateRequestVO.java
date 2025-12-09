package edu.neu.curriculumRecommendation.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Course Create Request VO
 * Request object for creating a new course
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseCreateRequestVO {

    @NotBlank(message = "Course code is required")
    private String courseCode;

    @NotBlank(message = "Course name is required")
    private String courseName;

    private String description;

    @NotNull(message = "Credits is required")
    @Min(value = 1, message = "Credits must be at least 1")
    @Max(value = 4, message = "Credits must not exceed 4")
    private Integer credits;

    @NotBlank(message = "Difficulty is required")
    private String difficulty;

    private String department;

    private String semester;

    @Builder.Default
    private Boolean isActive = true;

    private String instructor;

    @Email(message = "Invalid email format")
    private String instructorEmail;

    private String location;

    private String schedule;

    private String deliveryMode;

    private String workload;

    private String language;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    private String syllabus;

    private String courseUrl;

    private String textbook;

    // Optional: career tags to improve recommendation matching
    private String careerTags;
}

