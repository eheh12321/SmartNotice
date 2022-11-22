package sejong.smartnotice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import sejong.smartnotice.domain.member.Account;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.AdminType;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/resources/**", "/alerts/api", "/profile", "/login", "/register/**", "/test/**").permitAll()
                .antMatchers("/storage/**", "/api/**").authenticated() // 아무 권한이나 있으면
                .antMatchers("/u/**").hasRole("USER")
                .antMatchers("/s/**").hasRole("SUPPORTER")
                .anyRequest().hasRole("ADMIN");
        return http.build();
    }

    @Bean
    public UserDetailsService testUserDetailsService() {
        return username -> Admin.createAdmin(
                "테스트_최고관리자",
                "010-0000-0000",
                new Account(username, "loginPw"),
                AdminType.SUPER
        );
    }
}
