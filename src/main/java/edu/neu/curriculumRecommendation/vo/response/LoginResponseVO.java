package edu.neu.curriculumRecommendation.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseVO {

    private String token;

    @Builder.Default
    private String tokenType = "Bearer";

    // token validity in milliseconds
    private Long expiresIn;

    private Long userId;

    private String username;

    private String email;

    private String role;

    // full name: firstName + " " + lastName
    private String name;

    // NEU student number (nullable)
    private String nuid;

    // student profile id (nullable)
    private Long studentId;
}

