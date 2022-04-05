package sejong.smartnotice.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.AdminRole;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class AdminServiceTest {

    @Autowired private AdminService adminService;
    @Autowired private EntityManager em;

    @Test
    void 관리자등록() {
        // given
        Admin admin = createAdmin("관리자", "010-1111-1111", "아이디", "비밀번호");

        // when
        em.flush();
        em.clear();

        // then
        Admin findAdmin = adminService.findById(admin.getId());
        assertThat(findAdmin.getName()).isEqualTo("관리자");
    }

    @Test
    void 관리자수정() {
        // given
        Admin admin = createAdmin("관리자", "010-1212-1212", "id", "pw");

        // when
        adminService.modifyAdminInfo(admin.getId(), "수정된 관리자", "010-2121-2121");

        // then
        assertThat(admin.getName()).isEqualTo("수정된 관리자");
    }

    @Test
    void 관리자삭제() {
        // given
        Admin admin = createAdmin("관리자", "010-1212-1212", "id", "pw");

        // when
        adminService.delete(admin.getId());

        // then
        assertThrows(NullPointerException.class,
                () -> adminService.findById(admin.getId()));
    }

    @Test
    void 관리자중복검증() {
        // given

        // when

        // then
    }

    private Admin createAdmin(String name, String tel, String id, String pw) {
        Long adminId = adminService.register(name, tel, id, pw, AdminRole.ADMIN);
        return adminService.findById(adminId);
    }
}