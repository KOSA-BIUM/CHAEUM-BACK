package com.bium.chaeum.presentation;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class HealthCheckController {

    /**
     * 인증된 사용자만 접근 가능한 헬스 체크.
     * - RS 체인에서 JWT 인증을 거치며, SCOPE_api.read 권한이 필요합니다.
     * - 응답에 현재 사용자/권한/토큰 만료 정보 등을 포함해 점검에 활용합니다.
     */
    @PreAuthorize("hasAuthority('SCOPE_api.read')")
    @GetMapping("/api/health/secure")
    public Map<String, Object> secureHealth(@AuthenticationPrincipal Jwt jwt, Authentication authentication) {
        // jwt가 null이면 Bearer 토큰 없이 들어온 것 (권장: 401)
        String username = authentication.getName(); // subject와 동일할 가능성 큼
        List<String> authorities = authentication.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority).sorted().toList();

        return Map.of(
                "status", "UP",
                "user", username,
                "authorities", authorities,
                "tokenIssuedAt", jwt != null && jwt.getIssuedAt() != null ? jwt.getIssuedAt().toString() : null,
                "tokenExpiresAt", jwt != null && jwt.getExpiresAt() != null ? jwt.getExpiresAt().toString() : null,
                "claims", jwt != null ? jwt.getClaims() : Map.of()
        );
    }
}
