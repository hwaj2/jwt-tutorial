package com.study.jwttutorial.dto;

import lombok.*;

//Token정보를 response응답(받아올때) 사용할 TokenDto
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenDto {

    private String token;
}