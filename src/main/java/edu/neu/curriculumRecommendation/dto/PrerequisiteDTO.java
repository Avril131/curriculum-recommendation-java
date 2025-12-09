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
public class PrerequisiteDTO {

    private Long id;

    private Long courseId;

    private Long prerequisiteCourseId;

    private LocalDateTime createdAt;
}

