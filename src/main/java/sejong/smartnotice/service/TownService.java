package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.Admin_Town;
import sejong.smartnotice.domain.Region;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.member.Admin;
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
    public Long register(String townName, Long regionCode) {
        Region region = em.find(Region.class, regionCode); // 지역코드 조회
        Town town = Town.createTown(townName, region);

        townRepository.save(town);
        return town.getId();
    }

    // 신규 지역 등록 (할 일은 없는데 테스트용)
    public Long registerRegion(Long regionCode,String mainRegion, String subRegion) {
        // 대충 검증 코드
        Region region = Region.createRegion(regionCode, mainRegion, subRegion);
        em.persist(region);
        return region.getRegionCode();
    }

    // 마을 삭제
    public void delete(Long townId) {
        Town town = validateTownId(townId);
        if(!town.getUserList().isEmpty()) {
            log.warn("마을 주민이 없어야 마을을 삭제할 수 있습니다");
            throw new RuntimeException("에러");
        }
        em.remove(town);
    }

    // 마을 정보 수정
    public Long modifyTownInfo(Long id, String name, Long regionCode) {
        Town town = findById(id);
        Region region = em.find(Region.class, regionCode);
        town.modifyTownInfo(name, region);
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
                at.getAdmin().getTownList().remove(at);
                em.remove(at);
                log.info("남은 관리마을: {}", admin.getTownList().toString());
                return false;
            } else return true;
        }).collect(Collectors.toList());

        town.removeTownAdmin(newAtList);
    }

    // 마을 목록 조회
    public List<Town> findAll() {
        return townRepository.findAll();
    }

    // 마을 조회 (단순 위임 및 예외처리)
    public Town findById(Long townId) {
        return validateTownId(townId);
    }

    // 마을 이름 검색
    public List<Town> findByName(String name) {
        return townRepository.findByNameContaining(name);
    }

    // 마을ID 검증
    private Town validateTownId(Long townId) {
        Optional<Town> opt = townRepository.findById(townId);
        if(opt.isEmpty()) {
            log.warn("마을이 존재하지 않습니다");
            throw new RuntimeException("에러");
        }
        return opt.get();
    }
}
