package sejong.smartnotice.controller.viewController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.*;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.service.*;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
@Controller
@RequestMapping(value = "/towns", produces = MediaType.TEXT_HTML_VALUE)
@RequiredArgsConstructor
public class TownController {

    private final TownDataService townDataService;

    private final TownService townService;
    private final AdminService adminService;
    private final EntityManager em;

    @GetMapping("/{townId}")
    public String getTownDetail(@AuthenticationPrincipal Admin admin,
                                @PathVariable Long townId,
                                Model model) {
        log.info("== 마을 상세 조회 ==");
        // 1. 마을 관리 권한 확인
        if(!townService.isTownAdmin(townId, admin.getId())) {
            throw new AccessDeniedException("마을 관리 권한이 없습니다");
        }

        // 2. Redis에서 캐시 데이터 조회
        TownData townData = townDataService.findById(townId);
        model.addAttribute("townData", townData);

        // 3. 마을 수정을 위한 전체 지역 목록 조회
        // TODO: 이거 별도로 분리하기 (마을 수정 분리)
        List<Region> regionList = em.createQuery("select r from Region r", Region.class).getResultList();
        model.addAttribute("regionList", regionList);
        return "town/townDetail";
    }


    @GetMapping("/{id}/admin/new")
    public String addTownAdminForm(@PathVariable Long id, Model model) {
        log.info("== 마을 관리자 신규 등록 ==");
        Town town = townService.findById(id);
        List<User> userList = town.getUserList();
        List<Admin> adminList = adminService.findNotTownAdmin(town);

        model.addAttribute("userList", userList);
        model.addAttribute("adminList", adminList);
        model.addAttribute("townId", id);
        return "town/townAdminRegister";
    }
}
