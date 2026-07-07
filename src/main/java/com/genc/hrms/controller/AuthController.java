package com.genc.hrms.controller;

import com.genc.hrms.dto.LoginRequestDto;
import com.genc.hrms.model.UserDetails;
import com.genc.hrms.repository.UserDetailsRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsRepository userDetailsRepository;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public AuthController(AuthenticationManager authenticationManager, UserDetailsRepository userDetailsRepository) {
        this.authenticationManager = authenticationManager;
        this.userDetailsRepository = userDetailsRepository;
    }

    /**
     * Authentication API endpoint.
     * Authenticates credentials and returns the DB role dynamically.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        try {
            // 1. Validate incoming JSON payload inputs
            if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Username is required"));
            }

            if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Password is required"));
            }

            // 2. Authenticate through Spring Security Provider Core
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // 3. Fetch specific authorization profile details from Database
            UserDetails userDetails = userDetailsRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User authorization record could not be found"));

            // 4. Manually commit the established session metadata token to the persistent context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            securityContextRepository.saveContext(SecurityContextHolder.getContext(), request, response);

            // 5. Construct client response metadata payload
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("username", userDetails.getUsername());
            responseBody.put("role", userDetails.getRole());
            responseBody.put("message", "Login successful");

            return ResponseEntity.ok(responseBody);

        } catch (org.springframework.security.core.AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred during login: " + e.getMessage()));
        }
    }

    /**
     * Explicit Security Context Termination API Endpoint
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }
}