package sejong.smartnotice.controller.viewController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.*;
import sejong.smartnotice.domain.announce.Announce;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.AdminType;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.helper.dto.request.register.AdminRegisterDTO;
import sejong.smartnotice.helper.dto.ComplexDTO;
import sejong.smartnotice.helper.dto.TownModifyDTO;
import sejong.smartnotice.service.*;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping(value = "/towns", produces = MediaType.TEXT_HTML_VALUE)
@RequiredArgsConstructor
public class TownController {

    private final TownService townService;
    private final AdminService adminService;
    private final UserService userService;
    private final EntityManager em;

    /**
     * 자료 가져올 때도 페이징 적용이 되었으면 좋겠는데..
     */
    @GetMapping("/{id}")
    public String getTownDetail(@AuthenticationPrincipal Admin admin, @PathVariable Long id, Model model) throws IOException {
        log.info("== 마을 상세 조회 ==");
        Town town = townService.findById(id);
        List<Admin_Town> adminTownList = town.getAtList();
        List<Admin> adminList = new ArrayList<>();

        for (Admin_Town adminTown : adminTownList) {
            adminList.add(adminTown.getAdmin());
        }

        boolean isAdmin = adminTownList.stream()
                .map(Admin_Town::getAdmin)
                .anyMatch(a -> a.equals(admin));

        if (!isAdmin) {
            throw new AccessDeniedException("마을 관리 권한이 없습니다");
        }

        List<Announce> announceList = em.createQuery("select a from Announce a join fetch a.atList at where at.town=:town", Announce.class)
                .setParameter("town", town)
                .getResultList();
        List<User> userList = town.getUserList();

        ComplexDTO dto = ComplexDTO.from(town, adminList, userList, announceList);

        List<Region> regionList = em.createQuery("select r from Region r", Region.class).getResultList();

        model.addAttribute("dto", dto);
        model.addAttribute("regionList", regionList);
        model.addAttribute("town", new TownModifyDTO(town.getId(), town.getName(), town.getRegion().getId()));
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

    // 마을 관리자 등록(등록)
    @PostMapping("/{id}/admin/new")
    public String addTownAdmin(@PathVariable Long id, @RequestParam(required = false) Long userId, @RequestParam(required = false) Long adminId) {
        log.info("== 마을 관리자 등록 ==");

        if (userId != null && adminId == null) {
            User user = userService.findById(userId); // 주민 정보 조회
            AdminRegisterDTO registerDTO = new AdminRegisterDTO(user.getName(), user.getTel(),
                    user.getAccount().getLoginId(), user.getAccount().getLoginPw(), AdminType.ADMIN);
            Long newAdminId = adminService.register(registerDTO); // 주민 계정 정보로 관리자 계정 생성
            user.modifyUserIsAdmin(); // 마을 주민이 마을 관리자라는 상태 표시
            townService.addTownAdmin(id, newAdminId); // 관리자와 마을 연결
        } else if (userId == null && adminId != null) {
            townService.addTownAdmin(id, adminId); // 관리자와 마을 연결
        } else {
            log.warn("잘못된 요청입니다");
        }
        return "redirect:/towns/{id}";
    }

    // 마을 관리자 삭제
    @DeleteMapping("/{id}/admin")
    public String removeTownAdmin(@PathVariable Long id, @RequestParam Long adminId) {
        log.info("== 마을 관리자 삭제 ==");
        townService.removeTownAdmin(id, adminId);
        return "redirect:/towns/{id}";
    }
}
