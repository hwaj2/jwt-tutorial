package com.study.jwttutorial.entity;


import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="member")
@Entity
public class Member {
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(length = 50, unique = true)
    private String memberName;

    @Column(length = 100)
    private String password;

    @Column(name = "nickname",length = 50)
    private String nickName;

    @Column(name = "activated")
    private boolean activated; //활성화 여부

    @ManyToMany
    @JoinTable( //User객체와 권한 객체의 다대다 관계를 일대다,다대일 관계의 조인테이블로 정의 했다는 의미
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "member_id", referencedColumnName = "member_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<Authority> authorities; //권한에 대한 관계

}
