package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.dto.AdminDTO;
import sejong.smartnotice.dto.LoginDTO;
import sejong.smartnotice.repository.AdminRepository;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    
    // 회원가입
    public Long register(AdminDTO adminDTO, LoginDTO loginDTO) {
        Admin admin = Admin.createAdmin(adminDTO.getName(), adminDTO.getTel(),
                loginDTO.getLoginId(), loginDTO.getLoginPw(), adminDTO.getType());
        adminRepository.save(admin);
        return admin.getId();
    }

    // 관리자 정보 수정
    public Long changeAdminInfo(Long id, AdminDTO adminDTO) {
        Admin admin = findById(id);
        admin.changeMemberInfo(adminDTO.getName(), adminDTO.getTel());
        return admin.getId();
    }

    // 관리자 삭제
    public void remove(Long id) {
        Admin admin = findById(id);
        adminRepository.delete(admin);
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

    // 관리자 전체 목록 조회
    public List<Admin> getAdminList() {
        return adminRepository.findAll();
    }
}
