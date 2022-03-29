package sejong.smartnotice.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.AdminRole;
import sejong.smartnotice.dto.AdminDTO;
import sejong.smartnotice.dto.LoginDTO;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AdminServiceTest {

    @Autowired AdminService adminService;
    @Autowired TownService townService;

    @Test
    @Rollback(false)
    void 관리자마을연결() {
        // given
        Admin admin = createAdmin();
        List<Long> townIdList = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            Long tid = townService.registerTown("마을", 1L);
            townIdList.add(tid);
        }
        // when
        adminService.setManageTown(admin, townIdList);

        // then
        Assertions.assertThat(admin.getTownList().size()).isEqualTo(5);
    }

    private Admin createAdmin() {
        AdminDTO adminDTO = new AdminDTO("관리자", "010-1234-1234", AdminRole.ADMIN);
        LoginDTO loginDTO = new LoginDTO("아이디", "비밀번호");
        Long adminId = adminService.register(adminDTO, loginDTO);

        return adminService.findById(adminId);
    }
}