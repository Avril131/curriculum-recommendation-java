package edu.neu.curriculumRecommendation.service.impl;

import edu.neu.curriculumRecommendation.dto.UserDTO;
import edu.neu.curriculumRecommendation.entity.Student;
import edu.neu.curriculumRecommendation.entity.User;
import edu.neu.curriculumRecommendation.exception.DuplicateResourceException;
import edu.neu.curriculumRecommendation.exception.ResourceNotFoundException;
import edu.neu.curriculumRecommendation.mapper.converter.UserConverter;
import edu.neu.curriculumRecommendation.mapper.repository.StudentRepository;
import edu.neu.curriculumRecommendation.mapper.repository.UserRepository;
import edu.neu.curriculumRecommendation.service.AuthService;
import edu.neu.curriculumRecommendation.util.JwtUtil;
import edu.neu.curriculumRecommendation.vo.request.LoginRequestVO;
import edu.neu.curriculumRecommendation.vo.request.RegisterRequestVO;
import edu.neu.curriculumRecommendation.vo.response.LoginResponseVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Authentication Service Implementation
 */
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final UserConverter userConverter;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository,
                           StudentRepository studentRepository,
                           UserConverter userConverter,
                           JwtUtil jwtUtil,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.userConverter = userConverter;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponseVO login(LoginRequestVO loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new ResourceNotFoundException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        Optional<Student> studentOpt = studentRepository.findByUserId(user.getId());
        Student student = studentOpt.orElse(null);
        String name = null;
        String nuid = null;
        Long studentProfileId = null;
        if (student != null) {
            name = String.format("%s %s",
                    student.getFirstName() != null ? student.getFirstName() : "",
                    student.getLastName() != null ? student.getLastName() : "").trim();
            nuid = student.getStudentId();
            studentProfileId = student.getId();
        }

        return LoginResponseVO.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpiration())
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .name(name != null && !name.isBlank() ? name : null)
                .nuid(nuid)
                .studentId(studentProfileId)
                .build();
    }

    @Override
    public LoginResponseVO register(RegisterRequestVO registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new DuplicateResourceException("Username already taken");
        }

        // Create User
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setRole("STUDENT");
        user.setIsActive(true);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        User savedUser = userRepository.save(user);

        // Create Student
        Student student = new Student();
        student.setUser(savedUser);
        student.setFirstName(registerRequest.getFirstName());
        student.setLastName(registerRequest.getLastName());
        student.setStudentId(registerRequest.getStudentId());
        student.setMajor(registerRequest.getMajor());
        student.setGpa(registerRequest.getGpa());
        student.setEnrollmentYear(registerRequest.getEnrollmentYear() != null
                ? registerRequest.getEnrollmentYear()
                : LocalDate.now().getYear());
        student.setCareerInterests(registerRequest.getCareerInterests());
        studentRepository.save(student);

        String token = jwtUtil.generateToken(savedUser.getEmail());

        String fullName = String.format("%s %s",
                registerRequest.getFirstName(),
                registerRequest.getLastName()).trim();

        return LoginResponseVO.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpiration())
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .name(fullName.isBlank() ? null : fullName)
                .nuid(registerRequest.getStudentId())
                .studentId(student.getId())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getCurrentUser(String token) {
        String email = jwtUtil.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return userConverter.entityToDto(user);
    }

    @Override
    public Boolean validateToken(String token) {
        String email = jwtUtil.extractUsername(token);
        return email != null && jwtUtil.validateToken(token, email);
    }
}

