package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.Announce;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.repository.AnnounceRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AnnounceService {

    private final AdminService adminService;
    private final TownService townService;
    private final AnnounceRepository announceRepository;

    public void makeAnnounce(Long adminId, String title, String category, String type, List<Long> townIdList) {
        // 1. 방송 대상 마을 추출
        List<Town> townList = new ArrayList<>();
        for (Long tid : townIdList) {
            Town town = townService.findTownById(tid);
            townList.add(town);
        }

        // 2. 방송 생성
        Admin admin = adminService.findById(adminId);
        Announce announce = Announce.makeAnnounce(admin, title, category, type, townList);
        announceRepository.save(announce);
    }

    public Announce 방송조회() {
        return null;
    }

    public List<Announce> 방송목록조회() {
        return null;
    }
}
