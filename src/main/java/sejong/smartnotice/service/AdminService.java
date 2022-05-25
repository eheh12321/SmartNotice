package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import sejong.smartnotice.dto.AdminModifyDTO;
import sejong.smartnotice.dto.AdminRegisterDTO;
import sejong.smartnotice.repository.AdminRepository;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminService implements UserDetailsService {

    private final EntityManager em;
    private final AdminRepository adminRepository;
    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    // 회원가입
    public Long register(AdminRegisterDTO registerDTO) {
        log.info("== 관리자 등록 ==");
        // 중복 검증
        validateDuplicateAdmin(registerDTO.getTel(), registerDTO.getLoginId());

        // 비밀번호 암호화
        Account account = Account.createAccount(registerDTO.getLoginId(), registerDTO.getLoginPw(), PASSWORD_ENCODER);

        // 계정 생성 및 저장
        Admin admin = Admin.createAdmin(registerDTO.getName(), registerDTO.getTel(), account, registerDTO.getType());
        adminRepository.save(admin);

        return admin.getId();
    }

    // 관리자 정보 수정
    public Long modifyAdminInfo(AdminModifyDTO modifyDTO) {
        log.info("== 관리자 정보 수정 ==");
        // 1. 관리자 조회
        Admin admin = findById(modifyDTO.getId());
        
        // 2. 전화번호 중복 검증 (전화번호가 이미 존재하고, 동일 인물이 아닌 경우)
        Admin findAdmin = findByTel(modifyDTO.getTel());
        if(findAdmin != null && !findAdmin.getId().equals(admin.getId())) {
            log.warn("중복된 전화번호가 존재합니다");
            throw new IllegalStateException("중복된 전화번호가 존재합니다");
        }
        
        // 3. 정보 수정
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
        // 아이디 검증 이후 반환
        return validateAdminId(id);
    }

    /**
     * Transactional readOnly 설정 시 이점 (읽기 전용 모드)
     * Flush Mode를 MANUAL로 설정, 트랜잭션 커밋 시에도 flush 되지 않음 (강제 flush는 가능)
     * -> C/U/D 작업 X. 변경감지(Dirty Checking) X, 스냅샷 X -> 성능 향상
     */
    @Transactional(readOnly = true)
    public List<Admin> findAll() {
        log.info("== 관리자 전체 목록 조회 ==");
        return adminRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Admin> findAllWithTown() {
        log.info("== 관리자 전체 목록 조회(fetch) ==");
        return adminRepository.findAllWithTown();
    }

    @Transactional(readOnly = true)
    public List<Admin> findNotTownAdmin(Town town) {
        log.info("== 해당 마을 관리자가 아닌 관리자 목록 조회 ==");
        List<Admin> adminList = em.createQuery("select a from Admin a where a.id not in(select at.admin.id from Admin_Town at where at.town.id=?1)", Admin.class)
                .setParameter(1, town.getId()).getResultList();
        return adminList;
    }

    // 관리자 이름 검색
    @Transactional(readOnly = true)
    public List<Admin> findByName(String name) {
        log.info("== 관리자 이름 조회 ==");
        return adminRepository.findByNameContaining(name);
    }

    @Transactional(readOnly = true)
    public Admin findByLoginId(String loginId) {
        log.info("== 관리자 로그인 아이디 조회 ==");
        return adminRepository.findByAccountLoginId(loginId);
    }

    @Transactional(readOnly = true)
    public List<Admin> findAdminByTown(Long townId) {
        log.info("== 마을 관리자 목록 조회(fetch) ==");
        return adminRepository.findAdminByTown(townId);
    }

    @Transactional(readOnly = true)
    public Admin findByTel(String tel) {
        log.info("== 관리자 전화번호 조회 ==");
        return adminRepository.findByTel(tel);
    }

    @Transactional(readOnly = true)
    public List<Town> getTownList(Admin admin) {
        log.info("== 관리자 관리 마을 목록 조회 ==");
        List<Admin_Town> atList = em.createQuery("select at from Admin_Town at where at.admin=:admin", Admin_Town.class)
                .setParameter("admin", admin).getResultList();
        List<Town> townList = new ArrayList<>();
        for (Admin_Town at : atList) {
            townList.add(at.getTown());
        }
        return townList;
    }

    private Admin validateAdminId(Long id) {
        log.info("== 관리자 아이디 검증 ==");
        Optional<Admin> opt = adminRepository.findById(id);
        if (opt.isEmpty()) {
            log.warn("관리자가 존재하지 않습니다");
            throw new NullPointerException("관리자가 존재하지 않습니다.");
        }
        return opt.get();
    }

    // 관리자 중복 검증
    private void validateDuplicateAdmin(String tel, String loginId) {
        log.info("== 관리자 중복 검증 ==");
        if (adminRepository.existsAdminByTelOrAccountLoginId(tel, loginId)) {
            log.warn("이미 존재하는 회원입니다");
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
