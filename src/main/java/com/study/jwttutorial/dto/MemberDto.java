package com.study.jwttutorial.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.study.jwttutorial.entity.Member;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;
import java.util.stream.Collectors;
// 회원가입시 사용할 UserDto클래스
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {

    @NotNull
    @Size(min = 3, max = 50)
    private String memberName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    @Size(min = 3, max = 100)
    private String password;

    @NotNull
    @Size(min = 3, max = 50)
    private String nickName;

    private Set<AuthorityDto> authorityDtoSet;

    public static MemberDto from(Member member) {
        if(member == null) return null;

        return MemberDto.builder()
                .memberName(member.getMemberName())
                .nickName(member.getNickName())
                .authorityDtoSet(member.getAuthorities().stream()
                        .map(authority -> AuthorityDto.builder().authorityName(authority.getAuthorityName()).build())
                        .collect(Collectors.toSet()))
                .build();
    }
}