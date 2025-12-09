package edu.neu.curriculumRecommendation.controller;

import edu.neu.curriculumRecommendation.dto.RecommendationDTO;
import edu.neu.curriculumRecommendation.exception.ResourceNotFoundException;
import edu.neu.curriculumRecommendation.exception.ValidationException;
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
     * Degree requirement-based recommendations
     */
    @GetMapping("/{studentId}/degree-requirements")
    public ResponseEntity<List<RecommendationResponseVO>> getRecommendationsByDegreeRequirements(@PathVariable Long studentId,
                                                                                                 @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<RecommendationResponseVO> recommendations =
                    recommendationService.generateByDegreeRequirements(studentId, limit);
            return ResponseEntity.ok(recommendations);
        } catch (ResourceNotFoundException | ValidationException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Popular course recommendations
     */
    @GetMapping("/{studentId}/popular")
    public ResponseEntity<List<RecommendationResponseVO>> getRecommendationsByPopular(@PathVariable Long studentId,
                                                                                      @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<RecommendationResponseVO> recommendations =
                    recommendationService.generateByPopular(studentId, limit);
            return ResponseEntity.ok(recommendations);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Alias: next semester recommendations via query param
     */
    @GetMapping("/next-semester")
    public ResponseEntity<List<RecommendationResponseVO>> getNextSemesterRecommendations(@RequestParam Long studentId,
                                                                                         @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<RecommendationResponseVO> responses = recommendationService.generateRecommendations(studentId, limit)
                    .stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Alias: degree requirement recommendations via query param
     */
    @GetMapping("/requirements")
    public ResponseEntity<List<RecommendationResponseVO>> getDegreeRequirementsWithQuery(@RequestParam Long studentId,
                                                                                          @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<RecommendationResponseVO> recommendations =
                    recommendationService.generateByDegreeRequirements(studentId, limit);
            return ResponseEntity.ok(recommendations);
        } catch (ResourceNotFoundException | ValidationException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Alias: popular recommendations via query param
     */
    @GetMapping("/popular")
    public ResponseEntity<List<RecommendationResponseVO>> getPopularWithQuery(@RequestParam Long studentId,
                                                                              @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<RecommendationResponseVO> recommendations =
                    recommendationService.generateByPopular(studentId, limit);
            return ResponseEntity.ok(recommendations);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
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