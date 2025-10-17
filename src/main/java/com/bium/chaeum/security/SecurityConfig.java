package com.bium.chaeum.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity
public class SecurityConfig {

    // 1) Authorization Server 체인 (우선적용)
    @Bean
    @Order(1)
    SecurityFilterChain asSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServer = new OAuth2AuthorizationServerConfigurer();

        // AS 기본 엔드포인트 매처
        RequestMatcher endpointsMatcher = authorizationServer.getEndpointsMatcher();

        http
                .securityMatcher(endpointsMatcher)
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
                // 미인증 -> JSON 401 (SPA가 로그인 API 호출하도록 유도)
                .exceptionHandling(e -> e.authenticationEntryPoint((req, res, ex) -> {
                    res.setStatus(401);
                    res.setContentType("application/json");
                    res.getWriter().write("{\"error\":\"unauthorized\"}");
                }))
                // OIDC 활성화 (/.well-known/openid-configuration, /userinfo 등)
                .with(authorizationServer, as -> as.oidc(Customizer.withDefaults()))
                .cors(cors -> cors.configurationSource(corsConfigForSpa()))
                // 세션이 필요하므로, STATELESS 금지(기본값 IF_REQUIRED면 충분)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

        return http.build();
    }

    // 2) Authorization Server: 세션 로그인 전용 체인 (/api/auth/session/**)
    @Bean
    @Order(2)
    SecurityFilterChain sessionLoginChain(HttpSecurity http) throws Exception {
        RequestMatcher authSessionMatcher = req -> {
            String ctx = req.getContextPath() == null ? "" : req.getContextPath();
            String uri = req.getRequestURI();
            return uri.startsWith(ctx + "/api/auth/session/");
        };

        http
                .securityMatcher(authSessionMatcher)
                .authorizeHttpRequests(a -> a.anyRequest().permitAll())
                // JSON 로그인/로그아웃은 CSRF 제외(모던 SPA는 헤더·CORS로 보호)
                .csrf(csrf -> csrf.disable())
                .cors(c -> c.configurationSource(corsConfigForSpa()))
                // 브라우저 세션 필요 (authorize 단계에서 로그인 상태 유지)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
        return http.build();
    }

    // 3) Resource Server 체인 (/api/**) - JWT, 무상태
    @Bean
    @Order(3)
    SecurityFilterChain rsSecurityFilterChain(HttpSecurity http) throws Exception {
        // "/api/**"만 매칭하는 간단한 RequestMatcher
        RequestMatcher apiMatcher = req -> req.getRequestURI().startsWith("/api/");

        http
                .securityMatcher(apiMatcher)
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/public/**").permitAll()
                        // 세션 로그인 API는 RS 체인이 아니라 기본 체인에서 처리하므로 여기선 제외
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter())))
                .cors(cors -> cors.configurationSource(corsConfigForSpa()));

        return http.build();
    }

    // 4) Authorization Server - 기본 체인 (나머지)
    @Bean
    @Order(4)
    SecurityFilterChain defaultWebSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/health", "/assets/**", "/css/**", "/js/**", "/images/**").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable())
                .cors(c -> c.configurationSource(corsConfigForSpa()));
        return http.build();
    }



    // Authorization Server) SPA 클라이언트 등록 (Public + PKCE)
    @Bean
    RegisteredClientRepository registeredClientRepository() {
        RegisteredClient spa = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("vue-spa")
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE) // public
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://localhost:5173/callback")
//                .redirectUri("https://oauth.pstmn.io/v1/callback")
                .postLogoutRedirectUri("http://localhost:5173")
                .scope("openid").scope("profile")
                .scope("api.read").scope("api.write")
                .clientSettings(ClientSettings.builder().requireProofKey(true).build()) // PKCE
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(15))
                        .refreshTokenTimeToLive(Duration.ofDays(14))
                        .reuseRefreshTokens(false) // 회전 권장
                        .build())
                .build();
        return new InMemoryRegisteredClientRepository(spa);
    }

    // Authorization Server) 개발용 CORS — 토큰/인가 엔드포인트 + API 모두 허용
    private CorsConfigurationSource corsConfigForSpa() {
        CorsConfiguration conf = new CorsConfiguration();
        conf.setAllowedOrigins(List.of("http://localhost:5173")); // Vue dev
        conf.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        conf.setAllowedHeaders(List.of("Authorization","Content-Type"));
        conf.setAllowCredentials(true); // refresh 쿠키 쓰면 true 필요
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", conf);
        return source;
    }

    // Authorization Server) 액세스 토큰 커스터마이저 (roles 등 커스텀 클레임)
    @Bean
    OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return context -> {
            // 1) Access Token에만 커스텀 클레임 추가
            if (!"access_token".equals(context.getTokenType().getValue())) return;

            // 2) client_credentials 등 클라이언트 자격 플로우는 skip
            if (AuthorizationGrantType.CLIENT_CREDENTIALS.equals(context.getAuthorizationGrantType())) return;

            Authentication authentication = context.getPrincipal();
            if (authentication == null || authentication.getName() == null) return;

            // 3) 권한 → roles 클레임
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            if (authorities != null && !authorities.isEmpty()) {
                List<String> roles = authorities.stream()
                        .map(GrantedAuthority::getAuthority)      // e.g. ROLE_USER
                        .filter(a -> a.startsWith("ROLE_"))
                        .map(a -> a.substring("ROLE_".length()))  // -> USER
                        .distinct()
                        .toList();
                context.getClaims().claim("roles", roles);
            }

            // 4) name 클레임 (표시용)
            String usernameAttr = authentication.getName();
            context.getClaims().claim("username", usernameAttr);

            // 5) 내부 주체 꺼내서 CustomUserDetails 판별
            Object userPrincipal = authentication.getPrincipal();
            if (userPrincipal instanceof CustomUserDetails customUserDetails) {
                String userId = customUserDetails.getUserId().value();
                context.getClaims().claim("user_id", userId);
            }
        };
    }

    // Resource Server) scope/roles → GrantedAuthority 매핑
    private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthConverter() {
        var scopeConv = new JwtGrantedAuthoritiesConverter();
        scopeConv.setAuthorityPrefix("SCOPE_"); // scope → SCOPE_xxx
        return jwt -> {
            Collection<GrantedAuthority> auths = new ArrayList<>(scopeConv.convert(jwt));
            var roles = (Collection<String>) jwt.getClaims().getOrDefault("roles", List.of());
            roles.stream().map(r -> "ROLE_" + r).map(SimpleGrantedAuthority::new).forEach(auths::add);
            return new JwtAuthenticationToken(jwt, auths, jwt.getSubject());
        };
    }

    // Authorization Server) RSA 키쌍(JWK)
    @Bean
    JWKSource<SecurityContext> jwkSource() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();

        RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) kp.getPublic())
                .privateKey((RSAPrivateKey) kp.getPrivate())
                .keyID(UUID.randomUUID().toString())
                .build();

        JWKSet jwkSet = new JWKSet(rsaKey);
        return (selector, ctx) -> selector.select(jwkSet);
    }

    // Authorization Server) Issuer/엔드포인트 설정
    @Bean
    AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer("http://localhost:8080") // 외부에서 접근 가능한 정확한 issuer
                .build();
    }

    // Resource Server) 동일 앱에서 RS가 사용할 JwtDecoder — JWKSource 공유
    @Bean
    JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
