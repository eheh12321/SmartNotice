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
import sejong.smartnotice.domain.member.Account;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.AdminType;
import sejong.smartnotice.dto.AdminModifyDTO;
import sejong.smartnotice.dto.AdminRegisterDTO;
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
    public Long register(AdminRegisterDTO registerDTO) {
        log.info("== 관리자 등록 ==");
        // 중복 검증
        validateDuplicateAdmin(registerDTO.getTel(), registerDTO.getLoginId());

        // 비밀번호 암호화
        Account account = Account.createAccount(registerDTO.getLoginId(), registerDTO.getLoginPw(), new BCryptPasswordEncoder());

        // 계정 생성 및 저장
        Admin admin = Admin.createAdmin(registerDTO.getName(), registerDTO.getTel(), account, registerDTO.getType());
        adminRepository.save(admin);

        return admin.getId();
    }

    // 관리자 정보 수정
    public Long modifyAdminInfo(AdminModifyDTO modifyDTO) {
        log.info("== 관리자 정보 수정 ==");
        if(findByTel(modifyDTO.getTel()) != null) { // 전화번호 중복 검증
            throw new IllegalStateException("중복된 전화번호가 존재합니다");
        }
        Admin admin = findById(modifyDTO.getId());
        admin.modifyAdminInfo(modifyDTO.getName(), modifyDTO.getTel());
        return admin.getId();
    }

    // 관리자 삭제
    public void delete(Long id) {
        log.info("== 관리자 삭제 ==");
        Admin admin = findById(id);
        adminRepository.delete(admin);
    }

    // 관리자 검색
    public Admin findById(Long id) {
        log.info("== 관리자 아이디 조회 ==");
        Optional<Admin> opt = adminRepository.findById(id);
        if (opt.isEmpty()) {
            log.warn("관리자가 존재하지 않습니다");
            throw new NullPointerException("관리자가 존재하지 않습니다.");
        }
        return opt.get();
    }

    // 관리자 전체 목록 조회

    /**
     * @Transactional readOnly 설정 시 이점 (읽기 전용 모드)
     * Flush Mode를 MANUAL로 설정, 트랜잭션 커밋 시에도 flush 되지 않음 (강제 flush는 가능)
     * -> C/U/D 작업 X. 변경감지(Dirty Checking) X, 스냅샷 X -> 성능 향상
     */
    @Transactional(readOnly = true)
    public List<Admin> findAll() {
        log.info("== 관리자 전체 목록 조회 ==");
        return adminRepository.findAll();
    }

    // 관리자 이름 검색
    @Transactional(readOnly = true)
    public List<Admin> findByName(String name) {
        log.info("== 관리자 이름 조회 ==");
        return adminRepository.findByNameContaining(name);
    }

    // 마을 관리 관리자 목록 조회
    @Transactional(readOnly = true)
    public List<Admin> findByTown(Long townId) {
        log.info("== 관리자 마을 조회 ==");
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

    @Transactional(readOnly = true)
    public Admin findByLoginId(String loginId) {
        log.info("== 관리자 로그인 아이디 조회 ==");
        return adminRepository.findByAccountLoginId(loginId);
    }

    @Transactional(readOnly = true)
    public Admin findByTel(String tel) {
        log.info("== 관리자 전화번호 조회 ==");
        return adminRepository.findByTel(tel);
    }

    // 관리자 중복 검증
    private void validateDuplicateAdmin(String tel, String loginId) {
        if (adminRepository.existsAdminByTelOrAccountLoginId(tel, loginId)) {
            throw new IllegalStateException("이미 존재하는 회원입니다");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = findByLoginId(username);
        if(admin == null) throw new UsernameNotFoundException("등록되지 않은 사용자입니다");
        return admin;
    }
}
