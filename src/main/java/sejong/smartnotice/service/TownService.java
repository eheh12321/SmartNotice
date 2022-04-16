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
import java.util.stream.Collectors;

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
        Town town = validateTownId(townId);
        if(!town.getUserList().isEmpty()) {
            log.warn("마을 삭제 실패");
            throw new RuntimeException("마을에 속한 주민이 없어야 마을을 삭제할 수 있습니다");
        }
        em.remove(town);
    }

    // 마을 정보 수정
    public Long modifyTownInfo(TownModifyDTO modifyDTO) {
        // 1. 마을 조회 및 검증
        Town town = findById(modifyDTO.getId());

        // 2. 지역 조회 및 검증
        Region region = findRegion(modifyDTO.getRegionCode());

        // 2-1. 바뀐 정보가 없으면 아무것도 하지 않음
        if(town.getName().equals(modifyDTO.getName()) && town.getRegion().equals(region)) {
            return town.getId();
        }

        // 3. 중복 검증
        validateDuplicateTown(modifyDTO.getName(), region);

        // 4. 마을 정보 수정
        town.modifyTownInfo(modifyDTO.getName(), region);
        return town.getId();
    }

    // 마을 관리자 등록
    public void addTownAdmin(Long townId, Long adminId) {
        Town town = findById(townId);
        Admin admin = adminService.findById(adminId);

        town.addTownAdmin(admin);
    }

    // 마을 관리자 삭제
    public void removeTownAdmin(Long townId, Long adminId) {
        Town town = findById(townId);
        Admin admin = adminService.findById(adminId);

        List<Admin_Town> atList = em.createQuery("select at from Admin_Town at where at.town=:town", Admin_Town.class)
                .setParameter("town", town).getResultList();

        List<Admin_Town> newAtList = atList.stream().filter(at -> {
            if (at.getAdmin() == admin) {
                log.info("관리자 확인, {}", admin.getName());
                at.getAdmin().getAtList().remove(at);
                em.remove(at);
                log.info("남은 관리마을: {}", admin.getAtList().toString());
                return false;
            } else return true;
        }).collect(Collectors.toList());

        town.removeTownAdmin(newAtList);
    }

    // 마을 조회
    public Town findById(Long townId) {
        return validateTownId(townId);
    }

    // 마을 목록 조회
    @Transactional(readOnly = true)
    public List<Town> findAll() {
        return townRepository.findAll();
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
