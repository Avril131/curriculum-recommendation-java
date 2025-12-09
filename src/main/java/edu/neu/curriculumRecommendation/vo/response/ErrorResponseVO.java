package edu.neu.curriculumRecommendation.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Error Response VO
 * Standard error response format for API errors
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponseVO {

    private Integer status;

    private String error;

    private String message;

    private String path;

    private LocalDateTime timestamp;
}

