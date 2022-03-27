package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.Admin_Town;
import sejong.smartnotice.domain.Announce;
import sejong.smartnotice.domain.Announce_Town;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AnnounceService {

    private final AdminService adminService;
    private final EntityManager em;

    public void 음성방송() {

    }

    public void 문자방송() {

    }

    public Announce 방송조회() {
        return null;
    }

    public List<Announce> 방송목록조회() {
        return null;
    }

    // 방송 잘 되는지 테스트용
    public void 방송테스트(Long adminId, String title, String type) {
        Admin admin = adminService.findById(adminId);
        Announce announce = Announce.builder()
                .admin(admin)
                .title(title)
                .type(type)
                .time(LocalDateTime.now())
                .store("./저장소").build();
        em.persist(announce);

        List<Admin_Town> townList = admin.getTownList();
        List<Announce_Town> atList = new ArrayList<>(); // 관리자가 어느 마을에 방송할지 선택함
        for (Admin_Town adminTown : townList) {
            Announce_Town at = Announce_Town.builder()
                    .town(adminTown.getTown())
                    .announce(announce).build();
            em.persist(at);
            atList.add(at);

            //////////////////////// 회원에게 방송 알림 쏘기 (나중에 따로 메소드로 빼기)
            Town town = at.getTown();
            List<User> userList = em.createQuery("select u from User u where u.town=:town", User.class)
                    .setParameter("town", town).getResultList();
            for (User user : userList) {
                user.receiveAnnounce(announce);
            }
            ///////////////////////
        }
    }
}
