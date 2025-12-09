package edu.neu.curriculumRecommendation.service;

import edu.neu.curriculumRecommendation.dto.EnrollmentDTO;

import java.util.List;

/**
 * Enrollment Service Interface
 * Handles enrollment operations and GPA recalculation
 */
public interface EnrollmentService {

    EnrollmentDTO createEnrollment(EnrollmentDTO enrollmentDTO);

    EnrollmentDTO updateEnrollment(Long id, EnrollmentDTO enrollmentDTO);

    List<EnrollmentDTO> findByStudentId(Long studentId);

    List<EnrollmentDTO> findByStudentIdAndStatus(Long studentId, String status);

    EnrollmentDTO findById(Long id);

    void deleteEnrollment(Long id);

    /**
     * Recalculate student's GPA based on enrollments
     *
     * @param studentId student id
     */
    void recalculateStudentGPA(Long studentId);
}

