package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.Region;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.AdminType;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.service.AdminService;
import sejong.smartnotice.service.TownService;
import sejong.smartnotice.service.UserService;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/towns")
@RequiredArgsConstructor
public class TownController {

    private final TownService townService;
    private final AdminService adminService;
    private final UserService userService;

    @GetMapping
    public String getTownList(Model model, @RequestParam(required = false) String name) {
        log.info("== 마을 목록 조회 ==");
        List<Town> townList;
        if(StringUtils.hasText(name)) {
            townList = townService.findByName(name);
        } else {
            townList = townService.findAll();
        }
        model.addAttribute("townList", townList);
        return "town/townList";
    }

    @GetMapping("/new")
    public String registerTown(Model model) {
        List<Region> regionList = townService.findAllRegion();
        model.addAttribute("regionList", regionList);
        return "town/register";
    }

    @PostMapping
    public String register(String name, Long regionCode) {
        log.info("== 마을 등록 ==");
        townService.register(name, regionCode);
        return "redirect:/towns";
    }

    @GetMapping("/{id}")
    public String getTownDetail(@PathVariable Long id, Model model) {
        log.info("== 마을 상세 조회 ==");
        Town town = townService.findById(id);
        model.addAttribute("town", town);
        return "town/townDetail";
    }

    @GetMapping("/{id}/edit")
    public String modify(@PathVariable Long id, Model model) {
        log.info("== 마을 수정 ==");
        Town town = townService.findById(id);
        List<Region> regionList = townService.findAllRegion();
        model.addAttribute("town", town);
        model.addAttribute("regionList", regionList);
        return "town/modify";
    }

    @PutMapping("/{id}")
    public String modify(@PathVariable Long id, @RequestParam String name, @RequestParam Long regionCode) {
        log.info("== 마을 수정 ==");
        townService.modifyTownInfo(id, name, regionCode);
        return "redirect:/towns";
    }
    
    @DeleteMapping("/{id}")
    public String remove(@PathVariable Long id) {
        log.info("== 마을 삭제 ==");
        townService.delete(id);
        return "redirect:/towns";
    }

    // 마을 관리자 목록 조회
    @GetMapping("/{id}/admin")
    public String getTownAdminList(@PathVariable Long id, Model model) {
        log.info("== 마을 관리자 목록 조회 ==");
        Town town = townService.findById(id);
        model.addAttribute("town", town);

        return "town/adminList";
    }

    @GetMapping("/{id}/admin/new")
    public String addTownAdminForm(@PathVariable Long id, Model model) {
        log.info("== 마을 관리자 신규 등록 ==");
        Town town = townService.findById(id);
        List<User> userList = town.getUserList();
        List<Admin> adminList = adminService.findAll();

        model.addAttribute("userList", userList);
        model.addAttribute("adminList", adminList);
        model.addAttribute("townId", id);

        return "town/userList";
    }

    // 마을 관리자 등록(등록)
    @PostMapping("/{id}/admin/new")
    public String addTownAdmin(@PathVariable Long id, @RequestParam(required = false) Long userId, @RequestParam(required = false) Long adminId) {
        log.info("== 마을 관리자 등록 ==");

        if(userId != null && adminId == null) {
            User user = userService.findById(userId); // 주민 정보 조회
            Long newAdminId = adminService.register(user.getName(), user.getTel(), user.getLoginId(), user.getLoginPw(), AdminType.ADMIN); // 주민 계정 정보로 관리자 계정 생성
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
