package com.question.service.question_service.controller;


import com.question.service.question_service.dto.response.ApiResponse;
import com.question.service.question_service.dto.response.UserProfile;
import com.question.service.question_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfile>> getUsers() {
        UserProfile userProfile = userService.getUserProfile();
        return ResponseEntity.ok(ApiResponse.success("User list retrieved successfully", userProfile));
    }
}
