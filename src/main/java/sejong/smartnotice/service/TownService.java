package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.Region;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.repository.TownRepository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TownService {

    private final TownRepository townRepository;
    private final EntityManager em;

    // 신규 마을 등록
    public Long registerTown(String townName, Long regionCode) {
        Region region = em.find(Region.class, regionCode); // 지역코드 조회
        /// 마을이름 중복 검증 코드 들어가야함 ///
        Town town = Town.builder()
                .name(townName)
                .region(region)
                .userList(new ArrayList<>()).build();

        townRepository.save(town);
        return town.getId();
    }

    // 마을 조회 (단순 위임 및 예외처리)
    public Town findTownById(Long townId) {
        return validateTownId(townId);
    }

    // 마을 삭제
    public void removeTown(Long townId) {
        Town town = validateTownId(townId);
        if(!town.getUserList().isEmpty()) {
            log.warn("마을 주민이 없어야 마을을 삭제할 수 있습니다");
            throw new RuntimeException("에러");
        }
        em.remove(town);
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
