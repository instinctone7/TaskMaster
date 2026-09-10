package com.projects.task_master.services;

import com.projects.task_master.dtos.requests.LoginRequest;
import com.projects.task_master.dtos.requests.UserRequestDto;
import com.projects.task_master.dtos.responses.UserResponseDto;
import com.projects.task_master.entities.BlackListJwt;
import com.projects.task_master.entities.User;
import com.projects.task_master.enums.Roles;
import com.projects.task_master.exceptions.PasswordIncorrect;
import com.projects.task_master.exceptions.UserNotFound;
import com.projects.task_master.mappers.UserMapper;
import com.projects.task_master.repositories.JwtTokenRepository;
import com.projects.task_master.repositories.UserRepository;
import com.projects.task_master.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;


@Service
public class AuthService implements UserDetailsService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final JwtTokenRepository jwtTokenRepository;


    public AuthService(BCryptPasswordEncoder passwordEncoder, UserRepository userRepository, UserMapper userMapper, JwtUtil jwtUtil, JwtTokenRepository jwtTokenRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
        this.jwtTokenRepository = jwtTokenRepository;
    }

    public UserResponseDto registerUser(UserRequestDto req) {
        User user = User.builder()
                .name(req.name())
                .email(req.email())
                .bio(req.bio())
                .password(passwordEncoder.encode(req.password()))
                .role(Roles.USER)   // hardcode here, ignore req.role() entirely
                .build();
        userRepository.save(user);
        return userMapper.FromUsertoUserResponseDto(user);
    }

    public String signIn(LoginRequest request) {
        User user = userRepository.findByEmail(request.email());
        if(user == null) {
            throw new UserNotFound("User not found with email: " + request.email());
        }
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new PasswordIncorrect("Invalid password");
        }
        return jwtUtil.generateToken(user);
    }

    @Override
    @NonNull
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(username);
        if (user == null){
            throw new UserNotFound("User Not Found");
        }
        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }

    public void loggingOut(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7).trim();
        String jti = jwtUtil.extractJti(token);
        Instant expiry = jwtUtil.extractExpiration(token).toInstant();

        BlackListJwt blacklisted = new BlackListJwt();
        blacklisted.setJti(jti);
        blacklisted.setExpires(expiry);
        jwtTokenRepository.save(blacklisted);
    }
}
