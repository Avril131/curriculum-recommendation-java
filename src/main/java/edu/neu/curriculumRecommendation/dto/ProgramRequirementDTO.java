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
public class ProgramRequirementDTO {

    private Long id;

    private String major;

    private Long courseId;

    private String requirementType;

    private Integer creditsRequired;

    private Boolean isMandatory;

    private LocalDateTime createdAt;
}

