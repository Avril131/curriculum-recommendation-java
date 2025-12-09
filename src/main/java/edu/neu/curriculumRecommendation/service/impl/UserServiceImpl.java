package edu.neu.curriculumRecommendation.service.impl;

import edu.neu.curriculumRecommendation.dto.UserDTO;
import edu.neu.curriculumRecommendation.entity.User;
import edu.neu.curriculumRecommendation.mapper.converter.UserConverter;
import edu.neu.curriculumRecommendation.mapper.repository.UserRepository;
import edu.neu.curriculumRecommendation.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User Service Implementation
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;

    public UserServiceImpl(UserRepository userRepository, UserConverter userConverter) {
        this.userRepository = userRepository;
        this.userConverter = userConverter;
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        // Create User entity manually since password is ignored in converter
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole() != null ? userDTO.getRole() : "STUDENT");
        user.setIsActive(userDTO.getIsActive() != null ? userDTO.getIsActive() : true);
        // Set a default password for testing (in production, this should be hashed)
        user.setPassword("password123"); // Default password for testing
        
        User savedUser = userRepository.save(user);
        return userConverter.entityToDto(savedUser);
    }
}

