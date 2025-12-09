package edu.neu.curriculumRecommendation.mapper.repository;

import edu.neu.curriculumRecommendation.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    List<Recommendation> findByStudentId(Long studentId);

    List<Recommendation> findByStudentIdAndStatus(Long studentId, String status);

    List<Recommendation> findByStudentIdOrderByMatchScoreDesc(Long studentId);

    List<Recommendation> findByCourseId(Long courseId);

    Optional<Recommendation> findByStudentIdAndCourseId(Long studentId, Long courseId);
}

