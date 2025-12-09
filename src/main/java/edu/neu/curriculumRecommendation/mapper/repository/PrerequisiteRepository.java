package edu.neu.curriculumRecommendation.mapper.repository;

import edu.neu.curriculumRecommendation.entity.Prerequisite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrerequisiteRepository extends JpaRepository<Prerequisite, Long> {

    List<Prerequisite> findByCourseId(Long courseId);

    List<Prerequisite> findByPrerequisiteCourseId(Long prerequisiteCourseId);

    boolean existsByCourseIdAndPrerequisiteCourseId(Long courseId, Long prerequisiteCourseId);
}

