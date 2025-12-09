package edu.neu.curriculumRecommendation.controller;

import edu.neu.curriculumRecommendation.dto.CourseDTO;
import edu.neu.curriculumRecommendation.dto.EnrollmentDTO;
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

    public EnrollmentController(EnrollmentService enrollmentService,
                                EnrollmentVOConverter enrollmentVOConverter,
                                CourseService courseService) {
        this.enrollmentService = enrollmentService;
        this.enrollmentVOConverter = enrollmentVOConverter;
        this.courseService = courseService;
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

    private EnrollmentResponseVO toResponseVO(EnrollmentDTO dto) {
        CourseResponseVO courseVO = null;
        if (dto.getCourseId() != null) {
            CourseDTO courseDTO = courseService.findById(dto.getCourseId());
            courseVO = CourseResponseVO.builder()
                    .id(courseDTO.getId())
                    .courseCode(courseDTO.getCourseCode())
                    .courseName(courseDTO.getCourseName())
                    .description(courseDTO.getDescription())
                    .credits(courseDTO.getCredits())
                    .difficulty(courseDTO.getDifficulty())
                    .department(courseDTO.getDepartment())
                    .semester(courseDTO.getSemester())
                    .isActive(courseDTO.getIsActive())
                    .createdAt(courseDTO.getCreatedAt())
                    .updatedAt(courseDTO.getUpdatedAt())
                    .build();
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

