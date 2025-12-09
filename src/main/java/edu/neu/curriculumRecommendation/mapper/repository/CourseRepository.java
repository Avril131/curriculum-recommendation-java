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

    @Query("SELECT c FROM Course c WHERE (LOWER(c.semester) LIKE LOWER(CONCAT('%', :semester, '%')) OR c.semester IS NULL) AND c.isActive = :isActive")
    List<Course> findBySemesterContainingAndIsActive(@Param("semester") String semester, @Param("isActive") Boolean isActive);

    @Query("SELECT c FROM Course c WHERE " +
            "(LOWER(c.courseCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.courseName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.careerTags) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
            "c.isActive = true")
    List<Course> searchCourses(@Param("query") String query);

    @Query("SELECT e.course.id, COUNT(e), AVG(CASE e.grade " +
            "WHEN 'A' THEN 4.0 " +
            "WHEN 'A-' THEN 3.667 " +
            "WHEN 'B+' THEN 3.333 " +
            "WHEN 'B' THEN 3.0 " +
            "WHEN 'B-' THEN 2.667 " +
            "WHEN 'C+' THEN 2.333 " +
            "WHEN 'C' THEN 2.0 " +
            "WHEN 'C-' THEN 1.667 " +
            "WHEN 'D+' THEN 1.333 " +
            "WHEN 'D' THEN 1.0 " +
            "WHEN 'D-' THEN 0.667 " +
            "WHEN 'F' THEN 0.0 " +
            "ELSE NULL END) " +
            "FROM Enrollment e " +
            "WHERE e.status = 'COMPLETED' " +
            "GROUP BY e.course.id " +
            "ORDER BY COUNT(e) DESC")
    List<Object[]> findCourseStatistics();
}

