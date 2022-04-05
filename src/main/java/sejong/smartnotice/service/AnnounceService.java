package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.announce.Announce;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.announce.AnnounceCategory;
import sejong.smartnotice.domain.announce.AnnounceType;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.repository.AnnounceRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AnnounceService {

    private final AdminService adminService;
    private final TownService townService;
    private final AnnounceRepository announceRepository;

    public Long makeAnnounce(Long adminId, String title, AnnounceCategory category, AnnounceType type, List<Long> townIdList) {
        // 1. 방송 대상 마을 추출
        List<Town> townList = new ArrayList<>();
        for (Long tid : townIdList) {
            Town town = townService.findById(tid);
            townList.add(town);
        }

        // 2. 방송 생성
        Admin admin = adminService.findById(adminId);
        Announce announce = Announce.makeAnnounce(admin, title, AnnounceCategory.NORMAL, AnnounceType.TEXT, townList);
        announceRepository.save(announce);

        return announce.getId();
    }

    public Announce findAnnounceById(Long id) {
        Optional<Announce> opt = announceRepository.findById(id);
        if(opt.isEmpty()) {
            log.warn("방송이 존재하지 않습니다");
            throw new RuntimeException("에러");
        }
        return opt.get();
    }

    public List<Announce> findAllAnnounce() {
        return announceRepository.findAll();
    }
}
