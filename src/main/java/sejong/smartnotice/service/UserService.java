package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.Announce;
import sejong.smartnotice.repository.UserRepository;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 방송 다시듣기 (단말기에서 요청 -> 방송 검색 -> 방송 리턴)
    public Announce 방송다시듣기() {
        return null;
    }
}
