package sejong.smartnotice.helper.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth) throws IOException {
        String domain = request.getParameter("domain");
        log.info("로그인 성공 -> {} (domain: {})", auth.getName(), domain);

        SavedRequest savedRequest = requestCache.getRequest(request, response);

        if (savedRequest != null) {
            log.info("> 이전에 보고있던 페이지로 리다이렉트");
            response.sendRedirect(savedRequest.getRedirectUrl());
        } else {
            log.info("> 기본 페이지로 리다이렉트");
            Collection<? extends GrantedAuthority> authSet = auth.getAuthorities();
            if (authSet.contains(new SimpleGrantedAuthority("ROLE_SUPER")) && domain.equals("admin")) {
                response.sendRedirect("/");
            } else if (authSet.contains(new SimpleGrantedAuthority("ROLE_ADMIN")) && domain.equals("admin")) {
                response.sendRedirect("/index");
            } else if (authSet.contains(new SimpleGrantedAuthority("ROLE_USER")) && domain.equals("user")) {
                response.sendRedirect("/u");
            } else if (authSet.contains(new SimpleGrantedAuthority("ROLE_SUPPORTER")) && domain.equals("supporter")) {
                response.sendRedirect("/s");
            }
        }
    }
}
