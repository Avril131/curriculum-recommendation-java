package edu.neu.curriculumRecommendation.mapper.repository;

import edu.neu.curriculumRecommendation.entity.ProgramRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramRequirementRepository extends JpaRepository<ProgramRequirement, Long> {

    List<ProgramRequirement> findByMajor(String major);

    List<ProgramRequirement> findByMajorAndIsMandatory(String major, Boolean isMandatory);

    List<ProgramRequirement> findByCourseId(Long courseId);

    List<ProgramRequirement> findByMajorAndRequirementType(String major, String requirementType);

    List<ProgramRequirement> findByMajorAndCourseId(String major, Long courseId);
}

