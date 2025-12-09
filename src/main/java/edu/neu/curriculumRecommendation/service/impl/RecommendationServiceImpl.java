package edu.neu.curriculumRecommendation.service.impl;

import edu.neu.curriculumRecommendation.dto.RecommendationDTO;
import edu.neu.curriculumRecommendation.entity.Course;
import edu.neu.curriculumRecommendation.entity.Enrollment;
import edu.neu.curriculumRecommendation.entity.Prerequisite;
import edu.neu.curriculumRecommendation.entity.Recommendation;
import edu.neu.curriculumRecommendation.entity.Student;
import edu.neu.curriculumRecommendation.exception.ResourceNotFoundException;
import edu.neu.curriculumRecommendation.mapper.converter.RecommendationConverter;
import edu.neu.curriculumRecommendation.mapper.repository.CourseRepository;
import edu.neu.curriculumRecommendation.mapper.repository.EnrollmentRepository;
import edu.neu.curriculumRecommendation.mapper.repository.PrerequisiteRepository;
import edu.neu.curriculumRecommendation.mapper.repository.RecommendationRepository;
import edu.neu.curriculumRecommendation.mapper.repository.StudentRepository;
import edu.neu.curriculumRecommendation.service.RecommendationService;
import edu.neu.curriculumRecommendation.vo.response.PrerequisiteCourseVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Recommendation Service Implementation with BFS prerequisite handling
 */
