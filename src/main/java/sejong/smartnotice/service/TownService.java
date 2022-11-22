package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.TownAdmin;
import sejong.smartnotice.domain.Region;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.helper.dto.request.TownRequest.TownCreateRequest;
import sejong.smartnotice.helper.dto.request.TownRequest.TownModifyRequest;
import sejong.smartnotice.helper.dto.response.TownResponse;
import sejong.smartnotice.repository.TownRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TownService {

    private final TownDataService townDataService;

    private final AdminService adminService;
    private final TownRepository townRepository;
    private final EntityManager em;

    // 신규 마을 등록
    public TownResponse register(TownCreateRequest registerDTO) {
        log.info("== 마을 등록 ==");
        // 1. 지역 조회
        Region region = findRegion(registerDTO.getRegionCode());

        // 2. 마을 생성
        Town town = Town.createTown(registerDTO.getName(), region);
        Town createdTown = townRepository.save(town);

        // Redis create
        townDataService.action(townData -> townData, createdTown.getId());
        return TownResponse.from(createdTown);
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
    public TownResponse modifyTownInfo(Long townId, TownModifyRequest modifyDTO) {
        log.info("== 마을 정보 수정 ==");
        // 1. 마을 조회 및 검증
        Town town = findById(townId);

        // 2. 지역 조회 및 검증
        Region region = findRegion(modifyDTO.getRegionCode());

        // 3. 마을 정보 수정
        town.modifyTownInfo(modifyDTO.getName(), region);

        // Redis Update
        townDataService.action(townData -> {
            townData.setTownName(modifyDTO.getName());
            townData.setRegionId(region.getId());
            return townData;
        }, town.getId());

        return TownResponse.from(town);
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

                // Redis Update
                townDataService.action(townData -> {
                    townData.setTownAdminCnt(townData.getTownAdminCnt() - 1);
                    return townData;
                }, town.getId());
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

                // Redis Update
                townDataService.action(townData -> {
                    townData.setTownAdminCnt(townData.getTownAdminCnt() + 1);
                    return townData;
                }, town.getId());
            }
        }
    }

    // 마을 대표 관리자 설정
    public void setTownRepresentativeAdmin(Long townId, Long adminId) {
        Town town = findById(townId);
        if (!isTownAdmin(townId, adminId)) {
            throw new IllegalArgumentException("마을에 속한 관리자가 아닙니다");
        }
        Admin admin = adminService.findById(adminId);
        town.setRepresentativeAdmin(adminId);

        // Redis Update
        townDataService.action(townData -> {
            townData.setMainAdminName(admin.getName());
            townData.setMainAdminTel(admin.getTel());
            return townData;
        }, town.getId());
    }
    
    // 관리자가 해당 마을 관리자가 맞는지 검증
    public boolean isTownAdmin(Long townId, Long adminId) {
        return townRepository.findTownsByAdmin(adminId).stream()
                .anyMatch(town -> town.getId().equals(townId));
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

    // 관리자가 관리하는 마을 목록 조회
    public List<Town> findByAdmin(Long adminId) {
        return townRepository.findTownsByAdmin(adminId);
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

    public List<Town> findTownsByTownIdList(List<Long> townIdList) {
        return townRepository.findTownsByTownIdList(townIdList);
    }

    // 관리자 관리 마을 목록 조회
    public List<Town> findTownByAdmin(Admin admin) {
        return townRepository.findTownsByAdmin(admin.getId());
    }

    // 마을 관리자가 대표 관리자로 있는 마을의 ID 리턴
    public List<Long> findByRepresentativeAdminId(Long adminId) {
        return townRepository.findByRepresentativeAdminId(adminId).stream()
                .map(Town::getId)
                .collect(Collectors.toList());
    }
}
