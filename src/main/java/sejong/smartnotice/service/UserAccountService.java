package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.member.UserAccount;
import sejong.smartnotice.repository.UserAccountRepository;

import javax.persistence.EntityNotFoundException;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserAccountService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    public UserAccount findAccountByLoginId(String loginId) {
        return userAccountRepository.findByAccount_LoginId(loginId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findAccountByLoginId(username);
    }
}
