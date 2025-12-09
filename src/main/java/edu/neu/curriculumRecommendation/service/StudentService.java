package edu.neu.curriculumRecommendation.service;

import edu.neu.curriculumRecommendation.dto.StudentDTO;

import java.util.List;

/**
 * Student Service Interface
 * Provides business logic for student management
 */
public interface StudentService {

    /**
     * Create a new student profile
     *
     * @param studentDTO Student data transfer object
     * @return Created student DTO
     */
    StudentDTO createStudent(StudentDTO studentDTO);

    /**
     * Update student information
     *
     * @param id Student ID
     * @param studentDTO Updated student data
     * @return Updated student DTO
     */
    StudentDTO updateStudent(Long id, StudentDTO studentDTO);

    /**
     * Find student by ID
     *
     * @param id Student ID
     * @return Student DTO
     */
    StudentDTO findById(Long id);

    /**
     * Find all students
     *
     * @return List of student DTOs
     */
    List<StudentDTO> findAll();

    /**
     * Find student by user ID
     *
     * @param userId User ID
     * @return Student DTO
     */
    StudentDTO findByUserId(Long userId);

    /**
     * Find student by student ID (student number)
     *
     * @param studentId Student ID (student number)
     * @return Student DTO
     */
    StudentDTO findByStudentId(String studentId);

    /**
     * Find students by major
     *
     * @param major Major name
     * @return List of student DTOs
     */
    List<StudentDTO> findByMajor(String major);

    /**
     * Delete student profile
     *
     * @param id Student ID
     */
    void deleteStudent(Long id);
}

