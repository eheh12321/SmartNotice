package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.member.UserAccount;
import sejong.smartnotice.repository.UserAccountRepository;


@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserAccountService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    public UserAccount findAccountByLoginId(String loginId) {
        return userAccountRepository.findByAccount_LoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("회원이 존재하지 않습니다."));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Login 요청 -> {}", username);
        String[] splits = username.split("/");
        String loginId = splits[0];
        String domain = splits[1];

        UserAccount userAccount = findAccountByLoginId(loginId);
        switch (domain) {
            case "admin":
                if(!userAccount.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                    throw new UsernameNotFoundException("권한이 존재하지 않습니다.");
                }
                break;
            case "user":
                if(!userAccount.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))) {
                    throw new UsernameNotFoundException("권한이 존재하지 않습니다.");
                }
                break;
            case "supporter":
                if(!userAccount.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SUPPORTER"))) {
                    throw new UsernameNotFoundException("권한이 존재하지 않습니다.");
                }
                break;
        }
        return userAccount;
    }
}
