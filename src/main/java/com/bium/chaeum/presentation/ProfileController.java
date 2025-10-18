package com.bium.chaeum.presentation;

import com.bium.chaeum.application.request.RegisterProfileRequest;
import com.bium.chaeum.application.request.UpdateProfileRequest;
import com.bium.chaeum.application.response.ProfileResponse;
import com.bium.chaeum.application.service.ProfileAppService;
import com.bium.chaeum.domain.model.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileAppService profileAppService;

    // 현재 로그인 사용자의 프로필 조회
    @PreAuthorize("hasAuthority('SCOPE_api.read')")
    @GetMapping("/me")
    public ProfileResponse getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        String userId = requireUserId(jwt);
        return profileAppService.getProfile(UserId.of(userId)); // 없으면 AppService에서 예외 or null 처리
    }

    // 현재 로그인 사용자의 프로필 신규 등록
    @PreAuthorize("hasAuthority('SCOPE_api.write')")
    @PostMapping
    public ProfileResponse registerMyProfile(@AuthenticationPrincipal Jwt jwt,
                                             @Valid @RequestBody RegisterProfileRequest request) {
        String userId = requireUserId(jwt);

        return profileAppService.register(UserId.of(userId), request);
    }

    // 현재 로그인 사용자의 프로필 수정
    @PreAuthorize("hasAuthority('SCOPE_api.write')")
    @PutMapping
    public ProfileResponse updateMyProfile(@AuthenticationPrincipal Jwt jwt,
                                           @Valid @RequestBody UpdateProfileRequest request) {
        String userId = requireUserId(jwt);
        return profileAppService.update(UserId.of(userId), request);
    }

    private static String requireUserId(Jwt jwt) {
        if (jwt == null) throw new IllegalStateException("JWT is required");
        Object val = jwt.getClaim("user_id");
        if (val == null) throw new IllegalStateException("user_id claim missing");
        return String.valueOf(val);
    }
}
