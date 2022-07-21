package com.study.jwttutorial.repository;

import com.study.jwttutorial.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> { //spirng Data JPA

    //쿼리가 수행될때 Lazy조회가 아니고 Eager조회로 authorities정보를 같이 가져옴
    @EntityGraph(attributePaths = "authorities")
    Optional<Member> findOneWithAuthoritiesByMemberName(String memberName); // Member 정보를 가져올때 권한정보도 같이 가져오기
}
