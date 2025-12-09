package edu.neu.curriculumRecommendation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "program_requirements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "major", nullable = false, length = 100)
    private String major;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "requirement_type", nullable = false, length = 50)
    private String requirementType;

    @Column(name = "credits_required", nullable = false)
    private Integer creditsRequired;

    @Column(name = "is_mandatory", nullable = false)
    private Boolean isMandatory;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}


