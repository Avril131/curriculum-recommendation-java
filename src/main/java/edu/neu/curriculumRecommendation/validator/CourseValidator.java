package edu.neu.curriculumRecommendation.validator;

import edu.neu.curriculumRecommendation.exception.DuplicateResourceException;
import edu.neu.curriculumRecommendation.exception.ResourceNotFoundException;
import edu.neu.curriculumRecommendation.exception.ValidationException;
import edu.neu.curriculumRecommendation.mapper.repository.CourseRepository;
import edu.neu.curriculumRecommendation.vo.request.CourseCreateRequestVO;
import edu.neu.curriculumRecommendation.vo.request.CourseUpdateRequestVO;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Course Validator
 * Validates course-related business rules
 */
@Component
public class CourseValidator {

    private static final Set<String> ALLOWED_DIFFICULTIES = Set.of("BEGINNER", "INTERMEDIATE", "ADVANCED");

    private final CourseRepository courseRepository;

    public CourseValidator(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    /**
     * Validate course code
     *
     * @param courseCode course code to validate
     */
    public void validateCourseCode(String courseCode) {
        if (courseCode == null || courseCode.isBlank()) {
            throw new ValidationException("Course code cannot be empty");
        }
        // Optional format check: letters and digits only
        if (!courseCode.matches("[A-Za-z0-9]+")) {
            throw new ValidationException("Course code must contain only letters and digits");
        }
    }

    /**
     * Validate credits
     *
     * @param credits credits to validate
     */
    public void validateCredits(Integer credits) {
        if (credits == null) {
            throw new ValidationException("Credits cannot be null");
        }
        if (credits < 1 || credits > 4) {
            throw new ValidationException("Credits must be between 1 and 4");
        }
    }

    /**
     * Validate difficulty
     *
     * @param difficulty difficulty to validate
     */
    public void validateDifficulty(String difficulty) {
        if (difficulty == null || difficulty.isBlank()) {
            throw new ValidationException("Difficulty must be BEGINNER, INTERMEDIATE, or ADVANCED");
        }
        if (!ALLOWED_DIFFICULTIES.contains(difficulty.toUpperCase())) {
            throw new ValidationException("Difficulty must be BEGINNER, INTERMEDIATE, or ADVANCED");
        }
    }

    /**
     * Validate duplicate course code
     *
     * @param courseCode course code to check
     */
    public void validateDuplicateCourseCode(String courseCode) {
        if (courseCode != null && !courseCode.isBlank() && courseRepository.existsByCourseCode(courseCode)) {
            throw new DuplicateResourceException("Course code already exists: " + courseCode);
        }
    }

    /**
     * Validate course existence
     *
     * @param id course id to check
     */
    public void validateCourseExists(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course not found with id: " + id);
        }
    }

    /**
     * Validate create request
     *
     * @param requestVO create request VO
     */
    public void validateForCreate(CourseCreateRequestVO requestVO) {
        validateCourseCode(requestVO.getCourseCode());
        validateCredits(requestVO.getCredits());
        validateDifficulty(requestVO.getDifficulty());
        validateDuplicateCourseCode(requestVO.getCourseCode());
    }

    /**
     * Validate update request
     *
     * @param id        course id
     * @param requestVO update request VO
     */
    public void validateForUpdate(Long id, CourseUpdateRequestVO requestVO) {
        validateCourseExists(id);
        validateCredits(requestVO.getCredits());
        validateDifficulty(requestVO.getDifficulty());
    }
}

