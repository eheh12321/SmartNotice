package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.*;
import sejong.smartnotice.domain.device.Device;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.repository.AdminRepository;
import sejong.smartnotice.repository.UserRepository;

import javax.persistence.EntityManager;
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

    // 관리자(Root)가 마을을 추가할 수 있어야됨 (ROOT 권한)
    public void addTown(String name) {
        Town town = Town.builder()
                .name(name).build();
        em.persist(town);
    }

    public void 마을정보수정() { }
    public void 회원정보수정() { }
    public void 관리자등록() { }
    public void 관리자정보수정() { }
    public void 단말기점검() { }

    // 사용자 마을 등록 (ADMIN 권한)
    public void addUser(User user) {
        log.info("== 사용자 등록 ==");

        Device device = Device.builder().build(); // 유저랑 연결될 단말기 생성
        user.changeDevice(device);

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
