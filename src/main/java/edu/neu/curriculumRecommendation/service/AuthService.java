package edu.neu.curriculumRecommendation.service;

import edu.neu.curriculumRecommendation.dto.UserDTO;
import edu.neu.curriculumRecommendation.vo.request.LoginRequestVO;
import edu.neu.curriculumRecommendation.vo.request.RegisterRequestVO;
import edu.neu.curriculumRecommendation.vo.response.LoginResponseVO;

/**
 * Authentication Service Interface
 */
public interface AuthService {

    /**
     * User login
     *
     * @param loginRequest login request payload
     * @return login response with token
     */
    LoginResponseVO login(LoginRequestVO loginRequest);

    /**
     * User registration
     *
     * @param registerRequest registration payload
     * @return login response with token
     */
    LoginResponseVO register(RegisterRequestVO registerRequest);

    /**
     * Get current user info from token
     *
     * @param token JWT token
     * @return user DTO
     */
    UserDTO getCurrentUser(String token);

    /**
     * Validate token
     *
     * @param token JWT token
     * @return true if valid, false otherwise
     */
    Boolean validateToken(String token);
}

