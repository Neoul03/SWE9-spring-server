package com.example.swe9server.dto;

import com.example.swe9server.entity.Role;
import lombok.Data;

@Data
public class AdminUserResponseDTO {
    private Long id;
    private String username;
    private String phoneNumber;
    private String studentId;
    private String email;
    private String position;
    private Boolean isStaff;
    private String bio;
    private String github;
    private String blog;
    private String profileImage;
    private Role role;
}
