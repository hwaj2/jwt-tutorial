package com.study.jwttutorial.service;

import com.study.jwttutorial.entity.Member;
import com.study.jwttutorial.repository.MemberRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // 로그인시에 DB에서 유저정보와 권한정보를 가져옴
    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String username) {
        return memberRepository.findOneWithAuthoritiesByMemberName(username)
                .map(member -> createUser(username, member))
                .orElseThrow(() -> new UsernameNotFoundException(username + " -> 데이터베이스에서 찾을 수 없습니다."));
    }


    // 해당정보를 기반으로 Member객체를 생성해서 리턴(userDetails)
    private org.springframework.security.core.userdetails.User createUser(String username, Member member) {
        if (!member.isActivated()) {
            throw new RuntimeException(username + " -> 활성화되어 있지 않습니다.");
        }

        List<GrantedAuthority> grantedAuthorities = member.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(member.getMemberName(),
                member.getPassword(),
                grantedAuthorities);
    }
}