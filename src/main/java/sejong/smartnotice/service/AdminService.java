package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.*;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.repository.AdminRepository;
import sejong.smartnotice.repository.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final EntityManager em;
    
    // 회원가입
    public void register(Admin admin) {
        log.info("== 관리자 회원가입 ==");
        adminRepository.save(admin);
    }

    // 관리자가 관리하는 마을 설정 (ROOT, ADMIN)
    public void setManageTown(Admin admin, List<Long> townIdList) {
        // 아예 새거로 갈아끼는 방식
        List<Admin_Town> adminTownList = new ArrayList<>();

        for (Long tId : townIdList) {
            Town findTown = em.find(Town.class, tId);

            Admin_Town at = Admin_Town.builder()
                    .admin(admin)
                    .town(findTown).build();
            adminTownList.add(at);
        }
        admin.setTownList(adminTownList);
    }

    // 방송하기 (ROOT, ADMIN)
    public void makeAnnounce(Admin admin, String title, String type) {
        // 방송정보 세팅 (DTO로 받아옴)
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

    // 관리자(Root)가 마을을 추가할 수 있어야됨 (ROOT 권한)
    public void addTown(String name) {
        Town town = Town.builder()
                .name(name).build();
        em.persist(town);
    }

    // 사용자 마을 등록 (ADMIN 권한)
    public void addUser(User user) {
        log.info("== 사용자 등록 ==");

        Device device = Device.builder().build(); // 유저랑 연결될 단말기 생성
        user.setDevice(device);

        userRepository.save(user);

    }

    // 관리자 검색
    public Admin findById(Long id) {
        Optional<Admin> opt = adminRepository.findById(id);
        if(opt.isEmpty()) {
            log.warn("관리자가 존재하지 않습니다");
            throw new RuntimeException("대충 에러 ㄱ");
        }
        return opt.get();
    }
    
    // 유저 검색
    public User findUserById(Long id) {
        Optional<User> opt = userRepository.findById(id);
        if(opt.isEmpty()) {
            log.warn("사용자가 존재하지 않습니다");
            throw new RuntimeException("에러");
        }
        return opt.get();
    }

    // 마을 검색
    public Town findTownById(Long id) {
        return em.find(Town.class, id);
    }
}
