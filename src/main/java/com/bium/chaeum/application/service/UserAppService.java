package com.bium.chaeum.application.service;

import com.bium.chaeum.application.request.LoginRequest;
import com.bium.chaeum.application.request.SignUpRequest;
import com.bium.chaeum.application.response.UserResponse;
import com.bium.chaeum.domain.model.entity.User;
import com.bium.chaeum.domain.model.repository.UserRepository;
import com.bium.chaeum.domain.shared.error.DomainException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAppService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse register(SignUpRequest request) {
        if(request == null) throw new IllegalArgumentException("request is required");
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DomainException("Email already in use: " + request.getEmail());
        }

        String encoded = passwordEncoder.encode(request.getPassword());
        User user = User.create(request.getEmail(), encoded, request.getName());
        userRepository.save(user);
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getByEmail(String email) {
        if (email == null) throw new IllegalArgumentException("email is required");
        return userRepository.findByEmail(email).map(UserResponse::from)
                .orElseThrow(() -> new DomainException("user not found"));
    }

    @Transactional(readOnly = true)
    public UserResponse authenticate(LoginRequest request) {
        if (request == null) throw new IllegalArgumentException("request is required");
        return userRepository.findByEmail(request.getEmail())
                .filter(u -> passwordEncoder.matches(request.getPassword(), u.getPassword()))
                .map(UserResponse::from)
                .orElseThrow(() -> new DomainException("user not found"));
    }
}
