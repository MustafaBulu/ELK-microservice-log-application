package com.mustafabulu.elk.authservice.controller.docs;

import com.mustafabulu.elk.authservice.dto.AuthRequest;
import com.mustafabulu.elk.authservice.entity.UserCredential;
import com.mustafabulu.elk.authservice.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

@Tag(name = "Auth API", description = "Authentication and JWT operations")
@SuppressWarnings("unused")
public interface AuthApiDoc {

    @Operation(
            summary = "Register a new user",
            description = "Creates a user account, hashes password, and queues USER_REGISTERED event via outbox."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User registered successfully",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "User Added to the system")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error or duplicate user",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {"timestamp":"2026-02-17T01:00:00Z","status":400,"error":"Bad Request","message":"Email is required","path":"/auth/register"}
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    ResponseEntity<String> addNewUser(UserCredential user);

    @Operation(
            summary = "Generate JWT token",
            description = "Authenticates username/password and returns signed JWT token."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Token generated",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(
                                    type = "string",
                                    example = "jwt-token-placeholder"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    ResponseEntity<String> getToken(AuthRequest authRequest);

    @Operation(
            summary = "Validate JWT token",
            description = "Validates bearer token and returns 200 if token is valid."
    )
    @SecurityRequirement(name = "bearerAuth")
    @Parameter(
            name = HttpHeaders.AUTHORIZATION,
            in = ParameterIn.HEADER,
            required = true,
            description = "Bearer token header",
            example = "Bearer <jwt-token>"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Token is valid",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Token is valid")
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token invalid or expired",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid authorization header format",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    ResponseEntity<String> validateToken(String authorizationHeader);
}

