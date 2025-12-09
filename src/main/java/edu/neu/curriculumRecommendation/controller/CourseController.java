package edu.neu.curriculumRecommendation.controller;

import edu.neu.curriculumRecommendation.mapper.converter.CourseVOConverter;
import edu.neu.curriculumRecommendation.service.CourseService;
import edu.neu.curriculumRecommendation.vo.request.CourseCreateRequestVO;
import edu.neu.curriculumRecommendation.vo.request.CourseUpdateRequestVO;
import edu.neu.curriculumRecommendation.vo.response.CourseResponseVO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Course Controller
 * REST API endpoints for course management
 */
@RestController
@RequestMapping("/courses")
@CrossOrigin(origins = "http://localhost:3000")
public class CourseController {

    private final CourseService courseService;
    private final CourseVOConverter voConverter;

    public CourseController(CourseService courseService, CourseVOConverter voConverter) {
        this.courseService = courseService;
        this.voConverter = voConverter;
    }

    /**
     * Get all courses
     */
    @GetMapping
    public ResponseEntity<List<CourseResponseVO>> getAllCourses() {
        List<CourseResponseVO> courses = voConverter.dtosToResponses(courseService.findAll());
        return ResponseEntity.ok(courses);
    }

    /**
     * Get course by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseVO> getCourseById(@PathVariable Long id) {
        try {
            CourseResponseVO course = voConverter.dtoToResponse(courseService.findById(id));
            return ResponseEntity.ok(course);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create a new course
     */
    @PostMapping
    public ResponseEntity<CourseResponseVO> createCourse(@Valid @RequestBody CourseCreateRequestVO requestVO) {
        CourseResponseVO createdCourse = voConverter.dtoToResponse(
                courseService.createCourse(voConverter.requestToDto(requestVO))
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCourse);
    }

    /**
     * Update course information
     */
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseVO> updateCourse(@PathVariable Long id, @Valid @RequestBody CourseUpdateRequestVO requestVO) {
        try {
            CourseResponseVO updatedCourse = voConverter.dtoToResponse(
                    courseService.updateCourse(id, voConverter.requestToDto(requestVO))
            );
            return ResponseEntity.ok(updatedCourse);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete course
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        try {
            courseService.deleteCourse(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get course by course code
     */
    @GetMapping("/code/{courseCode}")
    public ResponseEntity<CourseResponseVO> getCourseByCourseCode(@PathVariable String courseCode) {
        try {
            CourseResponseVO course = voConverter.dtoToResponse(courseService.findByCourseCode(courseCode));
            return ResponseEntity.ok(course);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get all active courses
     */
    @GetMapping("/active")
    public ResponseEntity<List<CourseResponseVO>> getActiveCourses() {
        List<CourseResponseVO> courses = voConverter.dtosToResponses(courseService.findActiveCourses());
        return ResponseEntity.ok(courses);
    }

    /**
     * Get courses by difficulty
     */
    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<CourseResponseVO>> getCoursesByDifficulty(@PathVariable String difficulty) {
        List<CourseResponseVO> courses = voConverter.dtosToResponses(courseService.findByDifficulty(difficulty));
        return ResponseEntity.ok(courses);
    }

    /**
     * Get courses by department
     */
    @GetMapping("/department/{department}")
    public ResponseEntity<List<CourseResponseVO>> getCoursesByDepartment(@PathVariable String department) {
        List<CourseResponseVO> courses = voConverter.dtosToResponses(courseService.findByDepartment(department));
        return ResponseEntity.ok(courses);
    }

    /**
     * Get available courses for student (not completed courses)
     */
    @GetMapping("/available/{studentId}")
    public ResponseEntity<List<CourseResponseVO>> getAvailableCoursesForStudent(@PathVariable Long studentId) {
        List<CourseResponseVO> courses = voConverter.dtosToResponses(courseService.findCoursesNotCompletedByStudent(studentId));
        return ResponseEntity.ok(courses);
    }

    /**
     * Search courses by keyword
     */
    @GetMapping("/search")
    public ResponseEntity<List<CourseResponseVO>> searchCourses(@RequestParam String query) {
        try {
            List<CourseResponseVO> courses = voConverter.dtosToResponses(courseService.searchCourses(query));
            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
