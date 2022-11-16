package sejong.smartnotice.helper.filter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        UsernamePasswordAuthenticationToken authRequest = getAuthRequest(request);
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private UsernamePasswordAuthenticationToken getAuthRequest(HttpServletRequest request) {
        String username = obtainUsername(request);
        String password = obtainPassword(request);
        String domain = obtainDomain(request);

        String userNameDomain = String.format("%s/%s", username, domain);
        return new UsernamePasswordAuthenticationToken(userNameDomain, password);
    }

    private String obtainDomain(HttpServletRequest request) {
        return request.getParameter("domain");
    }
}
