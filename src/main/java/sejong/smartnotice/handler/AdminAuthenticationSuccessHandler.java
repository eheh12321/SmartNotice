package sejong.smartnotice.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
public class AdminAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth) throws IOException {
        log.info("== 관리자 로그인 성공! ==");
        log.info("로그인 ID: {}", auth.getName());
        log.info("로그인 시각: {}", LocalDateTime.now());
        log.info("=========================");

        SavedRequest savedRequest = requestCache.getRequest(request, response);

        if(savedRequest != null) {
            log.info("이전 경로로 리다이렉트");
            String targetURL = savedRequest.getRedirectUrl();
            response.sendRedirect(targetURL);
        } else {
            response.sendRedirect("/");
        }
    }
}
