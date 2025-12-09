package edu.neu.curriculumRecommendation.validator;

import edu.neu.curriculumRecommendation.exception.DuplicateResourceException;
import edu.neu.curriculumRecommendation.exception.ResourceNotFoundException;
import edu.neu.curriculumRecommendation.exception.ValidationException;
import edu.neu.curriculumRecommendation.mapper.repository.StudentRepository;
import edu.neu.curriculumRecommendation.vo.request.StudentCreateRequestVO;
import edu.neu.curriculumRecommendation.vo.request.StudentUpdateRequestVO;
import org.springframework.stereotype.Component;

/**
 * Student Validator
 * Validates student-related business rules
 */
@Component
public class StudentValidator {

    private final StudentRepository studentRepository;

    public StudentValidator(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * Validate GPA value
     *
     * @param gpa GPA value to validate
     * @throws ValidationException if GPA is out of valid range
     */
    public void validateGPA(Double gpa) {
        if (gpa != null && (gpa < 0.0 || gpa > 4.0)) {
            throw new ValidationException("GPA must be between 0.0 and 4.0");
        }
    }

    /**
     * Validate student ID format
     *
     * @param studentId Student ID to validate
     * @throws ValidationException if student ID is invalid
     */
    public void validateStudentId(String studentId) {
        if (studentId == null || studentId.isBlank()) {
            throw new ValidationException("Student ID cannot be empty");
        }
        // Optional: Validate format (e.g., must be 9 digits)
        if (!studentId.matches("\\d{9}")) {
            throw new ValidationException("Student ID must be 9 digits");
        }
    }

    /**
     * Validate if student ID already exists
     *
     * @param studentId Student ID to check
     * @throws DuplicateResourceException if student ID already exists
     */
    public void validateDuplicateStudentId(String studentId) {
        if (studentId != null && !studentId.isBlank() && studentRepository.existsByStudentId(studentId)) {
            throw new DuplicateResourceException("Student ID already exists: " + studentId);
        }
    }

    /**
     * Validate if student exists
     *
     * @param id Student ID to check
     * @throws ResourceNotFoundException if student does not exist
     */
    public void validateStudentExists(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }
    }

    /**
     * Validate student data for creation
     *
     * @param requestVO Student create request VO
     */
    public void validateForCreate(StudentCreateRequestVO requestVO) {
        validateGPA(requestVO.getGpa());
        if (requestVO.getStudentId() != null && !requestVO.getStudentId().isBlank()) {
            validateStudentId(requestVO.getStudentId());
            validateDuplicateStudentId(requestVO.getStudentId());
        }
    }

    /**
     * Validate student data for update
     *
     * @param id Student ID
     * @param requestVO Student update request VO
     */
    public void validateForUpdate(Long id, StudentUpdateRequestVO requestVO) {
        validateStudentExists(id);
        validateGPA(requestVO.getGpa());
    }
}

