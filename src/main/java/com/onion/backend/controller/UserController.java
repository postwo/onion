package com.onion.backend.controller;

import com.onion.backend.dto.SignUpUser;
import com.onion.backend.entity.User;
import com.onion.backend.jwt.JwtUtil;
import com.onion.backend.service.CustomUserDetailsService;
import com.onion.backend.service.JwtBlacklistService;
import com.onion.backend.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final JwtBlacklistService jwtBlacklistService;

    @GetMapping("")
    public ResponseEntity<List<User>> getUserS() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @PostMapping("/signUp")
    public ResponseEntity<User> createUser(@RequestBody SignUpUser signUpUser) {
        User user = userService.createUser(signUpUser);
        return ResponseEntity.ok(user);
    }


    //description = "ID of the user to be deleted" → API 문서에서 userId가 무엇을 의미하는지 설명 추가 ✔ required = true → 반드시 필요한 값임을 명시
    //description은 Swagger API 문서에서 사용자에게 API의 파라미터 정보
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to be deleted", required = true) @PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpServletResponse response) throws AuthenticationException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        String token = jwtUtil.generateToken(userDetails.getUsername());
        Cookie cookie = new Cookie("onion_token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);

        response.addCookie(cookie);
        return token;
    }

    // 간단하게 로그아웃 처리할때는 브라우저 안에있는 키값을 그냥 삭제 하면 된다
    // 하지만 요즘에 안드로이드 tv 같은 곳으로 쿠기가 없기 때문에 이렇게 처리하면 안된다
    // 해킹 당해서 모든 디바이스 로그아웃을 해야하는데 이방식은 로그아웃을 요청한 클라이언트만 로그아웃을 할 수 있다
    // 그러므로 블랙리스트 기능을 만들어야 한다
    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("onion_token", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 쿠키 삭제
        response.addCookie(cookie);
    }

    // 전체 로그아웃
    @PostMapping("/logout/all")
    public void logout(@RequestParam(required = false) String requestToken, @CookieValue(value = "onion_token", required = false) String cookieToken, HttpServletRequest request, HttpServletResponse response) {
        String token = null;

        String bearerToken = request.getHeader("Authorization");

        if (requestToken != null) {
            token = requestToken;
        } else if (cookieToken != null) {
            token = cookieToken;
        } else if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            token = bearerToken.substring(7);
        }

        Instant instant = new Date().toInstant();

        LocalDateTime expirationTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();

        String username = jwtUtil.getUsernameFromToken(token);

        jwtBlacklistService.blacklistToken(token, expirationTime, username);

        Cookie cookie = new Cookie("onion_token", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 쿠키 삭제
        response.addCookie(cookie);
    }


    @PostMapping("/token/validation")
    @ResponseStatus(HttpStatus.OK)
    public void jwtValidate(@RequestParam String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Token is not validation");
        }
    }
}
