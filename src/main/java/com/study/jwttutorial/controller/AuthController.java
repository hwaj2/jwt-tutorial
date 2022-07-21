package com.study.jwttutorial.controller;


import com.study.jwttutorial.dto.LoginDto;
import com.study.jwttutorial.dto.TokenDto;
import com.study.jwttutorial.jwt.JwtFilter;
import com.study.jwttutorial.jwt.TokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public AuthController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }


    @PostMapping("/authenticate")
    public ResponseEntity<TokenDto> authorize(@Valid @RequestBody LoginDto loginDto) {
        // memberName, password를 파라미터로 받아 인증토큰을 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        // 인증토큰을 이용해서 Authentication객체를 생성
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken); //authenticate()가 실행될때 loadUserByname메소드가 실행
        SecurityContextHolder.getContext().setAuthentication(authentication);   //생성된 인증객체를 securityContext에 저장

        // Authentication객체를 통해서 JWT토큰을 생성
        String jwt = tokenProvider.createToken(authentication);

        // http헤더에 jwt토큰을 넣어줌
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.OK);
    }
}