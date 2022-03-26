package sejong.smartnotice.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.Admin_Town;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.AdminRole;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AdminServiceTest {

    @Autowired EntityManager em;
    @Autowired AdminService adminService;

    @Test
    @Rollback(false)
    void 관리자다중마을연결및방송() {

        Town town1 = Town.builder()
                .name("마을1").build();
        Town town2 = Town.builder()
                .name("마을2").build();
        em.persist(town1);
        em.persist(town2);

        Admin admin = Admin.builder()
                .loginId("아이디")
                .loginPw("비밀번호")
                .name("이름")
                .tel("전화번호")
                .type(AdminRole.ADMIN).build();
        em.persist(admin);

        em.flush();
        em.clear();
        System.out.println("----------------------------------------");

        Admin findAdmin = em.find(Admin.class, 1L);
        Town findTown1 = em.find(Town.class, 1L);
        Town findTown2 = em.find(Town.class, 2L);

        Admin_Town at1 = Admin_Town.builder()
                .admin(findAdmin)
                .town(findTown1).build();

        Admin_Town at2 = Admin_Town.builder()
                .admin(findAdmin)
                .town(findTown2).build();

        em.persist(at1);
        em.persist(at2);

        adminService.makeAnnounce(findAdmin, "제목", "일반"); // 관리자 방송하기!!!
    }
}