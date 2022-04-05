package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.service.AdminService;
import sejong.smartnotice.service.TownService;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/town")
@RequiredArgsConstructor
public class TownController {

    private final TownService townService;
    private final AdminService adminService;

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
        return "/town/townList";
    }

    @GetMapping("/new")
    public String registerTown() {
        return "/town/register";
    }

    @PostMapping
    public String register(String name, Long regionCode) {
        log.info("== 마을 등록 ==");
        townService.register(name, regionCode);
        return "redirect:/town";
    }

    @GetMapping("/{id}")
    public String getTownDetail(@PathVariable Long id, Model model) {
        log.info("== 마을 상세 조회 ==");
        Town town = townService.findById(id);
        model.addAttribute("town", town);
        return "/town/townDetail";
    }

    @GetMapping("/{id}/edit")
    public String modify(@PathVariable Long id, Model model) {
        log.info("== 마을 수정 ==");
        Town town = townService.findById(id);
        model.addAttribute("town", town);
        return "/town/modify";
    }

    @PutMapping("/{id}")
    public String modify(@PathVariable Long id, @RequestParam String name, @RequestParam Long regionCode) {
        log.info("== 마을 수정 ==");
        townService.modifyTownInfo(id, name, regionCode);
        return "redirect:/town";
    }
    
    @DeleteMapping("/{id}")
    public String remove(@PathVariable Long id) {
        log.info("== 마을 삭제 ==");
        townService.delete(id);
        return "redirect:/town";
    }

    // 마을 관리자 등록(목록 조회)
    @GetMapping("/{id}/admin")
    public String addTownAdminForm(@PathVariable Long id, Model model) {
        log.info("== 마을 관리자 목록 조회 ==");
        Town town = townService.findById(id);
        model.addAttribute("town", town);

        List<Admin> adminList = adminService.findAll();
        model.addAttribute("adminList", adminList);

        return "/town/adminList";
    }

    // 마을 관리자 등록(등록)
    @PostMapping("/{id}/admin")
    public String addTownAdmin(@PathVariable Long id, @RequestParam Long adminId) {
        log.info("== 마을 관리자 등록 ==");
        townService.addTownAdmin(id, adminId);
        return "redirect:/town/{id}";
    }

    // 마을 관리자 삭제
    @DeleteMapping("/{id}/admin")
    public String removeTownAdmin(@PathVariable Long id, @RequestParam Long adminId) {
        log.info("== 마을 관리자 삭제 ==");
        townService.removeTownAdmin(id, adminId);
        return "redirect:/town/{id}";
    }
}
