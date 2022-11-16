package sejong.smartnotice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import sejong.smartnotice.helper.filter.CustomAuthenticationFilter;
import sejong.smartnotice.helper.handler.*;
import sejong.smartnotice.service.UserAccountService;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig extends AbstractHttpConfigurer<SecurityConfig, HttpSecurity> {

    private final UserAccountService userAccountService;

    public static SecurityConfig securityConfig(UserAccountService userAccountService) {
        return new SecurityConfig(userAccountService);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        http.addFilterBefore(customAuthenticationFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/resources/**", "/test*", "/alerts/api", "/profile", "/api/**", "/login", "/register/**").permitAll()
                .antMatchers("/u/**").hasRole("USER")
                .antMatchers("/s/**").hasRole("SUPPORTER")
                .anyRequest().hasRole("ADMIN");

        http.formLogin()
                .loginPage("/login");

        http.logout()
                .invalidateHttpSession(true)
                .deleteCookies("JSESSION_ID")
                .logoutSuccessHandler(logoutSuccessHandler());

        http.exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler());

        http.apply(securityConfig(userAccountService));

        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userAccountService);
        builder.authenticationProvider(authProvider());
        return builder.build();
    }

    @Bean
    public AuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setHideUserNotFoundExceptions(false); // * UserNameException 유지하도록 설정 *
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userAccountService);
        return provider;
    }

    public UsernamePasswordAuthenticationFilter customAuthenticationFilter(AuthenticationManager authenticationManager) {
        CustomAuthenticationFilter filter = new CustomAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager);
        filter.setAuthenticationSuccessHandler(loginSuccessHandler());
        filter.setAuthenticationFailureHandler(loginFailureHandler());
        return filter;
    }

    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }

    @Bean
    public AuthenticationFailureHandler loginFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new CustomLogoutSuccessHandler();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new WebAccessDeniedHandler();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
