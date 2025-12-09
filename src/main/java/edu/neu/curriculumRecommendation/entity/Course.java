package edu.neu.curriculumRecommendation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "course_code", nullable = false, unique = true, length = 20)
    private String courseCode;

    @Column(name = "course_name", nullable = false, length = 200)
    private String courseName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "credits", nullable = false)
    private Integer credits;

    @Column(name = "difficulty", nullable = false, length = 20)
    private String difficulty;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "semester", length = 20)
    private String semester;

    @Column(name = "is_active", nullable = false, columnDefinition = "boolean default true")
    private Boolean isActive = true;

    @Column(name = "instructor", length = 100)
    private String instructor;

    @Column(name = "instructor_email", length = 100)
    private String instructorEmail;

    @Column(name = "location", length = 200)
    private String location;

    @Column(name = "schedule", length = 200)
    private String schedule;

    @Column(name = "delivery_mode", length = 50)
    private String deliveryMode;

    @Column(name = "workload", length = 50)
    private String workload;

    @Column(name = "language", length = 50)
    private String language;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "current_enrollment", columnDefinition = "integer default 0")
    private Integer currentEnrollment = 0;

    @Column(name = "syllabus", columnDefinition = "TEXT")
    private String syllabus;

    @Column(name = "course_url", length = 500)
    private String courseUrl;

    @Column(name = "textbook", length = 500)
    private String textbook;

    @Column(name = "career_tags", length = 500)
    private String careerTags;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Enrollment> enrollments = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Prerequisite> prerequisites = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ProgramRequirement> programRequirements = new HashSet<>();
}

