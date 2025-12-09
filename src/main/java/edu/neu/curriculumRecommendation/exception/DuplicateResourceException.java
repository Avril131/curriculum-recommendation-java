package edu.neu.curriculumRecommendation.exception;

/**
 * Duplicate Resource Exception
 * Custom exception for duplicate resource scenarios
 * (e.g., email already exists, course code already exists)
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}