@Service
@Transactional
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final RecommendationConverter recommendationConverter;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PrerequisiteRepository prerequisiteRepository;

    public RecommendationServiceImpl(RecommendationRepository recommendationRepository,
                                     RecommendationConverter recommendationConverter,
                                     StudentRepository studentRepository,
                                     CourseRepository courseRepository,
                                     EnrollmentRepository enrollmentRepository,
                                     PrerequisiteRepository prerequisiteRepository) {
        this.recommendationRepository = recommendationRepository;
        this.recommendationConverter = recommendationConverter;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.prerequisiteRepository = prerequisiteRepository;
    }

    /**
     * BFS to get all prerequisites with levels
     */
    private Map<Long, Integer> getAllPrerequisitesWithLevels(Long courseId) {
        Map<Long, Integer> result = new HashMap<>();
        Queue<Long> queue = new LinkedList<>();
        Set<Long> visited = new HashSet<>();

        queue.offer(courseId);
        visited.add(courseId);
        int currentLevel = 1;

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Long currentCourseId = queue.poll();
                List<Prerequisite> directPrereqs = prerequisiteRepository.findByCourseId(currentCourseId);
                for (Prerequisite prereq : directPrereqs) {
                    if (prereq.getPrerequisiteCourse() == null || prereq.getPrerequisiteCourse().getId() == null) {
                        continue;
                    }
                    Long prereqId = prereq.getPrerequisiteCourse().getId();
                    if (!visited.contains(prereqId)) {
                        visited.add(prereqId);
                        queue.offer(prereqId);
                        result.put(prereqId, currentLevel);
                    }
                }
            }
            currentLevel++;
        }
        return result;
    }

    /**
     * Build prerequisite chain with completion status
     */
    private List<PrerequisiteCourseVO> buildPrerequisiteChain(Long courseId, Set<Long> completedCourseIds) {
        Map<Long, Integer> prereqMap = getAllPrerequisitesWithLevels(courseId);
        List<PrerequisiteCourseVO> chain = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : prereqMap.entrySet()) {
            Long prereqCourseId = entry.getKey();
            Integer level = entry.getValue();

            Course prereqCourse = courseRepository.findById(prereqCourseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Prerequisite course not found"));

            PrerequisiteCourseVO vo = PrerequisiteCourseVO.builder()
                    .courseId(prereqCourse.getId())
                    .courseCode(prereqCourse.getCourseCode())
                    .courseName(prereqCourse.getCourseName())
                    .credits(prereqCourse.getCredits())
                    .isCompleted(completedCourseIds.contains(prereqCourseId))
                    .level(level)
                    .build();

            chain.add(vo);
        }

        // Sort by level asc (deeper prerequisites come first if larger level value)
        chain.sort((a, b) -> a.getLevel().compareTo(b.getLevel()));
        return chain;
    }

    /**
     * Get missing prerequisites
     */
    private List<PrerequisiteCourseVO> getMissingPrerequisites(List<PrerequisiteCourseVO> prerequisiteChain) {
        return prerequisiteChain.stream()
                .filter(p -> p.getIsCompleted() == null || !p.getIsCompleted())
                .collect(Collectors.toList());
    }

    /**
     * Count career interest matches
     */
    private int countInterestMatches(String interests, String description) {
        String descLower = description.toLowerCase();
        String[] keywords = interests.split(",");
        int count = 0;
        for (String k : keywords) {
            String kw = k.trim().toLowerCase();
            if (!kw.isEmpty() && descLower.contains(kw)) {
                count++;
            }
        }
        return count;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecommendationDTO> generateRecommendations(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        // Completed courses
        Set<Long> completedCourseIds = enrollmentRepository.findByStudentIdAndStatus(studentId, "COMPLETED")
                .stream()
                .map(Enrollment::getCourse)
                .filter(c -> c != null && c.getId() != null)
                .map(Course::getId)
                .collect(Collectors.toSet());

        // Candidate courses (not completed)
        List<Course> candidates = courseRepository.findCoursesNotCompletedByStudent(studentId);

        List<RecommendationDTO> recommendations = new ArrayList<>();

        for (Course course : candidates) {
            if (course == null || course.getId() == null) {
                continue;
            }

            double score = 0.0;
            StringBuilder reason = new StringBuilder();

            // GPA factor (30)
            if (student.getGpa() != null) {
                double gpa = student.getGpa();
                String difficulty = course.getDifficulty() != null ? course.getDifficulty().toUpperCase() : "";
                if (gpa >= 3.5 && "ADVANCED".equals(difficulty)) {
                    score += 30;
                    reason.append("Your high GPA suits advanced courses. ");
                } else if (gpa >= 3.0 && gpa < 3.5 && "INTERMEDIATE".equals(difficulty)) {
                    score += 30;
                    reason.append("This intermediate course matches your performance. ");
                } else if (gpa < 3.0 && "BEGINNER".equals(difficulty)) {
                    score += 30;
                    reason.append("A good foundational course. ");
                }
            }

            // Prerequisite factor (40) using BFS chain
            List<PrerequisiteCourseVO> prerequisiteChain;
            List<PrerequisiteCourseVO> missingPrerequisites;
            Map<Long, Integer> allPrereqsMap = getAllPrerequisitesWithLevels(course.getId());

            if (allPrereqsMap.isEmpty()) {
                // No prerequisites
                score += 40;
                reason.append("No prerequisites required. ");
                prerequisiteChain = new ArrayList<>();
                missingPrerequisites = new ArrayList<>();
            } else {
                prerequisiteChain = buildPrerequisiteChain(course.getId(), completedCourseIds);
                missingPrerequisites = getMissingPrerequisites(prerequisiteChain);
                if (missingPrerequisites.isEmpty()) {
                    score += 40;
                    reason.append("All prerequisites met. ");
                } else {
                    // Skip recommending this course if prerequisites not met
                    continue;
                }
            }

            // Career interests (30)
            if (student.getCareerInterests() != null && course.getDescription() != null) {
                int matches = countInterestMatches(student.getCareerInterests(), course.getDescription());
                if (matches >= 3) {
                    score += 30;
                    reason.append("Strongly aligns with your interests. ");
                } else if (matches == 2) {
                    score += 20;
                    reason.append("Aligns with your interests. ");
                } else if (matches == 1) {
                    score += 10;
                    reason.append("Somewhat relevant to your interests. ");
                }
            }

            RecommendationDTO dto = RecommendationDTO.builder()
                    .studentId(studentId)
                    .courseId(course.getId())
                    .courseCode(course.getCourseCode())
                    .courseName(course.getCourseName())
                    .matchScore(score)
                    .reason(reason.toString().trim())
                    .recommendedAt(LocalDateTime.now())
                    .status("PENDING")
                    .build();
            recommendations.add(dto);
        }

        // Sort by score desc and limit to top 10
        return recommendations.stream()
                .sorted(Comparator.comparing(RecommendationDTO::getMatchScore, Comparator.nullsLast(Double::compareTo)).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecommendationDTO> generateRecommendations(Long studentId, Integer limit) {
        List<RecommendationDTO> list = generateRecommendations(studentId);
        if (limit == null || limit <= 0) {
            return list;
        }
        return list.stream().limit(limit).collect(Collectors.toList());
    }

    @Override
    public RecommendationDTO saveRecommendation(RecommendationDTO recommendationDTO) {
        Recommendation recommendation = recommendationConverter.dtoToEntity(recommendationDTO);
        Student student = studentRepository.findById(recommendationDTO.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + recommendationDTO.getStudentId()));
        Course course = courseRepository.findById(recommendationDTO.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + recommendationDTO.getCourseId()));
        recommendation.setStudent(student);
        recommendation.setCourse(course);
        if (recommendation.getRecommendedAt() == null) {
            recommendation.setRecommendedAt(LocalDateTime.now());
        }
        if (recommendation.getStatus() == null) {
            recommendation.setStatus("PENDING");
        }
        Recommendation saved = recommendationRepository.save(recommendation);
        return recommendationConverter.entityToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecommendationDTO> getRecommendationHistory(Long studentId) {
        return recommendationConverter.entitiesToDtos(
                recommendationRepository.findByStudentIdOrderByMatchScoreDesc(studentId)
        );
    }

    @Override
    public RecommendationDTO updateRecommendationStatus(Long recommendationId, String status) {
        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation not found with id: " + recommendationId));
        recommendation.setStatus(status);
        Recommendation updated = recommendationRepository.save(recommendation);
        return recommendationConverter.entityToDto(updated);
    }

    @Override
    public void deleteRecommendation(Long id) {
        if (!recommendationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recommendation not found with id: " + id);
        }
        recommendationRepository.deleteById(id);
    }
}

