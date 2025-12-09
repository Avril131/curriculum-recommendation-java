package edu.neu.curriculumRecommendation.exception;

/**
 * Resource Not Found Exception
 * Custom exception for resource not found scenarios
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}

