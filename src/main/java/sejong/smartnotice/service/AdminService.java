package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.*;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.AdminRole;
import sejong.smartnotice.dto.AdminDTO;
import sejong.smartnotice.dto.LoginDTO;
import sejong.smartnotice.repository.AdminRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final TownService townService;
    private final AdminRepository adminRepository;
    
    // 회원가입
    public void register(AdminDTO adminDTO, LoginDTO loginDTO) {
        log.info("== 관리자 회원가입 ==");
        Admin admin = Admin.createAdmin(adminDTO.getName(), adminDTO.getTel(),
                loginDTO.getLoginId(), loginDTO.getLoginPw(), AdminRole.ADMIN);
        adminRepository.save(admin);
    }

    // 관리자가 관리하는 마을 설정 (ROOT, ADMIN)
    public void setManageTown(Admin admin, List<Long> townIdList) {
        // 아예 새거로 갈아끼는 방식
        List<Town> townList = new ArrayList<>();

        for (Long tId : townIdList) {
            Town town = townService.findTownById(tId);
            townList.add(town);
        }
        admin.setManageTown(townList);
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
}
