package edu.neu.curriculumRecommendation.service;

import edu.neu.curriculumRecommendation.dto.UserDTO;

/**
 * User Service Interface
 * Provides business logic for user management
 */
public interface UserService {

    /**
     * Create a new user
     *
     * @param userDTO User data transfer object
     * @return Created user DTO
     */
    UserDTO createUser(UserDTO userDTO);
}

