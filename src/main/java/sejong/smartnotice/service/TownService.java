package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.TownAdmin;
import sejong.smartnotice.domain.Region;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.helper.dto.TownModifyDTO;
import sejong.smartnotice.helper.dto.TownRegisterDTO;
import sejong.smartnotice.repository.TownRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.util.*;

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
        if (!town.getUserList().isEmpty()) {
            log.warn("마을에 속한 주민이 없어야 마을을 삭제할 수 있습니다");
            throw new IllegalStateException("마을에 속한 주민이 없어야 마을을 삭제할 수 있습니다");
        }

        // 3. 마을 삭제
        em.remove(town);
    }

    // 마을 정보 수정
    public void modifyTownInfo(TownModifyDTO modifyDTO) {
        log.info("== 마을 정보 수정 ==");
        // 1. 마을 조회 및 검증
        Town town = findById(modifyDTO.getId());

        // 2. 지역 조회 및 검증
        Region region = findRegion(modifyDTO.getRegionCode());

        // 3. 바뀐 정보가 없으면 아무것도 하지 않음
        if (town.getName().equals(modifyDTO.getName()) && town.getRegion().equals(region)) {
            return;
        }

        // 4. 중복 검증
        validateDuplicateTown(modifyDTO.getName(), region);

        // 5. 마을 정보 수정
        town.modifyTownInfo(modifyDTO.getName(), region);
    }

    // 관리자 관리 마을 목록 수정
    public void modifyAdminManagedTownList(Long adminId, List<Long> townIdList) {
        log.info("== 관리자 관리 마을 목록 수정 ==");

        // 1. 관리자 조회
        Admin admin = adminService.findById(adminId);
        List<Town> managedTownList = findTownByAdmin(admin); // 관리자 관리 마을 목록

        // 두개의 리스트에서 차이가 있는 요소를 찾는 알고리즘
        // 체크박스 리스트를 통해 현재 관리 마을 목록 List를 갈아끼우는 비즈니스 로직

        // 1. Set에 체크된 마을 목록 ID를 싹 다 넣는다
        Set<Long> townIdSet = townIdList == null ? new HashSet<>() : new HashSet<>(townIdList);

        // 2. 관리자가 관리하고 있는 마을 목록을 돌면서
        for (Town town : managedTownList) {
            // 기존에 관리하고 있던 마을이 체크 해제된 경우
            if (!townIdSet.contains(town.getId())) {
                // 마을 관리 삭제
                removeTownAdmin(admin, town);
            } else {
                townIdSet.remove(town.getId());
            }
        }
        // 3. 나머지 처리되지 않은 마을들 대상
        if (!townIdSet.isEmpty()) {
            Set<Town> newManageTownSet = townRepository.findTownsByTownIdSet(townIdSet);
            for (Town town : newManageTownSet) {
                // 신규 관리자 추가
                addTownAdmin(admin, town);
            }
        }
    }

    public void addTownAdmin(Admin admin, Town town) {
        log.info("== 마을 관리자 등록 ==");
        TownAdmin townAdmin = new TownAdmin(town, admin);
        townAdmin.addTownAdmin();
    }

    // 마을 관리자 삭제
    public void removeTownAdmin(Admin admin, Town town) {
        log.info("== 마을 관리자 삭제 ==");
        TownAdmin townAdmin = new TownAdmin(town, admin);
        townAdmin.removeTownAdmin();
    }

    // 마을 조회
    public Town findById(Long townId) {
        return townRepository.findById(townId)
                .orElseThrow(() -> new EntityNotFoundException("마을이 존재하지 않습니다"));
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
        if (region == null) {
            log.warn("지역이 존재하지 않습니다");
            throw new EntityNotFoundException("지역이 존재하지 않습니다");
        }
        return region;
    }

    // 관리자 관리 마을 목록 조회
    public List<Town> findTownByAdmin(Admin admin) {
        return townRepository.findTownsByAdminId(admin.getId());
    }

    // 마을 중복 검증
    private void validateDuplicateTown(String townName, Region region) {
        log.info("== 마을 중복 검증 ==");
        if (townRepository.existsByRegionAndName(region, townName)) {
            log.warn("같은 지역에 동일한 마을이 존재합니다");
            throw new IllegalArgumentException("같은 지역에 동일한 마을이 존재합니다");
        }
    }
}
