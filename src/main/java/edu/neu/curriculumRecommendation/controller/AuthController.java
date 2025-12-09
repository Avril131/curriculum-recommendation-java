package edu.neu.curriculumRecommendation.controller;

import edu.neu.curriculumRecommendation.dto.UserDTO;
import edu.neu.curriculumRecommendation.exception.DuplicateResourceException;
import edu.neu.curriculumRecommendation.exception.ResourceNotFoundException;
import edu.neu.curriculumRecommendation.service.AuthService;
import edu.neu.curriculumRecommendation.vo.request.LoginRequestVO;
import edu.neu.curriculumRecommendation.vo.request.RegisterRequestVO;
import edu.neu.curriculumRecommendation.vo.response.LoginResponseVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * Authentication Controller
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * User login
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseVO> login(@Valid @RequestBody LoginRequestVO loginRequest) {
        try {
            LoginResponseVO response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * User registration (auto login)
     */
    @PostMapping("/register")
    public ResponseEntity<LoginResponseVO> register(@Valid @RequestBody RegisterRequestVO registerRequest) {
        try {
            LoginResponseVO response = authService.register(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (DuplicateResourceException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get current user info
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            String token = extractToken(authHeader);
            UserDTO user = authService.getCurrentUser(token);
            return ResponseEntity.ok(user);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Validate token
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Boolean>> validateToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Boolean> result = new HashMap<>();
        try {
            String token = extractToken(authHeader);
            boolean valid = authService.validateToken(token);
            result.put("valid", valid);
            return valid ? ResponseEntity.ok(result) : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        } catch (Exception ex) {
            result.put("valid", false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }
    }

    /**
     * User logout (stateless JWT)
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout successful");
        return ResponseEntity.ok(response);
    }

    /**
     * Extract token from Authorization header
     */
    private String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResourceNotFoundException("Invalid token");
        }
        return authHeader.substring(7);
    }
}

