package edu.neu.curriculumRecommendation.service.impl;

import edu.neu.curriculumRecommendation.dto.CourseDTO;
import edu.neu.curriculumRecommendation.entity.Course;
import edu.neu.curriculumRecommendation.exception.ResourceNotFoundException;
import edu.neu.curriculumRecommendation.mapper.converter.CourseConverter;
import edu.neu.curriculumRecommendation.mapper.repository.CourseRepository;
import edu.neu.curriculumRecommendation.service.CourseService;
import edu.neu.curriculumRecommendation.validator.CourseValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Course Service Implementation
 */
@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseConverter courseConverter;
    private final CourseValidator validator;

    public CourseServiceImpl(CourseRepository courseRepository,
                             CourseConverter courseConverter,
                             CourseValidator validator) {
        this.courseRepository = courseRepository;
        this.courseConverter = courseConverter;
        this.validator = validator;
    }

    @Override
    public CourseDTO createCourse(CourseDTO courseDTO) {
        validator.validateCourseCode(courseDTO.getCourseCode());
        validator.validateCredits(courseDTO.getCredits());
        validator.validateDifficulty(courseDTO.getDifficulty());
        validator.validateDuplicateCourseCode(courseDTO.getCourseCode());

        Course course = courseConverter.dtoToEntity(courseDTO);
        Course savedCourse = courseRepository.save(course);
        return courseConverter.entityToDto(savedCourse);
    }

    @Override
    public CourseDTO updateCourse(Long id, CourseDTO courseDTO) {
        validator.validateCourseExists(id);
        validator.validateCredits(courseDTO.getCredits());
        validator.validateDifficulty(courseDTO.getDifficulty());

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        course.setCourseCode(courseDTO.getCourseCode());
        course.setCourseName(courseDTO.getCourseName());
        course.setDescription(courseDTO.getDescription());
        course.setCredits(courseDTO.getCredits());
        course.setDifficulty(courseDTO.getDifficulty());
        course.setDepartment(courseDTO.getDepartment());
        course.setSemester(courseDTO.getSemester());
        course.setIsActive(courseDTO.getIsActive());

        Course updatedCourse = courseRepository.save(course);
        return courseConverter.entityToDto(updatedCourse);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDTO findById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return courseConverter.entityToDto(course);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> findAll() {
        List<Course> courses = courseRepository.findAll();
        return courseConverter.entitiesToDtos(courses);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDTO findByCourseCode(String courseCode) {
        Course course = courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with courseCode: " + courseCode));
        return courseConverter.entityToDto(course);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> findByDifficulty(String difficulty) {
        List<Course> courses = courseRepository.findByDifficulty(difficulty);
        return courseConverter.entitiesToDtos(courses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> findActiveCourses() {
        List<Course> courses = courseRepository.findByIsActiveTrue();
        return courseConverter.entitiesToDtos(courses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> findByDepartment(String department) {
        List<Course> courses = courseRepository.findByDepartment(department);
        return courseConverter.entitiesToDtos(courses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> findCoursesNotCompletedByStudent(Long studentId) {
        List<Course> courses = courseRepository.findCoursesNotCompletedByStudent(studentId);
        return courseConverter.entitiesToDtos(courses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> searchCourses(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        List<Course> courses = courseRepository.searchCourses(query.trim());
        return courseConverter.entitiesToDtos(courses);
    }

    @Override
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
        courseRepository.delete(course);
    }
}

