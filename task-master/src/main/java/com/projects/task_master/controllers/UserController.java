package com.projects.task_master.controllers;

import com.projects.task_master.dtos.requests.UpdateUserDto;
import com.projects.task_master.dtos.responses.UserResponseDto;
import com.projects.task_master.entities.User;
import com.projects.task_master.handlers.ApiResponse;
import com.projects.task_master.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserProfile(@AuthenticationPrincipal User user) {
          UserResponseDto responseDto = userService.userProfile(user);
          ApiResponse<UserResponseDto> response = ApiResponse.success("User Profile",responseDto);
          return ResponseEntity.ok(response);
    }
    @PostMapping("/update")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUserInfo(@AuthenticationPrincipal User user,
                                                                       @RequestBody UpdateUserDto updateUserDto){
            UserResponseDto responseDto = userService.updateUser(user,updateUserDto);
            ApiResponse<UserResponseDto> response = ApiResponse.success("User Updated Successfully",responseDto);
            return ResponseEntity.ok(response);
    }
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@AuthenticationPrincipal User user,@PathVariable Long id) {
        userService.deleteUser(user,id);
        return ResponseEntity.ok(ApiResponse.success("User Deletion is successful",null));
    }
}
