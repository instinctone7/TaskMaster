package com.projects.task_master.filters;

import com.projects.task_master.entities.User;
import com.projects.task_master.repositories.JwtTokenRepository;
import com.projects.task_master.repositories.UserRepository;
import com.projects.task_master.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.lang.Collections;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtTokenRepository jwtTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public JwtFilter(JwtTokenRepository jwtTokenRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.jwtTokenRepository = jwtTokenRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authorization.substring(7).trim();

        try {
            Claims claims = jwtUtil.validateToken(jwt);

            String jti = claims.getId();

            if (jwtTokenRepository.existsByJti(jti)) {
                writeError(response, request, "Token has been revoked");
                return;
            }

            String email = claims.getSubject();

            User user = userRepository.findByEmail(email);

            if (user == null) {
                writeError(response, request, "User no longer exists");
                return;
            }

            String role = String.valueOf(claims.get("role"));

            String authority = role != null && role.startsWith("ROLE_") ? role : "ROLE_" + role;
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(authority));

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            authorities
                    );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

        } catch (JwtException e) {
            writeError(response, request, "Invalid or expired token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void writeError(HttpServletResponse response, HttpServletRequest request, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(
                "{\"success\": false, \"message\": \"" + message + "\", \"statusCode\": 401, \"path\": \"" + request.getRequestURI() + "\"}"
        );
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getRequestURI().equals("/api/auth/signIn") ||
                request.getRequestURI().equals("/api/auth/register") ||
                request.getRequestURI().equals("/api/auth/verification");
    }
}