package edu.neu.curriculumRecommendation.controller;

import edu.neu.curriculumRecommendation.dto.RecommendationDTO;
import edu.neu.curriculumRecommendation.service.RecommendationService;
import edu.neu.curriculumRecommendation.vo.response.RecommendationResponseVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Recommendation Controller
 * Provides endpoints for generating and managing recommendations
 */
@RestController
@RequestMapping("/recommendations")
@CrossOrigin(origins = "http://localhost:3000")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    /**
     * Generate recommendations on-the-fly (not persisted)
     */
    @GetMapping("/{studentId}")
    public ResponseEntity<List<RecommendationResponseVO>> getRecommendations(@PathVariable Long studentId,
                                                                             @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<RecommendationDTO> dtos = recommendationService.generateRecommendations(studentId, limit);
            List<RecommendationResponseVO> responses = dtos.stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Generate and persist recommendations
     */
    @PostMapping("/{studentId}/generate")
    public ResponseEntity<List<RecommendationResponseVO>> generateAndSaveRecommendations(@PathVariable Long studentId) {
        try {
            List<RecommendationDTO> generated = recommendationService.generateRecommendations(studentId);
            List<RecommendationResponseVO> saved = generated.stream()
                    .map(recommendationService::saveRecommendation)
                    .map(this::toResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get recommendation history
     */
    @GetMapping("/{studentId}/history")
    public ResponseEntity<List<RecommendationResponseVO>> getRecommendationHistory(@PathVariable Long studentId) {
        try {
            List<RecommendationResponseVO> history = recommendationService.getRecommendationHistory(studentId).stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(history);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update recommendation status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<RecommendationResponseVO> updateStatus(@PathVariable Long id,
                                                                 @RequestParam String status) {
        try {
            RecommendationDTO updated = recommendationService.updateRecommendationStatus(id, status);
            return ResponseEntity.ok(toResponse(updated));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private RecommendationResponseVO toResponse(RecommendationDTO dto) {
        return RecommendationResponseVO.builder()
                .id(dto.getId())
                .courseId(dto.getCourseId())
                .courseCode(dto.getCourseCode())
                .courseName(dto.getCourseName())
                .matchScore(dto.getMatchScore())
                .reason(dto.getReason())
                .status(dto.getStatus())
                .recommendedAt(dto.getRecommendedAt())
                // prerequisite fields remain null unless populated upstream
                .prerequisiteChain(null)
                .missingPrerequisites(null)
                .allPrerequisitesMet(null)
                .build();
    }
}
package edu.neu.curriculumRecommendation.controller;

import edu.neu.curriculumRecommendation.dto.CourseDTO;
import edu.neu.curriculumRecommendation.dto.RecommendationDTO;
import edu.neu.curriculumRecommendation.service.CourseService;
import edu.neu.curriculumRecommendation.service.RecommendationService;
import edu.neu.curriculumRecommendation.vo.response.CourseResponseVO;
import edu.neu.curriculumRecommendation.vo.response.RecommendationResponseVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Recommendation Controller
 * Provides endpoints for generating and managing course recommendations
 */
@RestController
@RequestMapping("/recommendations")
@CrossOrigin(origins = "http://localhost:3000")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final CourseService courseService;

    public RecommendationController(RecommendationService recommendationService, CourseService courseService) {
        this.recommendationService = recommendationService;
        this.courseService = courseService;
    }

    /**
     * Generate recommendations on-the-fly (not saved)
     */
    @GetMapping("/{studentId}")
    public ResponseEntity<List<RecommendationResponseVO>> getRecommendations(@PathVariable Long studentId,
                                                                             @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<RecommendationDTO> dtos = recommendationService.generateRecommendations(studentId, limit);
            List<RecommendationResponseVO> responses = dtos.stream()
                    .map(this::toResponseVO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Generate recommendations and persist them
     */
    @PostMapping("/{studentId}/generate")
    public ResponseEntity<List<RecommendationResponseVO>> generateAndSaveRecommendations(@PathVariable Long studentId) {
        try {
            List<RecommendationDTO> generated = recommendationService.generateRecommendations(studentId);
            List<RecommendationDTO> saved = generated.stream()
                    .map(recommendationService::saveRecommendation)
                    .collect(Collectors.toList());
            List<RecommendationResponseVO> responses = saved.stream()
                    .map(this::toResponseVO)
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.CREATED).body(responses);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get recommendation history
     */
    @GetMapping("/{studentId}/history")
    public ResponseEntity<List<RecommendationResponseVO>> getRecommendationHistory(@PathVariable Long studentId) {
        try {
            List<RecommendationDTO> dtos = recommendationService.getRecommendationHistory(studentId);
            List<RecommendationResponseVO> responses = dtos.stream()
                    .map(this::toResponseVO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update recommendation status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<RecommendationResponseVO> updateRecommendationStatus(@PathVariable Long id,
                                                                               @RequestParam String status) {
        try {
            RecommendationDTO updated = recommendationService.updateRecommendationStatus(id, status);
            return ResponseEntity.ok(toResponseVO(updated));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private RecommendationResponseVO toResponseVO(RecommendationDTO dto) {
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

        return RecommendationResponseVO.builder()
                .id(dto.getId())
                .courseId(dto.getCourseId())
                .courseCode(dto.getCourseCode())
                .courseName(dto.getCourseName())
                .description(courseVO != null ? courseVO.getDescription() : null)
                .credits(courseVO != null ? courseVO.getCredits() : null)
                .difficulty(courseVO != null ? courseVO.getDifficulty() : null)
                .matchScore(dto.getMatchScore())
                .reason(dto.getReason())
                .status(dto.getStatus())
                .recommendedAt(dto.getRecommendedAt())
                .build();
    }
}

