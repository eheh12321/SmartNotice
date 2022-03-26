package sejong.smartnotice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.Admin_Town;
import sejong.smartnotice.domain.Announce;
import sejong.smartnotice.domain.Announce_Town;
import sejong.smartnotice.domain.member.Admin;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AdminService {

    @Autowired EntityManager em;

    public void makeAnnounce(Admin admin, String title, String type) {
        // 방송정보 세팅 (DTO로 받아옴)
        Announce announce = Announce.builder()
                .admin(admin)
                .title(title)
                .type(type)
                .time(LocalDateTime.now())
                .store("./저장소").build();

        List<Admin_Town> townList = admin.getTownList();
        List<Announce_Town> atList = new ArrayList<>(); // 관리자가 어느 마을에 방송할지 선택함
        for (Admin_Town adminTown : townList) {
            Announce_Town at = Announce_Town.builder()
                    .town(adminTown.getTown())
                    .announce(announce).build();
            em.persist(at);
            atList.add(at);
        }
        em.persist(announce);
    }
}
