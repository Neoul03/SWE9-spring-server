package com.example.comitserver.jwt;

import com.example.comitserver.service.CustomUserDetailsService;
import com.example.comitserver.utils.ResponseUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {
    private final CustomUserDetailsService customUserDetailsService;

    private final JWTUtil jwtUtil;
    public JWTFilter(JWTUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Bearer 이후의 토큰 부분만 추출
        String accessToken = authorizationHeader.substring(7);

        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            ResponseUtil.writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Unauthorized", "Access token expired");
            return;
        } catch (JwtException e) {
            ResponseUtil.writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Unauthorized", "Invalid JWT token");
            return;
        }

        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);
        if (!category.equals("access")) {
            ResponseUtil.writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Unauthorized", "Invalid access token");
            return;
        }

        Long userId = jwtUtil.getUserId(accessToken);

        if (userId == null) {
            ResponseUtil.writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Unauthorized", "User ID not found in token");
            return;
        }

        UserDetails userDetails = customUserDetailsService.loadUserById(userId);

        if (userDetails == null) {
            ResponseUtil.writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Unauthorized", "User not found");
            return;
        }

        Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}