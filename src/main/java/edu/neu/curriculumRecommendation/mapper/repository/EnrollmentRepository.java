package edu.neu.curriculumRecommendation.mapper.repository;

import edu.neu.curriculumRecommendation.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findByCourseId(Long courseId);

    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    List<Enrollment> findByStudentIdAndStatus(Long studentId, String status);

    List<Enrollment> findByStudentIdOrderByYearDescSemesterDesc(Long studentId);

    boolean existsByStudentIdAndCourseIdAndSemesterAndYear(Long studentId, Long courseId, String semester, Integer year);

    Long countByCourseIdAndStatus(Long courseId, String status);
}

