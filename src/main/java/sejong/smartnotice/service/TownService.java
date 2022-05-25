package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.Admin_Town;
import sejong.smartnotice.domain.Region;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.dto.TownModifyDTO;
import sejong.smartnotice.dto.TownRegisterDTO;
import sejong.smartnotice.repository.TownRepository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TownService {

    private final AdminService adminService;
    private final TownRepository townRepository;
    private final EntityManager em;

    // 신규 마을 등록
    public Long register(TownRegisterDTO registerDTO) {
        log.info("== 마을 등록 ==");
        // 1. 지역 조회
        Region region = findRegion(registerDTO.getRegionCode());

        // 2. 중복 검증
        validateDuplicateTown(registerDTO.getName(), region);

        // 3. 마을 생성
        Town town = Town.createTown(registerDTO.getName(), region);
        townRepository.save(town);

        return town.getId();
    }

    // 마을 삭제
    public void delete(Long townId) {
        log.info("== 마을 삭제 ==");
        // 1. 마을 조회
        Town town = findById(townId);

        // 2. 삭제 가능 유무 조사 (마을에 속한 주민이 없어야 함)
        if(!town.getUserList().isEmpty()) {
            log.warn("마을에 속한 주민이 없어야 마을을 삭제할 수 있습니다");
            throw new IllegalStateException("마을에 속한 주민이 없어야 마을을 삭제할 수 있습니다");
        }
        
        // 3. 마을 삭제
        em.remove(town);
    }

    // 마을 정보 수정
    public Long modifyTownInfo(TownModifyDTO modifyDTO) {
        log.info("== 마을 정보 수정 ==");
        // 1. 마을 조회 및 검증
        Town town = findById(modifyDTO.getId());

        // 2. 지역 조회 및 검증
        Region region = findRegion(modifyDTO.getRegionCode());

        // 3. 바뀐 정보가 없으면 아무것도 하지 않음
        if(town.getName().equals(modifyDTO.getName()) && town.getRegion().equals(region)) {
            return town.getId();
        }

        // 4. 중복 검증
        validateDuplicateTown(modifyDTO.getName(), region);

        // 5. 마을 정보 수정
        town.modifyTownInfo(modifyDTO.getName(), region);
        return town.getId();
    }

    // 마을 관리자 등록
    public void addTownAdmin(Long townId, Long adminId) {
        log.info("== 마을 관리자 등록 ==");
        // 1. 마을 조회
        Town town = findById(townId);
        
        // 2. 관리자 조회
        Admin admin = adminService.findById(adminId);

        // 3. 관리자가 현재 관리하는 마을 명단에 현재 마을이 있으면 등록 취소
        if(adminService.getTownList(admin).contains(town)) {
            log.warn("이미 관리자로 등록되어 있습니다!");
            return;
        }

        // 4. 마을 관리자 등록
        town.addTownAdmin(admin);
    }

    // 마을 관리자 삭제
    public void removeTownAdmin(Long townId, Long adminId) {
        log.info("== 마을 관리자 삭제 ==");
        // 1. 마을 조회
        Town town = findById(townId);

        // 2. 관리자 조회
        Admin admin = adminService.findById(adminId);

        // 3. 데이터 조회
        log.info("== 삭제 데이터 조회 ==");
        Admin_Town at = em.createQuery("select at from Admin_Town at " +
                "where at.town=:town and at.admin=:admin", Admin_Town.class)
                .setParameter("town", town)
                .setParameter("admin", admin).getSingleResult();

        // 4. 데이터 삭제
        town.removeTownAdmin(at);
        em.remove(at);
    }

    // 마을 조회
    public Town findById(Long townId) {
        return validateTownId(townId);
    }

    // 마을 목록 조회
    @Transactional(readOnly = true)
    public List<Town> findAll() {
        return townRepository.findAllWithRegion();
    }

    // 마을 이름 검색
    @Transactional(readOnly = true)
    public List<Town> findByName(String name) {
        return townRepository.findByNameContaining(name);
    }

    @Transactional(readOnly = true)
    public Region findRegion(Long regionCode) {
        Region region = em.find(Region.class, regionCode);
        if(region == null) {
            log.warn("지역이 존재하지 않습니다");
            throw new NullPointerException("지역이 존재하지 않습니다");
        }
        return region;
    }

    // 관리자 관리 마을 목록 조회
    @Transactional(readOnly = true)
    public List<Town> findTownByAdmin(Admin admin) {
        return em.createQuery("select t from Town t join fetch t.adminList at where at.admin.id=:adminId", Town.class)
                .setParameter("adminId", admin.getId())
                .getResultList();
    }

    // 지역 목록 반환
    @Transactional(readOnly = true)
    public List<Region> findAllRegion() {
        return em.createQuery("select r from Region r", Region.class)
                .getResultList();
    }

    // 마을ID 검증
    private Town validateTownId(Long townId) {
        log.info("== 마을 아이디 검증 ==");
        Optional<Town> opt = townRepository.findById(townId);
        if(opt.isEmpty()) {
            log.warn("마을이 존재하지 않습니다");
            throw new NullPointerException("마을이 존재하지 않습니다");
        }
        return opt.get();
    }

    // 마을 중복 검증
    private void validateDuplicateTown(String townName, Region region) {
        log.info("== 마을 중복 검증 ==");
        if(townRepository.existsByRegionAndName(region, townName)) {
            log.warn("같은 지역에 동일한 마을이 존재합니다");
            throw new IllegalStateException("같은 지역에 동일한 마을이 존재합니다");
        }
    }
}
