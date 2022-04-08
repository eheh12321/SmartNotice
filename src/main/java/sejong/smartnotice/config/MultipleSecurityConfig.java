package sejong.smartnotice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import sejong.smartnotice.handler.AdminAuthenticationSuccessHandler;
import sejong.smartnotice.handler.UserAuthenticationSuccessHandler;
import sejong.smartnotice.service.AdminService;
import sejong.smartnotice.service.UserService;

@EnableWebSecurity
public class MultipleSecurityConfig {

    @Order(1)
    @Configuration
    @RequiredArgsConstructor
    static class UserSecurityConfig extends WebSecurityConfigurerAdapter {

        private final UserService userService;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable().requestMatcher(new AntPathRequestMatcher("/u/**"))
                    .authorizeRequests()
                    .antMatchers("/u/login", "/u/register").permitAll()
                    .anyRequest().authenticated();

            http.formLogin()
                    .loginPage("/u/login")
                    .loginProcessingUrl("/u/login")
                    .successHandler(UserLoginSuccessHandler());

            http.logout()
                    .logoutUrl("/u/logout")
                    .logoutSuccessUrl("/u/login?logout")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSION_ID");

        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userService);
        }

        @Bean
        public AuthenticationSuccessHandler UserLoginSuccessHandler() {
            return new UserAuthenticationSuccessHandler();
        }
    }

    @Order(2)
    @Configuration
    @RequiredArgsConstructor
    static class AdminSecurityConfig extends WebSecurityConfigurerAdapter {

        private final AdminService adminService;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable().requestMatcher(new AntPathRequestMatcher("/**"))
                    .authorizeRequests()
                    .antMatchers("/login", "/register/**").permitAll()
                    .antMatchers("/user/**").permitAll()
                    .anyRequest().authenticated();

            http.formLogin()
                    .loginPage("/login")
                    .loginProcessingUrl("/login")
                    .successHandler(AdminLoginSuccessHandler());

            http.logout()
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSION_ID");
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(adminService);
        }

        @Bean
        public AuthenticationSuccessHandler AdminLoginSuccessHandler() {
            return new AdminAuthenticationSuccessHandler();
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
