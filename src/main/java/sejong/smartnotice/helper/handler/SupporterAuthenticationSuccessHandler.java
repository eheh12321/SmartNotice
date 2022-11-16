package sejong.smartnotice.helper.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
public class SupporterAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth) throws IOException {
        log.info("== 보호자 로그인 성공! ==");
        log.info("로그인 ID: {}", auth.getName());
        log.info("로그인 시각: {}", LocalDateTime.now());
        log.info("=========================");

        response.sendRedirect("/s/");
    }
}
