package sejong.smartnotice.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import sejong.smartnotice.domain.Region;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.member.Account;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.dto.UserModifyDTO;
import sejong.smartnotice.dto.UserRegisterDTO;
import sejong.smartnotice.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TownService townService;

    @InjectMocks
    private UserService userService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void 주민_회원가입() {
        // given
        Town town = createTown("마을");
        User user = createUser("주민", "010-1234-1234", "주소", "2022-05-06", town, "id", "pw");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(townService.findById(any())).thenReturn(town);

        UserRegisterDTO registerDTO = UserRegisterDTO.builder()
                .name("주민")
                .tel("010-1234-1234")
                .address("주소")
                .birth("2022-05-06")
                .townId(town.getId())
                .loginId("id")
                .loginPw("pw").build();

        // when
        Long userId = userService.register(registerDTO);

        // then
        User findUser = userRepository.findById(userId).get();

        assertThat(findUser.getTel()).isEqualTo(registerDTO.getTel());
        assertThat(passwordEncoder.matches(registerDTO.getLoginPw(), findUser.getPassword())).isTrue();
    }

    @Test
    void 주민_회원가입_실패_중복() {
        // given
        UserRegisterDTO registerDTO = new UserRegisterDTO();
        when(userRepository.existsByTelOrAccountLoginId(any(), any())).thenReturn(true);

        // then
        assertThrows(IllegalStateException.class, () -> {
            // when
            userService.register(registerDTO);
        });
    }

    @Test
    void 주민_회원가입_실패_마을없음() {
        // given
        UserRegisterDTO registerDTO = new UserRegisterDTO();
        when(townService.findById(any())).thenThrow(new NullPointerException("마을이 존재하지 않습니다"));

        // then
        assertThrows(NullPointerException.class, () -> {
            // when
            userService.register(registerDTO);
        });
    }

    @Test
    void 주민_정보수정() {
        // given
        Town town = createTown("마을");
        User user = createUser("수정된 주민", "010-1234-1234", "수정된 주소", "2022-05-06", town, "id", "pw");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        UserModifyDTO modifyDTO = UserModifyDTO.builder()
                .name("수정된 주민")
                .tel("010-1234-1234")
                .address("수정된 주소")
                .info("새 정보").build();

        // when
        Long userId = userService.modifyUserInfo(modifyDTO);

        // then
        User findUser = userRepository.findById(userId).get();
        assertThat(findUser.getTel()).isEqualTo(modifyDTO.getTel());
    }

    @Test
    void 주민_정보수정_실패_중복() {
        // given
        User user = User.builder()
                .id(1L)
                .tel("010-0000-0000").build();
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        User duplicateUser = User.builder()
                .id(3L)
                .tel("010-1234-1234").build();
        when(userRepository.findByTel(any())).thenReturn(duplicateUser);

        UserModifyDTO modifyDTO = UserModifyDTO.builder()
                .id(1L)
                .tel("010-1234-1234").build();

        // then
        assertThrows(IllegalStateException.class, () -> {
            // when
            userService.modifyUserInfo(modifyDTO);
        });
    }

    @Test
    void 주민_삭제() {
        // given
        Town town = createTown("마을");
        User user = createUser("수정된 주민", "010-1234-1234", "수정된 주소", "2022-05-06", town, "id", "pw");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        when(userRepository.existsByTelOrAccountLoginId(any(), any()))
                .thenThrow(new NullPointerException("주민이 존재하지 않습니다"));

        // when
        userService.delete(1L);

        // then
        assertThrows(NullPointerException.class, () -> {
           userRepository.existsByTelOrAccountLoginId("010-1234-1234", "id");
        });
    }

    @Test
    void 주민_삭제_실패() {
        // given
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // then
        assertThrows(NullPointerException.class, () -> {
            // when
            userService.delete(1L);
        });
    }

    private Town createTown(String name) {
        Region region = new Region(1L, "시/도", "시/군/구");
        return Town.createTown(name, region);
    }

    private User createUser(String name, String tel, String address, String birth, Town town, String id, String pw) {
        Account account = Account.createAccount(id, pw, passwordEncoder);
        return User.createUser(name, tel, address, birth, town, account);
    }
}