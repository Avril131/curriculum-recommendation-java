package edu.neu.curriculumRecommendation.controller;

import edu.neu.curriculumRecommendation.dto.CourseDTO;
import edu.neu.curriculumRecommendation.dto.EnrollmentDTO;
import edu.neu.curriculumRecommendation.exception.DuplicateResourceException;
import edu.neu.curriculumRecommendation.exception.ResourceNotFoundException;
import edu.neu.curriculumRecommendation.mapper.converter.CourseVOConverter;
import edu.neu.curriculumRecommendation.mapper.converter.EnrollmentVOConverter;
import edu.neu.curriculumRecommendation.service.CourseService;
import edu.neu.curriculumRecommendation.service.EnrollmentService;
import edu.neu.curriculumRecommendation.util.GradeUtil;
import edu.neu.curriculumRecommendation.vo.request.EnrollmentRequestVO;
import edu.neu.curriculumRecommendation.vo.response.CourseResponseVO;
import edu.neu.curriculumRecommendation.vo.response.EnrollmentResponseVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enrollment Controller
 * Handles enrollment operations and GPA updates
 */
@RestController
@RequestMapping("/enrollments")
@CrossOrigin(origins = "http://localhost:3000")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final EnrollmentVOConverter enrollmentVOConverter;
    private final CourseService courseService;
    private final CourseVOConverter courseVOConverter;

    public EnrollmentController(EnrollmentService enrollmentService,
                                EnrollmentVOConverter enrollmentVOConverter,
                                CourseService courseService,
                                CourseVOConverter courseVOConverter) {
        this.enrollmentService = enrollmentService;
        this.enrollmentVOConverter = enrollmentVOConverter;
        this.courseService = courseService;
        this.courseVOConverter = courseVOConverter;
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<EnrollmentResponseVO>> getStudentEnrollments(@PathVariable Long studentId) {
        try {
            List<EnrollmentDTO> dtos = enrollmentService.findByStudentId(studentId);
            List<EnrollmentResponseVO> responses = dtos.stream()
                    .map(this::toResponseVO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<EnrollmentResponseVO> createEnrollment(@Valid @RequestBody EnrollmentRequestVO requestVO) {
        try {
            EnrollmentDTO dto = enrollmentVOConverter.requestToDto(requestVO);
            EnrollmentDTO created = enrollmentService.createEnrollment(dto);
            EnrollmentResponseVO response = toResponseVO(created);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnrollmentResponseVO> updateEnrollment(@PathVariable Long id,
                                                                 @Valid @RequestBody EnrollmentRequestVO requestVO) {
        try {
            EnrollmentDTO dto = enrollmentVOConverter.requestToDto(requestVO);
            EnrollmentDTO updated = enrollmentService.updateEnrollment(id, dto);
            EnrollmentResponseVO response = toResponseVO(updated);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable Long id) {
        try {
            enrollmentService.deleteEnrollment(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/student/{studentId}/completed")
    public ResponseEntity<List<EnrollmentResponseVO>> getCompletedEnrollments(@PathVariable Long studentId) {
        try {
            List<EnrollmentDTO> dtos = enrollmentService.findByStudentIdAndStatus(studentId, "COMPLETED");
            List<EnrollmentResponseVO> responses = dtos.stream()
                    .map(this::toResponseVO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/student/{studentId}/current-semester")
    public ResponseEntity<List<EnrollmentResponseVO>> getCurrentSemesterEnrollments(@PathVariable Long studentId) {
        try {
            LocalDate now = LocalDate.now();
            int month = now.getMonthValue();
            String currentSemester;
            int currentYear = now.getYear();
            if (month >= 1 && month <= 5) {
                currentSemester = "Spring";
            } else if (month >= 6 && month <= 8) {
                currentSemester = "Fall";
            } else {
                currentSemester = "Fall";
            }

            List<EnrollmentDTO> dtos = enrollmentService.findByStudentIdAndStatus(studentId, "IN_PROGRESS")
                    .stream()
                    .filter(e -> currentSemester.equals(e.getSemester()) && currentYear == e.getYear())
                    .collect(Collectors.toList());

            List<EnrollmentResponseVO> responses = dtos.stream()
                    .map(this::toResponseVO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/drop")
    public ResponseEntity<EnrollmentResponseVO> dropCourse(@PathVariable Long id) {
        try {
            EnrollmentDTO enrollment = enrollmentService.findById(id);
            if (!"IN_PROGRESS".equals(enrollment.getStatus())) {
                return ResponseEntity.badRequest().build();
            }

            enrollment.setStatus("DROPPED");
            EnrollmentDTO updated = enrollmentService.updateEnrollment(id, enrollment);

            CourseDTO course = courseService.findById(updated.getCourseId());
            CourseResponseVO courseVO = courseVOConverter.dtoToResponse(course);

            EnrollmentResponseVO responseVO = EnrollmentResponseVO.builder()
                    .id(updated.getId())
                    .studentId(updated.getStudentId())
                    .course(courseVO)
                    .semester(updated.getSemester())
                    .year(updated.getYear())
                    .status(updated.getStatus())
                    .build();

            return ResponseEntity.ok(responseVO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/register-current")
    public ResponseEntity<EnrollmentResponseVO> registerForCurrentSemester(@Valid @RequestBody EnrollmentRequestVO requestVO) {
        try {
            if (!"IN_PROGRESS".equals(requestVO.getStatus())) {
                requestVO.setStatus("IN_PROGRESS");
            }

            LocalDate now = LocalDate.now();
            int month = now.getMonthValue();
            String currentSemester;
            int currentYear = now.getYear();
            if (month >= 1 && month <= 5) {
                currentSemester = "Spring";
            } else if (month >= 6 && month <= 8) {
                currentSemester = "Fall";
            } else {
                currentSemester = "Fall";
            }

            requestVO.setSemester(currentSemester);
            requestVO.setYear(currentYear);
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
                    .grade(created.getGrade())
                    .gradePoint(null)
                    .status(created.getStatus())
                    .completedAt(created.getCompletedAt())
                    .createdAt(created.getCreatedAt())
                    .updatedAt(created.getUpdatedAt())
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(responseVO);
        } catch (DuplicateResourceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<EnrollmentResponseVO> completeCourse(@PathVariable Long id, @RequestParam String grade) {
        try {
            EnrollmentDTO enrollment = enrollmentService.findById(id);
            if (!"IN_PROGRESS".equals(enrollment.getStatus())) {
                return ResponseEntity.badRequest().build();
            }

            enrollment.setStatus("COMPLETED");
            enrollment.setGrade(grade);
            enrollment.setCompletedAt(LocalDate.now());

            EnrollmentDTO updated = enrollmentService.updateEnrollment(id, enrollment);

            CourseDTO course = courseService.findById(updated.getCourseId());
            CourseResponseVO courseVO = courseVOConverter.dtoToResponse(course);
            Double gradePoint = GradeUtil.gradeToGPA(grade);

            EnrollmentResponseVO responseVO = EnrollmentResponseVO.builder()
                    .id(updated.getId())
                    .studentId(updated.getStudentId())
                    .course(courseVO)
                    .semester(updated.getSemester())
                    .year(updated.getYear())
                    .grade(updated.getGrade())
                    .gradePoint(gradePoint)
                    .status(updated.getStatus())
                    .completedAt(updated.getCompletedAt())
                    .build();

            return ResponseEntity.ok(responseVO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private EnrollmentResponseVO toResponseVO(EnrollmentDTO dto) {
        CourseResponseVO courseVO = null;
        if (dto.getCourseId() != null) {
            CourseDTO courseDTO = courseService.findById(dto.getCourseId());
            courseVO = courseVOConverter.dtoToResponse(courseDTO);
        }

        Double gradePoint = dto.getGrade() != null ? GradeUtil.gradeToGPA(dto.getGrade()) : null;

        return EnrollmentResponseVO.builder()
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
    }
}

