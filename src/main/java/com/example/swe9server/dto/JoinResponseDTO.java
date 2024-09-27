package com.example.swe9server.dto;

import lombok.Data;

@Data
public class JoinResponseDTO extends UserResponseDTO {
    private String role;
}