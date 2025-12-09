package edu.neu.curriculumRecommendation.service;

import edu.neu.curriculumRecommendation.dto.CourseDTO;

import java.util.List;

/**
 * Course Service Interface
 * Provides business logic for course management
 */
public interface CourseService {

    /**
     * Create a new course
     *
     * @param courseDTO Course data transfer object
     * @return Created course DTO
     */
    CourseDTO createCourse(CourseDTO courseDTO);

    /**
     * Update course information
     *
     * @param id Course ID
     * @param courseDTO Updated course data
     * @return Updated course DTO
     */
    CourseDTO updateCourse(Long id, CourseDTO courseDTO);

    /**
     * Find course by ID
     *
     * @param id Course ID
     * @return Course DTO
     */
    CourseDTO findById(Long id);

    /**
     * Find all courses
     *
     * @return List of course DTOs
     */
    List<CourseDTO> findAll();

    /**
     * Find course by course code
     *
     * @param courseCode Course code
     * @return Course DTO
     */
    CourseDTO findByCourseCode(String courseCode);

    /**
     * Find courses by difficulty
     *
     * @param difficulty Difficulty level
     * @return List of course DTOs
     */
    List<CourseDTO> findByDifficulty(String difficulty);

    /**
     * Find all active courses
     *
     * @return List of active course DTOs
     */
    List<CourseDTO> findActiveCourses();

    /**
     * Find courses by department
     *
     * @param department Department name
     * @return List of course DTOs
     */
    List<CourseDTO> findByDepartment(String department);

    /**
     * Find courses not completed by student
     *
     * @param studentId Student ID
     * @return List of course DTOs
     */
    List<CourseDTO> findCoursesNotCompletedByStudent(Long studentId);

    /**
     * Delete course
     *
     * @param id Course ID
     */
    void deleteCourse(Long id);
}

