package edu.neu.curriculumRecommendation.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * API Response VO
 * Generic response wrapper for all API responses
 *
 * @param <T> The type of data in the response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponseVO<T> {

    private Boolean success;

    private String message;

    private T data;

    private LocalDateTime timestamp;

    /**
     * Create a successful response with data
     *
     * @param data The response data
     * @param <T>  The type of data
     * @return ApiResponseVO with success status
     */
    public static <T> ApiResponseVO<T> success(T data) {
        return ApiResponseVO.<T>builder()
                .success(true)
                .message("Success")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create a successful response with custom message and data
     *
     * @param message The response message
     * @param data    The response data
     * @param <T>     The type of data
     * @return ApiResponseVO with success status
     */
    public static <T> ApiResponseVO<T> success(String message, T data) {
        return ApiResponseVO.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create an error response with error message
     *
     * @param message The error message
     * @param <T>     The type of data (can be null for errors)
     * @return ApiResponseVO with error status
     */
    public static <T> ApiResponseVO<T> error(String message) {
        return ApiResponseVO.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }
}

