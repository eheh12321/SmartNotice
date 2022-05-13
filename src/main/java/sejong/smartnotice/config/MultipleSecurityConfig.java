package sejong.smartnotice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import sejong.smartnotice.handler.*;
import sejong.smartnotice.service.AdminService;

@EnableWebSecurity
public class MultipleSecurityConfig {

//    @Order(1)
//    @Configuration
//    @RequiredArgsConstructor
//    static class UserSecurityConfig extends WebSecurityConfigurerAdapter {
//
//        private final UserService userService;
//
//        @Override
//        protected void configure(HttpSecurity http) throws Exception {
//            http.csrf().disable().requestMatcher(new AntPathRequestMatcher("/u/**"))
//                    .authorizeRequests()
//                    .antMatchers("/u/login", "/u/register").permitAll()
//                    .anyRequest().hasRole("USER");
//
//            http.formLogin()
//                    .loginPage("/u/login")
//                    .loginProcessingUrl("/u/login")
//                    .successHandler(UserLoginSuccessHandler());
//
//            http.logout()
//                    .logoutUrl("/u/logout")
//                    .logoutSuccessUrl("/u/login?logout")
//                    .invalidateHttpSession(true)
//                    .deleteCookies("JSESSION_ID");
//
//        }
//
//        @Override
//        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//            auth.userDetailsService(userService);
//        }
//
//        @Bean
//        public AuthenticationSuccessHandler UserLoginSuccessHandler() {
//            return new UserAuthenticationSuccessHandler();
//        }
//    }
//
//    @Order(2)
//    @Configuration
//    @RequiredArgsConstructor
//    static class SupporterSecurityConfig extends WebSecurityConfigurerAdapter {
//
//        private final SupporterService supporterService;
//
//        @Override
//        protected void configure(HttpSecurity http) throws Exception {
//            http.csrf().disable().requestMatcher(new AntPathRequestMatcher("/s/**"))
//                    .authorizeRequests()
//                    .antMatchers("/s/login", "/s/register").permitAll()
//                    .anyRequest().hasRole("SUPPORTER");
//
//            http.formLogin()
//                    .loginPage("/s/login")
//                    .loginProcessingUrl("/s/login")
//                    .successHandler(SupporterLoginSuccessHandler());
//
//            http.logout()
//                    .logoutUrl("/s/logout")
//                    .logoutSuccessUrl("/s/login?logout")
//                    .invalidateHttpSession(true)
//                    .deleteCookies("JSESSION_ID");
//        }
//
//        @Override
//        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//            auth.userDetailsService(supporterService);
//        }
//
//        @Bean
//        public AuthenticationSuccessHandler SupporterLoginSuccessHandler() {
//            return new SupporterAuthenticationSuccessHandler();
//        }
//    }

    @Order(1)
    @Configuration
    @RequiredArgsConstructor
    static class AdminSecurityConfig extends WebSecurityConfigurerAdapter {

        private final AdminService adminService;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable().requestMatcher(new AntPathRequestMatcher("/**"))
                    .authorizeRequests()
                    .antMatchers("/resources/**", "/test*", "/emergency/**").permitAll()
                    .antMatchers("/login", "/register/**").permitAll()
                    .antMatchers("/user/**").permitAll()
                    .anyRequest().hasRole("ADMIN");

            http.formLogin()
                    .loginPage("/login")
                    .loginProcessingUrl("/login")
                    .successHandler(AdminLoginSuccessHandler())
                    .failureHandler(AdminLoginFailureHandler());

            http.logout()
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSION_ID")
                    .logoutSuccessHandler(CustomLogoutSuccessHandler());

            http.exceptionHandling()
                    .accessDeniedHandler(accessDeniedHandler());
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(adminService);
        }

        @Bean
        public AuthenticationSuccessHandler AdminLoginSuccessHandler() {
            return new AdminAuthenticationSuccessHandler();
        }

        @Bean
        public AuthenticationFailureHandler AdminLoginFailureHandler() {
            return new AdminAuthenticationFailureHandler();
        }

        @Bean
        public LogoutSuccessHandler CustomLogoutSuccessHandler() {
            return new CustomLogoutSuccessHandler();
        }

        @Bean
        public AccessDeniedHandler accessDeniedHandler() {
            return new WebAccessDeniedHandler();
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
