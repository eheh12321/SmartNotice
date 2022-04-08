package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.Admin_Town;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.AdminType;
import sejong.smartnotice.repository.AdminRepository;

import javax.persistence.EntityManager;
import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final EntityManager em;

    // 회원가입
    public Long register(String name, String tel, String id, String pw, AdminType type) {
        log.info("== (서비스) 관리자 등록 ==");
        log.info("Before Encode: {}", pw);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(pw); // 비밀번호 암호화
        Admin admin = Admin.createAdmin(name, tel, id, encodedPassword, type);
        adminRepository.save(admin);
        return admin.getId();
    }

    // 관리자 정보 수정
    public Long modifyAdminInfo(Long id, String name, String tel) {
        log.info("== (서비스) 관리자 정보 수정 ==");
        Admin admin = findById(id);
        admin.modifyAdminInfo(name, tel);
        return admin.getId();
    }

    // 관리자 삭제
    public void delete(Long id) {
        log.info("== (서비스) 관리자 삭제 ==");
        Admin admin = findById(id);
        adminRepository.delete(admin);
    }

    // 관리자 검색
    public Admin findById(Long id) {
        log.info("== (서비스) 관리자 아이디 조회 ==");
        Optional<Admin> opt = adminRepository.findById(id);
        if (opt.isEmpty()) {
            log.warn("관리자가 존재하지 않습니다");
            throw new NullPointerException("관리자가 존재하지 않습니다.");
        }
        return opt.get();
    }

    // 관리자 전체 목록 조회
    public List<Admin> findAll() {
        log.info("== (서비스) 관리자 전체 목록 조회 ==");
        return adminRepository.findAll();
    }

    // 관리자 이름 검색
    public List<Admin> findByName(String name) {
        log.info("== (서비스) 관리자 이름 조회 ==");
        return adminRepository.findByNameContaining(name);
    }

    // 마을 관리 관리자 목록 조회
    public List<Admin> findByTown(Long townId) {
        log.info("== (서비스) 관리자 마을 조회 ==");
        Town town = em.find(Town.class, townId);
        List<Admin_Town> atList = em.createQuery("select at from Admin_Town at where at.town=:town", Admin_Town.class)
                .setParameter("town", town)
                .getResultList();

        List<Admin> adminList = new ArrayList<>();
        for (Admin_Town at : atList) {
            adminList.add(at.getAdmin());
        }
        return adminList;
    }

    public Admin findByLoginId(String loginId) {
        log.info("== (서비스) 관리자 로그인 아이디 조회 ==");
        return adminRepository.findByLoginId(loginId);
    }

    public Admin findByTel(String tel) {
        log.info("== (서비스) 관리자 전화번호 조회 ==");
        return adminRepository.findByTel(tel);
    }

    //// 스프링 시큐리티

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = findByLoginId(username);
        if(admin == null) throw new UsernameNotFoundException("등록되지 않은 사용자입니다");
        return admin;
    }
}
