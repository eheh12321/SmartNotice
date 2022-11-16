package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.Admin_Town;
import sejong.smartnotice.domain.Region;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.helper.dto.TownModifyDTO;
import sejong.smartnotice.helper.dto.TownRegisterDTO;
import sejong.smartnotice.repository.TownRepository;

import javax.persistence.EntityManager;
import java.util.HashMap;
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
    public void modifyTownInfo(TownModifyDTO modifyDTO) {
        log.info("== 마을 정보 수정 ==");
        // 1. 마을 조회 및 검증
        Town town = findById(modifyDTO.getId());

        // 2. 지역 조회 및 검증
        Region region = findRegion(modifyDTO.getRegionCode());

        // 3. 바뀐 정보가 없으면 아무것도 하지 않음
        if(town.getName().equals(modifyDTO.getName()) && town.getRegion().equals(region)) {
            return;
        }

        // 4. 중복 검증
        validateDuplicateTown(modifyDTO.getName(), region);

        // 5. 마을 정보 수정
        town.modifyTownInfo(modifyDTO.getName(), region);
    }

    // 관리자 관리 마을 목록 수정
    public void modifyAdminManagedTownList(Long adminId, List<Long> townIdList) {
        log.info("관리자 관리 마을 목록 수정");

        Admin admin = adminService.findById(adminId);
        // 현재 관리 마을 목록
        List<Town> managedTownList = adminService.getTownList(admin);

        // 두개의 리스트에서 차이가 있는 요소를 찾는 알고리즘
        // 체크박스 리스트를 통해 현재 관리 마을 목록 List를 갈아끼우는 비즈니스 로직
        // 1. Map에 체크된 마을 목록 ID를 싹 다 넣는다
        HashMap<Long, Integer> map = new HashMap<>();
        for (Long tId : townIdList) {
            map.put(tId, 1);
        }
        // 2. 관리자가 관리하고 있는 마을 목록을 돌면서
        for (Town town : managedTownList) {
            // 기존에 관리하고 있던 마을이 체크 해제된 경우
            if(!map.containsKey(town.getId())) {
                // 마을 관리 삭제
                removeTownAdmin(town.getId(), adminId);
            } else {
                // 유지
                map.remove(town.getId());
            }
        }
        // 3. 나머지 처리되지 않은 마을들을 대상으로
        if(!map.isEmpty()) {
            for (Long key : map.keySet()) {
                // 신규 관리 마을 추가
                addTownAdmin(key, adminId);
            }
        }
    }

    // 마을 관리자 등록
    public void addTownAdmin(Long townId, Long adminId) {
        log.info("== 마을 관리자 등록 ==");
        // 1. 마을 조회
        Town town = findById(townId);

        // 2. 관리자 조회
        Admin admin = adminService.findById(adminId);

        // 3. 마을 관리자 등록
        Admin_Town at = Admin_Town.builder().admin(admin).town(town).build();
        town.getAtList().add(at);
        admin.getAtList().add(at);
        em.persist(at);
    }

    // 마을 관리자 삭제
    public void removeTownAdmin(Long townId, Long adminId) {
        log.info("== 마을 관리자 삭제 ==");
        // 1. 마을 조회
        Town town = findById(townId);

        // 2. 관리자 조회
        Admin admin = adminService.findAdminWithTown(adminId);

        // 3. 데이터 조회
        log.info("== 삭제 데이터 조회 ==");
        Admin_Town at = em.createQuery("select at from Admin_Town at where at.town=:town and at.admin=:admin", Admin_Town.class)
                .setParameter("town", town)
                .setParameter("admin", admin).getSingleResult();

        // 4. 데이터 삭제
        em.remove(at);
        admin.getAtList().remove(at);
        town.getAtList().remove(at);
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
        return townRepository.findTownsByAdmin(admin.getId());
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
            throw new IllegalArgumentException("같은 지역에 동일한 마을이 존재합니다");
        }
    }
}
