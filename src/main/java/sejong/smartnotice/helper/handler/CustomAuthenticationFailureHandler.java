package sejong.smartnotice.helper.handler;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@NoArgsConstructor
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        log.warn("# 로그인 실패!");
        if(exception != null) {
            final FlashMap flashMap = new FlashMap();
            if(exception instanceof UsernameNotFoundException) {
                flashMap.put("error", exception.getMessage());
            } else if (exception instanceof BadCredentialsException) {
                flashMap.put("error", "아이디 혹은 비밀번호를 확인해주세요.");
            } else {
                flashMap.put("error", "알 수 없는 오류가 발생했습니다.");
            }
            final FlashMapManager flashMapManager = new SessionFlashMapManager();
            flashMapManager.saveOutputFlashMap(flashMap, request, response);
        }
        String domain = request.getParameter("domain");
        response.sendRedirect("/login?domain=" + domain);
    }
}
