package com.study.jwttutorial.controller;

import com.study.jwttutorial.dto.MemberDto;
import com.study.jwttutorial.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("hello");
    }

    @PostMapping("/test-redirect")
    public void testRedirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/api/user");
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<MemberDto> signup(
            @Valid @RequestBody MemberDto userDto
    ) {
        return ResponseEntity.ok(memberService.signup(userDto));
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER','ADMIN')") // USER,ADMIN 두가지 권한 모두 허용
    public ResponseEntity<MemberDto> getMyUserInfo(HttpServletRequest request) {
        return ResponseEntity.ok(memberService.getMyUserWithAuthorities());
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasAnyRole('ADMIN')") //ADMIN 권한만 호출할수 있도록
    public ResponseEntity<MemberDto> getUserInfo(@PathVariable String username) {
        return ResponseEntity.ok(memberService.getUserWithAuthorities(username));
    }
}
