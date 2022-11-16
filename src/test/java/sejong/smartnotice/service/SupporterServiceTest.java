//package sejong.smartnotice.service;
//
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import sejong.smartnotice.domain.member.Account;
//import sejong.smartnotice.domain.member.Supporter;
//import sejong.smartnotice.domain.member.User;
//import sejong.smartnotice.dto.SupporterModifyDTO;
//import sejong.smartnotice.dto.request.register.SupporterRegisterDTO;
//import sejong.smartnotice.repository.SupporterRepository;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class SupporterServiceTest {
//
//    @Mock
//    SupporterRepository supporterRepository;
//
//    @Mock
//    UserService userService;
//
//    @InjectMocks
//    SupporterService supporterService;
//
//    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//
//    @Test
//    void 보호자_회원가입() {
//        // given
//        User user = User.createUser("주민", "010-1212-1212", "주소", "2022-05-06", null, null);
//        when(userService.findById(any())).thenReturn(user);
//
//        Supporter supporter = createSupporter("보호자", "010-1234-1234", "id", "pw");
//        supporter.connectUser(user);
//        when(supporterRepository.findById(any())).thenReturn(Optional.of(supporter));
//
//        SupporterRegisterDTO registerDTO = SupporterRegisterDTO.builder()
//                .name("보호자")
//                .tel("010-1234-1234")
//                .loginId("id")
//                .loginPw("pw")
//                .userId(1L).build();
//
//        // when
//        Long supporterId = supporterService.register(registerDTO);
//
//        // then
//        Supporter findSupporter = supporterRepository.findById(supporterId).get();
//
//        assertThat(findSupporter.getTel()).isEqualTo(registerDTO.getTel()); // 가입이 잘 됐는지?
//        assertThat(passwordEncoder.matches(registerDTO.getLoginPw(), findSupporter.getPassword())).isTrue(); // 비밀번호 암호화 잘 됐는지?
//        assertThat(findSupporter.getUser()).isEqualTo(user); // 주민 연결은 잘 됐는지?
//    }
//
//    @Test
//    void 보호자_회원가입_실패_중복() {
//        // given
//        SupporterRegisterDTO registerDTO = SupporterRegisterDTO.builder()
//                .name("중복된 보호자").build();
//
//        when(supporterRepository.existsSupporterByAccountLoginIdOrTel(any(), any()))
//                .thenReturn(true);
//
//        // then
//        assertThrows(IllegalStateException.class, () -> {
//           // when
//           supporterService.register(registerDTO);
//        });
//    }
//
//    @Test
//    void 보호자_회원가입_실패_주민없음() {
//        // given
//        when(userService.findById(any())).thenThrow(new NullPointerException("마을 주민이 존재하지 않습니다"));
//        SupporterRegisterDTO registerDTO = SupporterRegisterDTO.builder()
//                .name("보호자")
//                .tel("010-1234-1234")
//                .loginId("id")
//                .loginPw("pw")
//                .userId(1L).build();
//
//        // then
//        assertThrows(NullPointerException.class, () -> {
//            // when
//            supporterService.register(registerDTO);
//        });
//    }
//
//    @Test
//    void 보호자_정보수정() {
//        // given
//        Supporter supporter = createSupporter("보호자", "010-1234-1234", "id", "pw");
//        when(supporterRepository.findById(any())).thenReturn(Optional.of(supporter));
//
//        SupporterModifyDTO modifyDTO = SupporterModifyDTO.builder()
//                .id(supporter.getId())
//                .name("수정된 보호자")
//                .tel("010-1111-1111").build();
//
//        // when
//        Long supporterId = supporterService.modifySupporterInfo(modifyDTO);
//
//        // then
//        Supporter findSupporter = supporterRepository.findById(supporterId).get();
//        assertThat(findSupporter.getTel()).isEqualTo(modifyDTO.getTel());
//    }
//
//    @Test
//    void 보호자_정보수정_실패_중복() {
//        // given
//        Supporter supporter = createSupporter("보호자", "중복된 전화번호", "id", "pw");
//        when(supporterRepository.findByTel(any())).thenReturn(supporter);
//
//        SupporterModifyDTO modifyDTO = SupporterModifyDTO.builder()
//                .name("수정될 보호자")
//                .tel("중복된 전화번호").build();
//
//        // then
//        assertThrows(IllegalStateException.class, () -> {
//            supporterService.modifySupporterInfo(modifyDTO);
//        });
//    }
//
//    @Test
//    void 보호자_삭제() {
//        // given
//        Supporter supporter = createSupporter("보호자", "010-1234-1234", "id", "pw");
//        when(supporterRepository.existsSupporterByAccountLoginIdOrTel(any(), any())).thenReturn(false);
//        when(supporterRepository.findById(any())).thenReturn(Optional.of(supporter));
//
//        // when
//        supporterService.delete(supporter.getId());
//
//        // then
//        assertThat(supporterRepository.existsSupporterByAccountLoginIdOrTel(
//                supporter.getAccount().getLoginId(), supporter.getTel())).isFalse();
//    }
//
//    @Test
//    void 보호자_삭제_실패() {
//        // given
//        when(supporterRepository.findById(any())).thenReturn(Optional.empty());
//
//        // then
//        assertThrows(NullPointerException.class, () -> {
//            // when
//            supporterService.delete(1L);
//        });
//    }
//
//    private Supporter createSupporter(String name, String tel, String id, String pw) {
//        Account account = Account.createAccount(id, pw, passwordEncoder);
//        return Supporter.createSupporter(name, tel, account);
//    }
//}