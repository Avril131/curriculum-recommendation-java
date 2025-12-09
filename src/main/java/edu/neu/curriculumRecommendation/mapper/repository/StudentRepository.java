package edu.neu.curriculumRecommendation.mapper.repository;

import edu.neu.curriculumRecommendation.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByUserId(Long userId);

    Optional<Student> findByStudentId(String studentId);

    List<Student> findByGpaGreaterThan(Double gpa);

    List<Student> findByGpaGreaterThanEqual(Double gpa);

    List<Student> findByMajor(String major);

    boolean existsByStudentId(String studentId);
}

