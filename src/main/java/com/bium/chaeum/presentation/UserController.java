package com.bium.chaeum.presentation;

import com.bium.chaeum.application.request.SignUpRequest;
import com.bium.chaeum.application.response.UserResponse;
import com.bium.chaeum.application.service.UserAppService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserAppService userAppService;

    /**
     * 회원 가입
     * Body: { "email": "...", "password": "...", ... }
     * 반환: 201 Created + UserResponse
     */
    @PostMapping("/signup")
    @Transactional
    public ResponseEntity<UserResponse> signUp(@RequestBody @Valid SignUpRequest request) {
    	System.out.println("회원가입 실행 -------------");
        UserResponse created = userAppService.register(request);
        // Location 헤더에 신규 리소스 위치 힌트(선택)
        return ResponseEntity
                .created(URI.create("/api/users/" + created.getUserId()))
                .body(created);
    }
}
