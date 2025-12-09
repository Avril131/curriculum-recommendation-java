package edu.neu.curriculumRecommendation.service.impl;

import edu.neu.curriculumRecommendation.dto.StudentDTO;
import edu.neu.curriculumRecommendation.entity.Student;
import edu.neu.curriculumRecommendation.entity.User;
import edu.neu.curriculumRecommendation.mapper.converter.StudentConverter;
import edu.neu.curriculumRecommendation.mapper.repository.StudentRepository;
import edu.neu.curriculumRecommendation.mapper.repository.UserRepository;
import edu.neu.curriculumRecommendation.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Student Service Implementation
 */
@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentConverter studentConverter;
    private final UserRepository userRepository;

    public StudentServiceImpl(StudentRepository studentRepository, StudentConverter studentConverter, UserRepository userRepository) {
        this.studentRepository = studentRepository;
        this.studentConverter = studentConverter;
        this.userRepository = userRepository;
    }

    @Override
    public StudentDTO createStudent(StudentDTO studentDTO) {
        Student student = studentConverter.dtoToEntity(studentDTO);
        // Set user entity from userId
        User user = userRepository.findById(studentDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + studentDTO.getUserId()));
        student.setUser(user);
        Student savedStudent = studentRepository.save(student);
        return studentConverter.entityToDto(savedStudent);
    }

    @Override
    public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));

        student.setFirstName(studentDTO.getFirstName());
        student.setLastName(studentDTO.getLastName());
        student.setMajor(studentDTO.getMajor());
        student.setGpa(studentDTO.getGpa());
        student.setEnrollmentYear(studentDTO.getEnrollmentYear());
        student.setCareerInterests(studentDTO.getCareerInterests());

        Student updatedStudent = studentRepository.save(student);
        return studentConverter.entityToDto(updatedStudent);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDTO findById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        return studentConverter.entityToDto(student);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> findAll() {
        List<Student> students = studentRepository.findAll();
        return studentConverter.entitiesToDtos(students);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDTO findByUserId(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Student not found with userId: " + userId));
        return studentConverter.entityToDto(student);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDTO findByStudentId(String studentId) {
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with studentId: " + studentId));
        return studentConverter.entityToDto(student);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> findByMajor(String major) {
        List<Student> students = studentRepository.findByMajor(major);
        return studentConverter.entitiesToDtos(students);
    }

    @Override
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        studentRepository.delete(student);
    }
}

