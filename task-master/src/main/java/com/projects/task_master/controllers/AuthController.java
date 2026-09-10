package com.projects.task_master.controllers;

import com.projects.task_master.dtos.requests.LoginRequest;
import com.projects.task_master.dtos.requests.UserRequestDto;
import com.projects.task_master.dtos.responses.UserResponseDto;
import com.projects.task_master.handlers.ApiResponse;
import com.projects.task_master.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService userService;

    public AuthController(AuthService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDto>> registerUser(@RequestBody UserRequestDto req) {
        UserResponseDto responseDto = userService.registerUser(req);
        ApiResponse<UserResponseDto> apiResponse = ApiResponse.
                success("User registered successfully", responseDto);
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/signIn")
    public ResponseEntity<ApiResponse<String>> signInUser(@RequestBody LoginRequest request) {
        String token = userService.signIn(request);
        ApiResponse<String> response = ApiResponse.success("User signed in successfully", token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
            userService.loggingOut(request);
        return ResponseEntity.ok("Logged Out Successfully");
    }
}
