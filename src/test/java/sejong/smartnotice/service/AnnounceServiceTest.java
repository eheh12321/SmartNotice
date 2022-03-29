package sejong.smartnotice.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.announce.Announce;
import sejong.smartnotice.domain.announce.AnnounceCategory;
import sejong.smartnotice.domain.announce.AnnounceType;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.AdminRole;
import sejong.smartnotice.dto.AdminDTO;
import sejong.smartnotice.dto.LoginDTO;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AnnounceServiceTest {

    @Autowired AdminService adminService;
    @Autowired AnnounceService announceService;
    @Autowired TownService townService;

    @Test
    @Rollback(false)
    void 방송하기() {
        // given
        Admin admin = createAdmin();

        List<Long> tidList = new ArrayList<>();
        List<Town> townList = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            Long tid = townService.registerTown("마을" + i, 1L);
            Town town = townService.findTownById(tid); // 1차 캐시에서 바로 가져온다
            tidList.add(tid);
            townList.add(town);
        }
        admin.setManageTown(townList); // 관리자와 마을 연결

        // when
        System.out.println("==============================================");
        Long aId = announceService.makeAnnounce(admin.getId(), "테스트방송", AnnounceCategory.NORMAL, AnnounceType.TEXT, tidList);
        System.out.println("==============================================");

        // then
        Announce announce = announceService.findAnnounceById(aId);

    }

    private Admin createAdmin() {
        AdminDTO adminDTO = new AdminDTO("관리자", "010-1234-1234", AdminRole.ADMIN);
        LoginDTO loginDTO = new LoginDTO("아이디", "비밀번호");
        Long adminId = adminService.register(adminDTO, loginDTO);

        return adminService.findById(adminId);
    }
}