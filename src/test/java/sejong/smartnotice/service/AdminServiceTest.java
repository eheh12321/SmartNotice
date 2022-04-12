package sejong.smartnotice.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import sejong.smartnotice.domain.member.Account;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.AdminType;
import sejong.smartnotice.dto.AdminModifyDTO;
import sejong.smartnotice.dto.AdminRegisterDTO;
import sejong.smartnotice.repository.AdminRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock // 가짜 객체
    private AdminRepository adminRepository;

    @InjectMocks // Mock 객체가 주입될 클래스
    private AdminService adminService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void 관리자_회원가입() {
        // given
        Admin admin = createAdmin("관리자", "010-1234-1234", "id", "pw");
        when(adminRepository.findById(any())).thenReturn(Optional.of(admin));

        AdminRegisterDTO registerDTO = AdminRegisterDTO.builder()
                .name("관리자")
                .tel("010-1234-1234")
                .loginId("id")
                .loginPw("pw")
                .type(AdminType.ADMIN).build();

        // when
        Long adminId = adminService.register(registerDTO);

        // then
        Admin findAdmin = adminRepository.findById(adminId).get(); // Optional 객체 바로 꺼냄

        assertThat(findAdmin.getTel()).isEqualTo(registerDTO.getTel()); // 회원 등록이 제대로 되는지?
        assertThat(passwordEncoder.matches(registerDTO.getLoginPw(), findAdmin.getPassword())).isTrue(); // 비밀번호 암호화가 제대로 되는지?
    }

    @Test
    void 관리자_회원가입_실패_중복() {
        // given
        when(adminRepository.existsAdminByTelOrAccountLoginId(any(), any())).thenReturn(true);
        AdminRegisterDTO registerDTO = new AdminRegisterDTO();

        // then
        Assertions.assertThrows(IllegalStateException.class, () -> {
            // when
            adminService.register(registerDTO);
        });
    }

    @Test
    void 관리자_정보수정() {
        // given
        Admin admin = createAdmin("수정된 관리자", "010-9999-9999", "id", "pw");
        when(adminRepository.findById(any())).thenReturn(Optional.of(admin));

        AdminModifyDTO modifyDTO = AdminModifyDTO.builder()
                .id(1L)
                .name("수정된 관리자")
                .tel("010-9999-9999")
                .type(AdminType.ADMIN).build();

        // when
        Long adminId = adminService.modifyAdminInfo(modifyDTO);

        // then
        Admin findAdmin = adminRepository.findById(adminId).get();
        assertThat(findAdmin.getTel()).isEqualTo(modifyDTO.getTel());
    }

    @Test
    void 관리자_정보수정_실패_중복() {
        // given
        Admin admin = createAdmin("관리자", "010-1234-1234", "id", "pw");
        when(adminRepository.findByTel(any())).thenReturn(admin);

        AdminModifyDTO modifyDTO = AdminModifyDTO.builder()
                .id(1L)
                .name("수정된 관리자")
                .tel("010-1234-1234")
                .type(AdminType.ADMIN).build();

        // then
        Assertions.assertThrows(IllegalStateException.class, () -> {
            // when
            adminService.modifyAdminInfo(modifyDTO);
        });
    }

    @Test
    void 관리자_삭제() {
        // given
        Admin admin = createAdmin("관리자", "010-1234-1234", "id", "pw");
        when(adminRepository.findById(any())).thenReturn(Optional.of(admin));

        when(adminRepository.existsAdminByTelOrAccountLoginId(any(), any())).thenThrow(new NullPointerException("관리자가 존재하지 않습니다"));

        // when
        adminService.delete(1L);

        // then
        Assertions.assertThrows(NullPointerException.class, () -> {
            adminRepository.existsAdminByTelOrAccountLoginId("010-1234-1234", "id");
        });
    }

    @Test
    void 관리자_삭제_실패() {
        // given
        when(adminRepository.findById(any())).thenReturn(Optional.empty());

        // then
        Assertions.assertThrows(NullPointerException.class, () -> {
            // when
            adminService.delete(1L);
        });
    }

    private Admin createAdmin(String name, String tel, String id, String pw) {
        Account account = Account.createAccount(id, pw, passwordEncoder);
        return Admin.createAdmin(name, tel, account, AdminType.ADMIN);
    }
}