package edu.neu.curriculumRecommendation.controller;

import edu.neu.curriculumRecommendation.dto.CourseDTO;
import edu.neu.curriculumRecommendation.dto.EnrollmentDTO;
import edu.neu.curriculumRecommendation.exception.ResourceNotFoundException;
import edu.neu.curriculumRecommendation.mapper.converter.CourseVOConverter;
import edu.neu.curriculumRecommendation.mapper.converter.EnrollmentVOConverter;
import edu.neu.curriculumRecommendation.mapper.converter.StudentVOConverter;
import edu.neu.curriculumRecommendation.service.CourseService;
import edu.neu.curriculumRecommendation.service.EnrollmentService;
import edu.neu.curriculumRecommendation.service.StudentService;
import edu.neu.curriculumRecommendation.util.GradeUtil;
import edu.neu.curriculumRecommendation.vo.request.EnrollmentRequestVO;
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
import java.util.Optional;

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
    private final EnrollmentVOConverter enrollmentVOConverter;

    public StudentController(StudentService studentService,
                             StudentVOConverter voConverter,
                             EnrollmentService enrollmentService,
                             CourseService courseService,
                             CourseVOConverter courseVOConverter,
                             EnrollmentVOConverter enrollmentVOConverter) {
        this.studentService = studentService;
        this.voConverter = voConverter;
        this.enrollmentService = enrollmentService;
        this.courseService = courseService;
        this.courseVOConverter = courseVOConverter;
        this.enrollmentVOConverter = enrollmentVOConverter;
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

    /**
     * Get completed courses for a student
     */
    @GetMapping("/{id}/completed-courses")
    public ResponseEntity<List<EnrollmentResponseVO>> getStudentCompletedCourses(@PathVariable Long id) {
        try {
            List<EnrollmentDTO> enrollments = enrollmentService.findByStudentIdAndStatus(id, "COMPLETED");

            List<EnrollmentResponseVO> responseList = new ArrayList<>();
            for (EnrollmentDTO dto : enrollments) {
                CourseDTO course = courseService.findById(dto.getCourseId());
                CourseResponseVO courseVO = courseVOConverter.dtoToResponse(course);
                Double gradePoint = GradeUtil.gradeToGPA(dto.getGrade());

                EnrollmentResponseVO responseVO = EnrollmentResponseVO.builder()
                        .id(dto.getId())
                        .studentId(dto.getStudentId())
                        .course(courseVO)
                        .semester(dto.getSemester())
                        .year(dto.getYear())
                        .grade(dto.getGrade())
                        .gradePoint(gradePoint)
                        .status(dto.getStatus())
                        .completedAt(dto.getCompletedAt())
                        .createdAt(dto.getCreatedAt())
                        .updatedAt(dto.getUpdatedAt())
                        .build();

                responseList.add(responseVO);
            }

            return ResponseEntity.ok(responseList);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get planned courses for a student (status = PLANNED)
     */
    @GetMapping("/{id}/plan")
    public ResponseEntity<List<EnrollmentResponseVO>> getStudentPlan(@PathVariable Long id) {
        try {
            List<EnrollmentDTO> plans = enrollmentService.findByStudentIdAndStatus(id, "PLANNED");
            List<EnrollmentResponseVO> responseList = new ArrayList<>();

            for (EnrollmentDTO dto : plans) {
                CourseDTO course = courseService.findById(dto.getCourseId());
                CourseResponseVO courseVO = courseVOConverter.dtoToResponse(course);

                EnrollmentResponseVO responseVO = EnrollmentResponseVO.builder()
                        .id(dto.getId())
                        .studentId(dto.getStudentId())
                        .course(courseVO)
                        .semester(dto.getSemester())
                        .year(dto.getYear())
                        .grade(null)
                        .gradePoint(null)
                        .status(dto.getStatus())
                        .completedAt(null)
                        .createdAt(dto.getCreatedAt())
                        .updatedAt(dto.getUpdatedAt())
                        .build();

                responseList.add(responseVO);
            }

            return ResponseEntity.ok(responseList);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Add a planned course for a student (status = PLANNED)
     */
    @PostMapping("/{id}/plan")
    public ResponseEntity<EnrollmentResponseVO> addPlan(@PathVariable Long id,
                                                        @Valid @RequestBody EnrollmentRequestVO requestVO) {
        try {
            requestVO.setStudentId(id);
            requestVO.setStatus("PLANNED");
            requestVO.setGrade(null);
            requestVO.setCompletedAt(null);

            EnrollmentDTO dto = enrollmentVOConverter.requestToDto(requestVO);
            EnrollmentDTO created = enrollmentService.createEnrollment(dto);

            CourseDTO course = courseService.findById(created.getCourseId());
            CourseResponseVO courseVO = courseVOConverter.dtoToResponse(course);

            EnrollmentResponseVO responseVO = EnrollmentResponseVO.builder()
                    .id(created.getId())
                    .studentId(created.getStudentId())
                    .course(courseVO)
                    .semester(created.getSemester())
                    .year(created.getYear())
                    .grade(null)
                    .gradePoint(null)
                    .status(created.getStatus())
                    .completedAt(null)
                    .createdAt(created.getCreatedAt())
                    .updatedAt(created.getUpdatedAt())
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(responseVO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Remove a planned course for a student
     */
    @DeleteMapping("/{id}/plan/{courseId}")
    public ResponseEntity<Void> removePlan(@PathVariable Long id, @PathVariable Long courseId) {
        try {
            List<EnrollmentDTO> plans = enrollmentService.findByStudentIdAndStatus(id, "PLANNED");
            Optional<EnrollmentDTO> target = plans.stream()
                    .filter(p -> courseId.equals(p.getCourseId()))
                    .findFirst();
            if (target.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            enrollmentService.deleteEnrollment(target.get().getId());
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
