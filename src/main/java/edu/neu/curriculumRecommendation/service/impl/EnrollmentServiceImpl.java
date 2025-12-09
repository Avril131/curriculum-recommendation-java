package edu.neu.curriculumRecommendation.service.impl;

import edu.neu.curriculumRecommendation.dto.EnrollmentDTO;
import edu.neu.curriculumRecommendation.entity.Course;
import edu.neu.curriculumRecommendation.entity.Enrollment;
import edu.neu.curriculumRecommendation.entity.Student;
import edu.neu.curriculumRecommendation.exception.DuplicateResourceException;
import edu.neu.curriculumRecommendation.exception.ResourceNotFoundException;
import edu.neu.curriculumRecommendation.mapper.converter.EnrollmentConverter;
import edu.neu.curriculumRecommendation.mapper.repository.CourseRepository;
import edu.neu.curriculumRecommendation.mapper.repository.EnrollmentRepository;
import edu.neu.curriculumRecommendation.mapper.repository.StudentRepository;
import edu.neu.curriculumRecommendation.service.EnrollmentService;
import edu.neu.curriculumRecommendation.util.GradeUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentConverter enrollmentConverter;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository,
                                 EnrollmentConverter enrollmentConverter,
                                 StudentRepository studentRepository,
                                 CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.enrollmentConverter = enrollmentConverter;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public EnrollmentDTO createEnrollment(EnrollmentDTO enrollmentDTO) {
        Student student = studentRepository.findById(enrollmentDTO.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + enrollmentDTO.getStudentId()));
        Course course = courseRepository.findById(enrollmentDTO.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + enrollmentDTO.getCourseId()));

        boolean exists = enrollmentRepository.existsByStudentIdAndCourseIdAndSemesterAndYear(
                enrollmentDTO.getStudentId(),
                enrollmentDTO.getCourseId(),
                enrollmentDTO.getSemester(),
                enrollmentDTO.getYear()
        );
        if (exists) {
            throw new DuplicateResourceException("Student already enrolled in this course for this semester");
        }

        Enrollment enrollment = enrollmentConverter.dtoToEntity(enrollmentDTO);
        enrollment.setStudent(student);
        enrollment.setCourse(course);

        Enrollment saved = enrollmentRepository.save(enrollment);

        // Recalculate GPA if completed with grade
        if ("COMPLETED".equalsIgnoreCase(saved.getStatus()) && saved.getGrade() != null) {
            recalculateStudentGPA(student.getId());
        }

        return enrollmentConverter.entityToDto(saved);
    }

    @Override
    public EnrollmentDTO updateEnrollment(Long id, EnrollmentDTO enrollmentDTO) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));

        String oldStatus = enrollment.getStatus();
        String oldGrade = enrollment.getGrade();

        enrollment.setSemester(enrollmentDTO.getSemester());
        enrollment.setYear(enrollmentDTO.getYear());
        enrollment.setGrade(enrollmentDTO.getGrade());
        enrollment.setStatus(enrollmentDTO.getStatus());
        enrollment.setCompletedAt(enrollmentDTO.getCompletedAt());

        Enrollment updated = enrollmentRepository.save(enrollment);

        boolean statusChangedToCompleted = oldStatus == null || !"COMPLETED".equalsIgnoreCase(oldStatus);
        boolean nowCompleted = "COMPLETED".equalsIgnoreCase(updated.getStatus());
        boolean gradeChanged = (oldGrade == null && updated.getGrade() != null)
                || (oldGrade != null && !oldGrade.equals(updated.getGrade()));

        if ((statusChangedToCompleted && nowCompleted) || gradeChanged) {
            recalculateStudentGPA(updated.getStudent().getId());
        }

        return enrollmentConverter.entityToDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> findByStudentId(Long studentId) {
        return enrollmentConverter.entitiesToDtos(enrollmentRepository.findByStudentId(studentId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> findByStudentIdAndStatus(Long studentId, String status) {
        return enrollmentConverter.entitiesToDtos(enrollmentRepository.findByStudentIdAndStatus(studentId, status));
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentDTO findById(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));
        return enrollmentConverter.entityToDto(enrollment);
    }

    @Override
    public void deleteEnrollment(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));
        Long studentId = enrollment.getStudent().getId();
        enrollmentRepository.delete(enrollment);
        recalculateStudentGPA(studentId);
    }

    @Override
    public void recalculateStudentGPA(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        List<Enrollment> completedEnrollments =
                enrollmentRepository.findByStudentIdAndStatus(studentId, "COMPLETED");

        Double gpa = GradeUtil.calculateWeightedGPA(completedEnrollments);
        student.setGpa(gpa);
        studentRepository.save(student);
    }
}

