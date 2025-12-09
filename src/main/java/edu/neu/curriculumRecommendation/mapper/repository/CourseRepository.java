package edu.neu.curriculumRecommendation.mapper.repository;

import edu.neu.curriculumRecommendation.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByCourseCode(String courseCode);

    List<Course> findByDifficulty(String difficulty);

    List<Course> findByIsActiveTrue();

    List<Course> findByDepartment(String department);

    List<Course> findByDifficultyAndIsActive(String difficulty, Boolean isActive);

    boolean existsByCourseCode(String courseCode);

    @Query("SELECT c FROM Course c WHERE c.id NOT IN (SELECT e.course.id FROM Enrollment e WHERE e.student.id = :studentId AND e.status = 'COMPLETED')")
    List<Course> findCoursesNotCompletedByStudent(@Param("studentId") Long studentId);
}

