package edu.neu.curriculumRecommendation.controller;

import edu.neu.curriculumRecommendation.dto.CourseDTO;
import edu.neu.curriculumRecommendation.dto.EnrollmentDTO;
import edu.neu.curriculumRecommendation.exception.ResourceNotFoundException;
import edu.neu.curriculumRecommendation.mapper.converter.StudentVOConverter;
import edu.neu.curriculumRecommendation.mapper.converter.CourseVOConverter;
import edu.neu.curriculumRecommendation.service.CourseService;
import edu.neu.curriculumRecommendation.service.EnrollmentService;
import edu.neu.curriculumRecommendation.service.StudentService;
import edu.neu.curriculumRecommendation.util.GradeUtil;
import edu.neu.curriculumRecommendation.vo.request.StudentCreateRequestVO;
import edu.neu.curriculumRecommendation.vo.request.StudentUpdateRequestVO;
import edu.neu.curriculumRecommendation.vo.response.CourseResponseVO;
import edu.neu.curriculumRecommendation.vo.response.EnrollmentResponseVO;
import edu.neu.curriculumRecommendation.vo.response.StudentResponseVO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Student Controller
 * REST API endpoints for student management
 */
@RestController
@RequestMapping("/students")
@CrossOrigin(origins = "http://localhost:3000")
public class StudentController {

    private final StudentService studentService;
    private final StudentVOConverter voConverter;
    private final EnrollmentService enrollmentService;
    private final CourseService courseService;
    private final CourseVOConverter courseVOConverter;

    public StudentController(StudentService studentService,
                             StudentVOConverter voConverter,
                             EnrollmentService enrollmentService,
                             CourseService courseService,
                             CourseVOConverter courseVOConverter) {
        this.studentService = studentService;
        this.voConverter = voConverter;
        this.enrollmentService = enrollmentService;
        this.courseService = courseService;
        this.courseVOConverter = courseVOConverter;
    }

    /**
     * Get all students
     */
    @GetMapping
    public ResponseEntity<List<StudentResponseVO>> getAllStudents() {
        List<StudentResponseVO> students = voConverter.dtosToResponses(studentService.findAll());
        return ResponseEntity.ok(students);
    }

    /**
     * Get student by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseVO> getStudentById(@PathVariable Long id) {
        try {
            StudentResponseVO student = voConverter.dtoToResponse(studentService.findById(id));
            return ResponseEntity.ok(student);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create a new student
     */
    @PostMapping
    public ResponseEntity<StudentResponseVO> createStudent(@Valid @RequestBody StudentCreateRequestVO requestVO) {
        StudentResponseVO createdStudent = voConverter.dtoToResponse(
                studentService.createStudent(voConverter.requestToDto(requestVO))
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
    }

    /**
     * Update student information
     */
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseVO> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentUpdateRequestVO requestVO) {
        try {
            StudentResponseVO updatedStudent = voConverter.dtoToResponse(
                    studentService.updateStudent(id, voConverter.requestToDto(requestVO))
            );
            return ResponseEntity.ok(updatedStudent);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete student
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        try {
            studentService.deleteStudent(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get student by user ID
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<StudentResponseVO> getStudentByUserId(@PathVariable Long userId) {
        try {
            StudentResponseVO student = voConverter.dtoToResponse(studentService.findByUserId(userId));
            return ResponseEntity.ok(student);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get students by major
     */
    @GetMapping("/major/{major}")
    public ResponseEntity<List<StudentResponseVO>> getStudentsByMajor(@PathVariable String major) {
        List<StudentResponseVO> students = voConverter.dtosToResponses(studentService.findByMajor(major));
        return ResponseEntity.ok(students);
    }
}
