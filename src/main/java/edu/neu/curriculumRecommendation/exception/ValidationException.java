package edu.neu.curriculumRecommendation.exception;

/**
 * Validation Exception
 * Custom exception for business validation failures
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}

