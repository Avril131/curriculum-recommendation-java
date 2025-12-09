package edu.neu.curriculumRecommendation.service.impl;

import edu.neu.curriculumRecommendation.dto.RecommendationDTO;
import edu.neu.curriculumRecommendation.entity.Course;
import edu.neu.curriculumRecommendation.entity.Enrollment;
import edu.neu.curriculumRecommendation.entity.ProgramRequirement;
import edu.neu.curriculumRecommendation.entity.Prerequisite;
import edu.neu.curriculumRecommendation.entity.Recommendation;
import edu.neu.curriculumRecommendation.entity.Student;
import edu.neu.curriculumRecommendation.exception.ResourceNotFoundException;
import edu.neu.curriculumRecommendation.exception.ValidationException;
import edu.neu.curriculumRecommendation.mapper.converter.CourseConverter;
import edu.neu.curriculumRecommendation.mapper.converter.RecommendationConverter;
import edu.neu.curriculumRecommendation.mapper.converter.CourseVOConverter;
import edu.neu.curriculumRecommendation.mapper.repository.CourseRepository;
import edu.neu.curriculumRecommendation.mapper.repository.EnrollmentRepository;
import edu.neu.curriculumRecommendation.mapper.repository.ProgramRequirementRepository;
import edu.neu.curriculumRecommendation.mapper.repository.PrerequisiteRepository;
import edu.neu.curriculumRecommendation.mapper.repository.RecommendationRepository;
import edu.neu.curriculumRecommendation.mapper.repository.StudentRepository;
import edu.neu.curriculumRecommendation.service.RecommendationService;
import edu.neu.curriculumRecommendation.vo.response.PrerequisiteCourseVO;
import edu.neu.curriculumRecommendation.vo.response.RecommendationResponseVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private final ProgramRequirementRepository programRequirementRepository;
    private final CourseConverter courseConverter;
    private final CourseVOConverter courseVOConverter;

    public RecommendationServiceImpl(RecommendationRepository recommendationRepository,
                                     RecommendationConverter recommendationConverter,
                                     StudentRepository studentRepository,
                                     CourseRepository courseRepository,
                                     EnrollmentRepository enrollmentRepository,
                                     PrerequisiteRepository prerequisiteRepository,
                                     ProgramRequirementRepository programRequirementRepository,
                                     CourseConverter courseConverter,
                                     CourseVOConverter courseVOConverter) {
        this.recommendationRepository = recommendationRepository;
        this.recommendationConverter = recommendationConverter;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.prerequisiteRepository = prerequisiteRepository;
        this.programRequirementRepository = programRequirementRepository;
        this.courseConverter = courseConverter;
        this.courseVOConverter = courseVOConverter;
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

        // Determine upcoming semester (Fall or Spring; ignore Summer)
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        String nextSemester;
        if (month >= 1 && month <= 5) {
            nextSemester = "Spring";
        } else if (month >= 6 && month <= 8) {
            nextSemester = "Fall";
        } else {
            nextSemester = "Spring";
        }

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

            // Career interests (30) - prefer course careerTags; fallback to description
            if (student.getCareerInterests() != null) {
                String studentInterests = student.getCareerInterests().toLowerCase();
                String[] interestKeywords = studentInterests.split(",");

                int matchCount = 0;

                if (course.getCareerTags() != null && !course.getCareerTags().isEmpty()) {
                    String courseTags = course.getCareerTags().toLowerCase();
                    for (String keyword : interestKeywords) {
                        if (courseTags.contains(keyword.trim())) {
                            matchCount++;
                        }
                    }
                } else if (course.getDescription() != null) {
                    String courseDesc = course.getDescription().toLowerCase();
                    for (String keyword : interestKeywords) {
                        if (courseDesc.contains(keyword.trim())) {
                            matchCount++;
                        }
                    }
                }

                if (matchCount >= 3) {
                    score += 30;
                    reason.append("Strongly aligns with your career interests. ");
                } else if (matchCount == 2) {
                    score += 20;
                    reason.append("Aligns with your career interests. ");
                } else if (matchCount == 1) {
                    score += 10;
                    reason.append("Somewhat relevant to your career interests. ");
                }
            }

            // Factor 4 - Semester match (bonus, not core weight)
            if (course.getSemester() != null && !course.getSemester().isEmpty()) {
                String courseSemester = course.getSemester().toLowerCase();
                if (courseSemester.contains(nextSemester.toLowerCase())) {
                    score += 10;
                    reason.append("Available in upcoming ").append(nextSemester).append(" semester. ");
                } else if (courseSemester.contains("fall") && courseSemester.contains("spring")) {
                    score += 5;
                    reason.append("Available in both semesters. ");
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
    @Transactional(readOnly = true)
    public List<RecommendationResponseVO> generateByDegreeRequirements(Long studentId, Integer limit) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        if (student.getMajor() == null || student.getMajor().trim().isEmpty()) {
            throw new ValidationException("Student major is not set. Please update your profile.");
        }

        List<ProgramRequirement> requirements = programRequirementRepository.findByMajor(student.getMajor());
        if (requirements.isEmpty()) {
            throw new ResourceNotFoundException("No course requirements found for major: " + student.getMajor());
        }

        Set<Long> completedCourseIds = enrollmentRepository
                .findByStudentIdAndStatus(studentId, "COMPLETED")
                .stream()
                .map(Enrollment::getCourse)
                .filter(Objects::nonNull)
                .map(Course::getId)
                .collect(Collectors.toSet());

        List<RecommendationResponseVO> recommendations = new ArrayList<>();

        for (ProgramRequirement req : requirements) {
            Course course = req.getCourse();
            if (course == null || course.getId() == null) {
                continue;
            }
            Long courseId = course.getId();

            // Skip completed courses
            if (completedCourseIds.contains(courseId)) {
                continue;
            }

            double score = 50.0;
            StringBuilder reason = new StringBuilder();

            if (Boolean.TRUE.equals(req.getIsMandatory())) {
                score += 30;
                reason.append("Required core course for ").append(student.getMajor()).append(" major. ");
            } else {
                score += 10;
                reason.append("Recommended elective for ").append(student.getMajor()).append(" major. ");
            }

            String reqType = req.getRequirementType();
            if ("Core".equalsIgnoreCase(reqType)) {
                score += 10;
                reason.append("Core course. ");
            } else if ("Foundation".equalsIgnoreCase(reqType)) {
                score += 5;
                reason.append("Foundation course. ");
            }

            Map<Long, Integer> allPrereqs = getAllPrerequisitesWithLevels(courseId);
            List<PrerequisiteCourseVO> prerequisiteChain;
            List<PrerequisiteCourseVO> missingPrerequisites;
            boolean allPrerequisitesMet;

            if (allPrereqs.isEmpty()) {
                prerequisiteChain = new ArrayList<>();
                missingPrerequisites = new ArrayList<>();
                allPrerequisitesMet = true;
                score += 10;
                reason.append("No prerequisites. ");
            } else {
                prerequisiteChain = buildPrerequisiteChain(courseId, completedCourseIds);
                missingPrerequisites = getMissingPrerequisites(prerequisiteChain);
                if (missingPrerequisites.isEmpty()) {
                    allPrerequisitesMet = true;
                    score += 10;
                    reason.append("Prerequisites met. ");
                } else {
                    allPrerequisitesMet = false;
                    reason.append("Prerequisites needed: ");
                    for (PrerequisiteCourseVO missing : missingPrerequisites) {
                        reason.append(missing.getCourseCode()).append(" ");
                    }
                }
            }

            RecommendationResponseVO responseVO = RecommendationResponseVO.builder()
                    .courseId(course.getId())
                    .courseCode(course.getCourseCode())
                    .courseName(course.getCourseName())
                    .description(course.getDescription())
                    .credits(course.getCredits())
                    .difficulty(course.getDifficulty())
                    .matchScore(score)
                    .reason(reason.toString().trim())
                    .status("PENDING")
                    .prerequisiteChain(prerequisiteChain)
                    .missingPrerequisites(missingPrerequisites)
                    .allPrerequisitesMet(allPrerequisitesMet)
                    .build();

            recommendations.add(responseVO);
        }

        recommendations.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));

        return recommendations.stream()
                .limit(limit != null ? limit : 10)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecommendationResponseVO> generateByPopular(Long studentId, Integer limit) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Set<Long> completedCourseIds = enrollmentRepository
                .findByStudentIdAndStatus(studentId, "COMPLETED")
                .stream()
                .map(Enrollment::getCourse)
                .filter(Objects::nonNull)
                .map(Course::getId)
                .collect(Collectors.toSet());

        List<Object[]> statistics = courseRepository.findCourseStatistics();

        Map<Long, Double> popularityScores = new HashMap<>();
        Map<Long, Long> enrollmentCounts = new HashMap<>();
        Map<Long, Double> averageGrades = new HashMap<>();

        for (Object[] stat : statistics) {
            Long courseId = (Long) stat[0];
            Long enrollmentCount = (Long) stat[1];
            Double avgGrade = stat[2] != null ? (Double) stat[2] : 0.0;

            double popularityScore = (enrollmentCount * 0.7) + (avgGrade * 10 * 0.3);

            popularityScores.put(courseId, popularityScore);
            enrollmentCounts.put(courseId, enrollmentCount);
            averageGrades.put(courseId, avgGrade);
        }

        List<Course> allCourses = courseRepository.findByIsActiveTrue();

        List<RecommendationResponseVO> recommendations = new ArrayList<>();

        for (Course course : allCourses) {
            Long courseId = course.getId();
            if (courseId == null) {
                continue;
            }

            if (completedCourseIds.contains(courseId)) {
                continue;
            }

            Double popularityScore = popularityScores.getOrDefault(courseId, 0.0);
            Long enrollmentCount = enrollmentCounts.getOrDefault(courseId, 0L);
            Double avgGrade = averageGrades.getOrDefault(courseId, 0.0);

            if (enrollmentCount == 0) {
                continue;
            }

            double score = 0.0;
            StringBuilder reason = new StringBuilder();

            double maxPopularity = popularityScores.values().stream()
                    .max(Double::compare)
                    .orElse(1.0);
            score += (popularityScore / maxPopularity) * 50;

            reason.append("Popular course with ")
                    .append(enrollmentCount)
                    .append(" students enrolled. ");

            if (avgGrade >= 3.5) {
                score += 30;
                reason.append("High average grade (").append(String.format("%.2f", avgGrade)).append("). ");
            } else if (avgGrade >= 3.0) {
                score += 20;
                reason.append("Good average grade (").append(String.format("%.2f", avgGrade)).append("). ");
            } else if (avgGrade >= 2.5) {
                score += 10;
                reason.append("Moderate average grade (").append(String.format("%.2f", avgGrade)).append("). ");
            }

            Map<Long, Integer> allPrereqs = getAllPrerequisitesWithLevels(courseId);
            List<PrerequisiteCourseVO> prerequisiteChain;
            List<PrerequisiteCourseVO> missingPrerequisites;
            boolean allPrerequisitesMet;

            if (allPrereqs.isEmpty()) {
                prerequisiteChain = new ArrayList<>();
                missingPrerequisites = new ArrayList<>();
                allPrerequisitesMet = true;
                score += 20;
            } else {
                prerequisiteChain = buildPrerequisiteChain(courseId, completedCourseIds);
                missingPrerequisites = getMissingPrerequisites(prerequisiteChain);

                if (missingPrerequisites.isEmpty()) {
                    allPrerequisitesMet = true;
                    score += 20;
                    reason.append("Prerequisites met. ");
                } else {
                    allPrerequisitesMet = false;
                    continue;
                }
            }

            RecommendationResponseVO responseVO = RecommendationResponseVO.builder()
                    .courseId(course.getId())
                    .courseCode(course.getCourseCode())
                    .courseName(course.getCourseName())
                    .description(course.getDescription())
                    .credits(course.getCredits())
                    .difficulty(course.getDifficulty())
                    .matchScore(score)
                    .reason(reason.toString().trim())
                    .status("PENDING")
                    .prerequisiteChain(prerequisiteChain)
                    .missingPrerequisites(missingPrerequisites)
                    .allPrerequisitesMet(allPrerequisitesMet)
                    .build();

            recommendations.add(responseVO);
        }

        recommendations.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));

        return recommendations.stream()
                .limit(limit != null ? limit : 10)
                .collect(Collectors.toList());
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

