package com.mustafabulu.elk.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Authentication request payload")
public class AuthRequest {

    @NotBlank(message = "Username is required")
    @Schema(description = "Account username", example = "demoUser")
    private String username;

    @NotBlank(message = "Password is required")
    @Schema(description = "Account password", example = "P@ssw0rd123")
    private String password;

}

