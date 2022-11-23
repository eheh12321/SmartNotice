package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.member.Account;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.helper.dto.request.AdminRequest.AdminModifyRequest;
import sejong.smartnotice.helper.dto.request.AdminRequest.AdminRegisterRequest;
import sejong.smartnotice.helper.dto.response.AdminResponse;
import sejong.smartnotice.repository.AdminRepository;
import sejong.smartnotice.repository.TownRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final EntityManager em;
    private final AdminRepository adminRepository;
    private final TownRepository townRepository;
    private final TownDataService townDataService;
    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    // 회원가입
    public AdminResponse register(AdminRegisterRequest registerDTO) {
        // 중복 검증
        validateDuplicateAdmin(registerDTO.getTel(), registerDTO.getLoginId());

        // 비밀번호 암호화
        Account account = Account.createAccount(registerDTO.getLoginId(), registerDTO.getLoginPw(), PASSWORD_ENCODER);

        // 계정 생성 및 저장
        Admin admin = Admin.createAdmin(registerDTO.getName(), registerDTO.getTel(), account, registerDTO.getType());
        Admin savedAdmin = adminRepository.save(admin);

        return AdminResponse.from(savedAdmin);
    }

    // 관리자 정보 수정
    public AdminResponse modifyAdminInfo(Long adminId, AdminModifyRequest modifyDTO) {
        // 1. 관리자 조회
        Admin admin = findById(adminId);

        // 2. 정보 수정
        // 쿼리를 바로 날리는게 아니고, DTO를 통해 변경되는 값이 있는 경우에만 select + update 쿼리를 발생시킨다
        // = DTO 변경 사항이 없으면 쿼리 자체를 내보내지 않는다
        admin.modifyAdminInfo(modifyDTO.getName(), modifyDTO.getTel());

        // 3. 마을 대표 관리자인 경우 Redis Update
        townRepository.findByRepresentativeAdminId(admin.getId()).forEach(
                town -> townDataService.action(townData -> {
                    townData.setMainAdminName(admin.getName());
                    townData.setMainAdminTel(admin.getTel());
                    return townData;
                }, town.getId())
        );
        return AdminResponse.from(admin);
    }

    // 관리자 삭제
    public void delete(Long id) {
        Admin admin = findById(id);
        adminRepository.delete(admin);
    }

    // 관리자 검색
    public Admin findById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("관리자가 존재하지 않습니다"));
    }

    /**
     * Transactional readOnly 설정 시 이점 (읽기 전용 모드)
     * Flush Mode를 MANUAL로 설정, 트랜잭션 커밋 시에도 flush 되지 않음 (강제 flush는 가능)
     * -> C/U/D 작업 X. 변경감지(Dirty Checking) X, 스냅샷 X -> 성능 향상
     */
    @Transactional(readOnly = true)
    public List<AdminResponse> findAll() {
        return adminRepository.findAll().stream()
                .map(AdminResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Admin> findAllWithTown() {
        return adminRepository.findAllWithTown();
    }

    @Transactional(readOnly = true)
    public List<Admin> findNotTownAdmin(Town town) {
        return em.createQuery("select a from Admin a where a.id not in(select at.admin.id from TownAdmin at where at.town.id=?1)", Admin.class)
                .setParameter(1, town.getId()).getResultList();
    }

    // 관리자 이름 검색
    @Transactional(readOnly = true)
    public List<Admin> findByName(String name) {
        return adminRepository.findByNameContaining(name);
    }

    @Transactional(readOnly = true)
    public Admin findByLoginId(String loginId) {
        return adminRepository.findByAccount_LoginId(loginId)
                .orElseThrow(() -> new EntityNotFoundException("관리자가 존재하지 않습니다"));
    }

    @Transactional(readOnly = true)
    public List<AdminResponse> findAdminByTown(Long townId) {
        return adminRepository.findAdminByTown(townId).stream()
                .map(AdminResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Admin findByTel(String tel) {
        return adminRepository.findByTel(tel)
                .orElseThrow(() -> new EntityNotFoundException("관리자가 존재하지 않습니다"));
    }

    // 관리자 중복 검증
    private void validateDuplicateAdmin(String tel, String loginId) {
        if (adminRepository.existsAdminByTelOrAccountLoginId(tel, loginId)) {
            log.warn("이미 존재하는 회원입니다");
            throw new IllegalStateException("이미 존재하는 회원입니다");
        }
    }
}
