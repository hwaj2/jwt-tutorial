package com.study.jwttutorial.config;

import com.study.jwttutorial.jwt.JwtAccessDeniedHandler;
import com.study.jwttutorial.jwt.JwtAuthenticationEntryPoint;
import com.study.jwttutorial.jwt.JwtSecurityConfig;
import com.study.jwttutorial.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@EnableGlobalMethodSecurity(prePostEnabled = true) //@PreAuthorize 어노테이션을 메소드 단위로 추가하기 위해서 적용
@EnableWebSecurity //스프링 웹 보안 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenProvider tokenProvider;
    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    // 생성자 주입
    public SecurityConfig(
            TokenProvider tokenProvider,
            CorsFilter corsFilter,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ) {
        this.tokenProvider = tokenProvider;
        this.corsFilter = corsFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    // pw 인코딩
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                // token을 사용하는 방식이기 때문에 csrf를 disable,
                .csrf().disable()
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)

                //Exception을 핸들링 할때 우리가 만들었던 클래스들을 추가
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                // enable h2-console
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // 로그인 API, 회원가입API는 토큰이 없는 상태에서 요청이 들어오기 때문에 모두 permitAll 설정
                .and()
                .authorizeRequests()
                .antMatchers("/api/hello","/api/authenticate/**","/api/signup/**", "/api/test").permitAll()
                .anyRequest().authenticated()

                // JwtSecurityConfig 클래스 적용(JwtFilter를 addFilterBefore로 등록했던, jwtFilter + springSecurity config)
                .and()
                .apply(new JwtSecurityConfig(tokenProvider));


    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring() ///h2 console 하위 모든 요청들과 파비콘 관련 요청은 SpringSecurity 로직을 수행하지 않도록 추가
                .antMatchers("/h2-console/**"
                        , "/favicon.ico"
                        , "/error");
    }
}
