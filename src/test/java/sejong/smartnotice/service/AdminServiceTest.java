package sejong.smartnotice.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.AdminType;

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
        Admin admin = createAdmin("테스트관리자", "테스트관리자전화번호", "테스트관리자아이디", "테스트관리자비밀번호");

        // when
        em.flush();
        em.clear();

        // then
        Admin findAdmin = adminService.findById(admin.getId());
        assertThat(findAdmin.getName()).isEqualTo("테스트관리자");
    }

    @Test
    void 관리자수정() {
        // given
        Admin admin = createAdmin("테스트관리자", "테스트관리자전화번호", "테스트관리자아이디", "테스트관리자비밀번호");

        // when
        adminService.modifyAdminInfo(admin.getId(), "수정된 관리자", "수정된 관리자전화번호");

        // then
        assertThat(admin.getName()).isEqualTo("수정된 관리자");
    }

    @Test
    void 관리자삭제() {
        // given
        Admin admin = createAdmin("테스트관리자", "테스트관리자전화번호", "테스트관리자아이디", "테스트관리자비밀번호");

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
        Long adminId = adminService.register(name, tel, id, pw, AdminType.ADMIN);
        return adminService.findById(adminId);
    }
}