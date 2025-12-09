package edu.neu.curriculumRecommendation.service;

import edu.neu.curriculumRecommendation.dto.RecommendationDTO;

import java.util.List;

/**
 * Recommendation Service Interface
 * Defines recommendation-related operations
 */
public interface RecommendationService {

    /**
     * Generate recommended courses for a student
     *
     * @param studentId student id
     * @return list of recommendations
     */
    List<RecommendationDTO> generateRecommendations(Long studentId);

    /**
     * Generate recommended courses for a student with a limit
     *
     * @param studentId student id
     * @param limit     max number of recommendations
     * @return list of recommendations
     */
    List<RecommendationDTO> generateRecommendations(Long studentId, Integer limit);

    /**
     * Save a recommendation record
     *
     * @param recommendationDTO recommendation dto
     * @return saved recommendation dto
     */
    RecommendationDTO saveRecommendation(RecommendationDTO recommendationDTO);

    /**
     * Get recommendation history for a student
     *
     * @param studentId student id
     * @return list of recommendation history
     */
    List<RecommendationDTO> getRecommendationHistory(Long studentId);

    /**
     * Update recommendation status
     *
     * @param recommendationId recommendation id
     * @param status           new status
     * @return updated recommendation dto
     */
    RecommendationDTO updateRecommendationStatus(Long recommendationId, String status);

    /**
     * Delete recommendation by id
     *
     * @param id recommendation id
     */
    void deleteRecommendation(Long id);
}

