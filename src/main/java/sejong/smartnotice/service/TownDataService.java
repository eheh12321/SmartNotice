package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sejong.smartnotice.domain.AlertType;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.TownData;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.helper.function.TownDataAction;
import sejong.smartnotice.repository.TownDataRepository;
import sejong.smartnotice.repository.TownRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service
public class TownDataService {

    private final EntityManager em;
    private final TownRepository townRepository;
    private final TownDataRepository townDataRepository;

    // Redis에서 조회 - 없으면 새로 만들어서 저장 이후 전달 (내용을 업데이트 하고 나서 줘야함!!)
    public TownData findById(Long townId) {
        return townDataRepository.findById(townId)
                .orElseGet(() -> initTownData(townId));
    }

    public void action(TownDataAction doSomething, Long townId) {
        // 캐시 데이터가 있으면 액션을 하고, 없으면 새로 생성 후 동기화 (액션 X)
        townDataRepository.findById(townId)
                .ifPresentOrElse(townData -> {
                    TownData actionAfterTownData = doSomething.action(townData);
                    save(actionAfterTownData);
                }, () -> initTownData(townId));
    }
    
    // 캐시 데이터 생성 및 동기화
    private TownData initTownData(Long townId) {
        log.info("마을_{}의 캐시 데이터를 초기화 합니다", townId);
        Town town = townRepository.findById(townId)
                .orElseThrow(() -> new EntityNotFoundException("마을이 존재하지 않습니다"));
        TownData townData = new TownData(town.getId(), town.getName(), town.getRegion().getId());

        // 1. 방송 횟수 조회 (JPQL에서 count문은 Long타입을 리턴한다)
        Long announceCnt = em.createQuery("select count(ta) from TownAnnounce ta where ta.town.id=:townId", Long.class)
                .setParameter("townId", townId)
                .getSingleResult();
        townData.setAnnounceCnt(announceCnt.intValue());

        // 2. 마을 주민 조회
        List<User> userList = em.createQuery("select u from User u where u.town.id=:townId", User.class)
                .setParameter("townId", townId)
                .getResultList();
        townData.setUserCnt(userList.size());

        // 3. 긴급 호출 횟수 조회
        List<Object[]> alertCntList = em.createQuery("select ea.alertType, count(ea.alertType) from EmergencyAlert ea where ea.user in :userList group by ea.alertType", Object[].class)
                .setParameter("userList", userList)
                .getResultList();

        int totalAlertCnt = 0;
        for (Object[] result : alertCntList) {
            AlertType type = (AlertType) result[0];
            int count = ((Long) result[1]).intValue();
            totalAlertCnt += count;
            switch (type) {
                case USER:
                    townData.setUserAlertCnt(count);
                    break;
                case FIRE:
                    townData.setFireAlertCnt(count);
                    break;
                case MOTION:
                    townData.setMotionAlertCnt(count);
                    break;
            }
        }
        townData.setAlertCnt(totalAlertCnt);

        // 4. 마을 관리자 조회
        Long townAdminCnt = em.createQuery("select count(ta) from TownAdmin ta where ta.town.id=:townId", Long.class)
                .setParameter("townId", townId)
                .getSingleResult();
        townData.setTownAdminCnt(townAdminCnt.intValue());

        // 5. 대표 관리자 조회
        List<Admin> representativeAdminList = em.createQuery("select a from Admin a where a.id=:adminId", Admin.class)
                .setParameter("adminId", town.getRepresentativeAdminId())
                .getResultList(); 
        if(!representativeAdminList.isEmpty()) {
            townData.setMainAdminName(representativeAdminList.get(0).getName());
        }
        

        // update
        return townDataRepository.save(townData);
    }

    private void save(TownData townData) {
        townDataRepository.save(townData);
    }
}
