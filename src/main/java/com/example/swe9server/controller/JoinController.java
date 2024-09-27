package com.example.swe9server.controller;

import com.example.swe9server.dto.JoinDTO;
import com.example.swe9server.dto.JoinResponseDTO;
import com.example.swe9server.dto.ServerResponseDTO;
import com.example.swe9server.entity.RefreshEntity;
import com.example.swe9server.entity.UserEntity;
import com.example.swe9server.exception.DuplicateResourceException;
import com.example.swe9server.jwt.JWTUtil;
import com.example.swe9server.repository.RefreshRepository;
import com.example.swe9server.service.JoinService;
import com.example.swe9server.utils.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Date;

@RestController
@RequestMapping("/api")
public class JoinController {
    private final JoinService joinService;
    private final ModelMapper modelMapper;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public JoinController(JoinService joinService, ModelMapper modelMapper, JWTUtil jwtUtil, RefreshRepository refreshRepository) {
        this.joinService = joinService;
        this.modelMapper = modelMapper;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinProcess(@RequestBody @Valid JoinDTO joinDTO, HttpServletResponse response) {
        UserEntity createdUser = joinService.joinProcess(joinDTO);
        JoinResponseDTO userDTO = modelMapper.map(createdUser, JoinResponseDTO.class);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/users/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();

        Long userId = createdUser.getId();

        String accessToken = jwtUtil.createJwt("access", userId, 1800000L);
        String refreshToken = jwtUtil.createJwt("refresh", userId, 1209600000L);

        Date date = new Date(System.currentTimeMillis() + 1209600000L);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUserId(userId);
        refreshEntity.setRefresh(refreshToken);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);

        response.setHeader("access", accessToken);
        response.addCookie(JWTUtil.createCookie("refresh", refreshToken));

        return ResponseUtil.createSuccessResponse(userDTO, HttpStatus.CREATED, location);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<?> handleDuplicateResourceException(DuplicateResourceException ex, HttpServletRequest request) {
        String endpoint = ResponseUtil.extractEndpoint(request.getRequestURI());
        return ResponseUtil.createErrorResponse(
                HttpStatus.CONFLICT,
                endpoint + "/duplicate_resource",
                ex.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ServerResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("Validation error");

        String endpoint = ResponseUtil.extractEndpoint(request.getRequestURI());

        return ResponseUtil.createErrorResponse(
                HttpStatus.BAD_REQUEST,
                endpoint + "/validation_failed",
                errorMessage
        );
    }
}