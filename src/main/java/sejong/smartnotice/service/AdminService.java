package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.Admin_Town;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.AdminRole;
import sejong.smartnotice.dto.AdminDTO;
import sejong.smartnotice.dto.LoginDTO;
import sejong.smartnotice.repository.AdminRepository;

import javax.persistence.EntityManager;
import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final EntityManager em;
    
    // 회원가입
    public Long register(String name, String tel, String id, String pw, AdminRole role) {
        Admin admin = Admin.createAdmin(name, tel, id, pw, role);
        adminRepository.save(admin);
        return admin.getId();
    }

    // 관리자 정보 수정
    public Long modifyAdminInfo(Long id, String name, String tel) {
        Admin admin = findById(id);
        admin.modifyAdminInfo(name, tel);
        return admin.getId();
    }

    // 관리자 삭제
    public void delete(Long id) {
        Admin admin = findById(id);
        adminRepository.delete(admin);
    }

    // 관리자 검색
    public Admin findById(Long id) {
        Optional<Admin> opt = adminRepository.findById(id);
        if(opt.isEmpty()) {
            log.warn("관리자가 존재하지 않습니다");
            throw new NullPointerException("관리자가 존재하지 않습니다.");
        }
        return opt.get();
    }

    // 관리자 전체 목록 조회
    public List<Admin> findAll() {
        return adminRepository.findAll();
    }

    // 관리자 이름 검색
    public List<Admin> findByName(String name) {
        return adminRepository.findByNameContaining(name);
    }

    // 마을 관리 관리자 목록 조회
    public List<Admin> findByTown(Long townId) {
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
}
