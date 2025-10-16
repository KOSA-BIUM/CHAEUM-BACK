package com.bium.chaeum.application.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthFlowTest {

    @LocalServerPort
    int port;

    // 필요시 .properties나 환경변수로 바인딩 가능
    @Value("${test.user.email:test@naver.com}")
    String email;

    @Value("${test.user.password:1234}")
    String password;

    private WebTestClient client() {
        return WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .responseTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Test
    @DisplayName("세션 로그인 → authorize(PKCE) → token 교환 → Bearer로 /api/health/secure 200")
    void full_pkce_flow_with_session_login() throws Exception {
        WebTestClient web = client();

        // 0) PKCE 준비
        String codeVerifier = randomString(64);
        String codeChallenge = s256Base64Url(codeVerifier);

        // 1) 세션 로그인 (JSON) - 세션 쿠키 취득
        var loginResp = web.post()
                .uri("/api/auth/session/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("email", email, "password", password))
                .exchange()
                .expectStatus().isNoContent()
                .returnResult(Void.class);

        // Set-Cookie → Cookie 헤더로 재조립
        String cookieHeader = loginResp.getResponseHeaders().getOrEmpty(HttpHeaders.SET_COOKIE).stream()
                .map(this::cookiePair)     // "JSESSIONID=..." 형태만 추출
                .filter(s -> !s.isBlank())
                .distinct()
                .reduce((a, b) -> a + "; " + b)
                .orElseThrow(() -> new IllegalStateException("세션 쿠키가 없습니다. 로그인 실패로 보입니다."));

        // 2) authorize (리다이렉트 받지 않고 Location 캡쳐 → code 추출)
        String redirectUri = "http://localhost:5173/callback";

        var authResp = web.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/oauth2/authorize")
                        .queryParam("client_id", "vue-spa")
                        .queryParam("response_type", "code")
                        .queryParam("redirect_uri", redirectUri)                   // ★ raw 값
                        .queryParam("scope", "openid profile api.read")            // ★ raw 값
                        .queryParam("code_challenge", codeChallenge)
                        .queryParam("code_challenge_method", "S256")
                        .build())
                .header(HttpHeaders.COOKIE, cookieHeader)
                .exchange()
                .expectStatus().is3xxRedirection()
                .returnResult(Void.class);

        String location = authResp.getResponseHeaders().getFirst(HttpHeaders.LOCATION);
        String code = extractQueryParam(location, "code");

        assertThat(location).as("Location 헤더(콜백)").isNotBlank();
        assertThat(code).as("Authorization Code").isNotBlank();

        // 3) 토큰 교환 (client_id=vue-spa, public + PKCE)
        var tokenJson = web.post()
                .uri("/oauth2/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                        .with("code", code)
                        .with("redirect_uri", redirectUri)       // ★ raw 값
                        .with("client_id", "vue-spa")
                        .with("code_verifier", codeVerifier))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody(Map.class)
                .returnResult()
                .getResponseBody();

        assertThat(tokenJson).isNotNull();
        String accessToken = (String) tokenJson.get("access_token");
        assertThat(accessToken).as("access_token").isNotBlank();

        // 4) 보호 API 호출 (/api/health/secure) — Bearer 인증
        var health = web.get()
                .uri("/api/health/secure")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .returnResult()
                .getResponseBody();

        assertThat(health).isNotNull();
        assertThat(health.get("status")).isEqualTo("UP");
        // (선택) 토큰 만료/발급 시각, 클레임 구조까지 더 검증 가능
    }

    // ================= helpers =================

    private static String url(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private static String randomString(int len) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~";
        StringBuilder sb = new StringBuilder(len);
        java.security.SecureRandom rnd = new java.security.SecureRandom();
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private static String s256Base64Url(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
        String b64 = Base64.getEncoder().encodeToString(digest);
        return b64.replace('+', '-').replace('/', '_').replace("=", "");
    }

    /** Set-Cookie → "name=value"만 뽑기 */
    private String cookiePair(String setCookie) {
        if (setCookie == null || setCookie.isBlank()) return "";
        // "JSESSIONID=xyz; Path=/; HttpOnly" → "JSESSIONID=xyz"
        int semi = setCookie.indexOf(';');
        String pair = (semi > -1 ? setCookie.substring(0, semi) : setCookie).trim();
        return pair.isBlank() ? "" : pair;
    }

    /** Location URI에서 쿼리 파라미터 추출 (한 개 가정) */
    private static String extractQueryParam(String location, String key) {
        try {
            URI uri = URI.create(location);
            String query = uri.getQuery();
            if (query == null) return null;
            for (String part : query.split("&")) {
                int pos = part.indexOf('=');
                if (pos < 0) continue;
                String k = URLDecoder.decode(part.substring(0, pos), StandardCharsets.UTF_8);
                if (key.equals(k)) {
                    return URLDecoder.decode(part.substring(pos + 1), StandardCharsets.UTF_8);
                }
            }
            return null;
        } catch (IllegalArgumentException e) {
            // 혹시 Location이 absolute가 아닐 경우 대비
            int q = location.indexOf('?');
            if (q < 0) return null;
            String query = location.substring(q + 1);
            for (String part : query.split("&")) {
                int pos = part.indexOf('=');
                if (pos < 0) continue;
                String k = URLDecoder.decode(part.substring(0, pos), StandardCharsets.UTF_8);
                if (key.equals(k)) {
                    return URLDecoder.decode(part.substring(pos + 1), StandardCharsets.UTF_8);
                }
            }
            return null;
        }
    }
}

