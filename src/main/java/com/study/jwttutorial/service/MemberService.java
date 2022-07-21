package com.study.jwttutorial.service;

import com.study.jwttutorial.dto.MemberDto;
import com.study.jwttutorial.entity.Authority;
import com.study.jwttutorial.entity.Member;
import com.study.jwttutorial.exception.DuplicateMemberException;
import com.study.jwttutorial.repository.MemberRepository;
import com.study.jwttutorial.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     * @param memberDto
     * @return
     */
    @Transactional
    public MemberDto signup(MemberDto memberDto) {
        if (memberRepository.findOneWithAuthoritiesByMemberName(memberDto.getMemberName()).orElse(null) != null) {
            throw new DuplicateMemberException("이미 가입되어 있는 유저입니다.");
        }

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        // signup메소드를 통해 가입된 회원은 USER_ROLE을 가지고 있고
        // data.sql에서 자동생성되는 admin계정은 USER,ADMIN ROLE 권한을 2개를가지고 있음
        // 유저와 권한정보를 담아서 저장

        Member member = Member.builder()
                .memberName(memberDto.getMemberName())
                .password(passwordEncoder.encode(memberDto.getPassword()))
                .nickName(memberDto.getNickName())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        return memberDto.from(memberRepository.save(member));
    }



    // username을 받아서 객체를 권한정보를 가져오는것
    @Transactional(readOnly = true)
    public MemberDto getUserWithAuthorities(String username) {
        return MemberDto.from(memberRepository.findOneWithAuthoritiesByMemberName(username).orElse(null));
    }

    // SecurityContext에 저장된 username의 정보만 가져옴
    @Transactional(readOnly = true)
    public MemberDto getMyUserWithAuthorities() {
        return MemberDto.from(SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByMemberName).orElse(null));
    }



}
