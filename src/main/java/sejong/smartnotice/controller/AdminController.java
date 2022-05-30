package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.Admin_Town;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.dto.AdminListPageDTO;
import sejong.smartnotice.service.AdminService;
import sejong.smartnotice.service.TownService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping(value = "/admin", produces = MediaType.TEXT_HTML_VALUE)
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final TownService townService;
    @GetMapping
    public String getAdminList(Model model) {
        log.info("== 관리자 목록 조회 ==");
        List<Admin> adminList = adminService.findAllWithTown();
        List<Town> townList = townService.findAll();
        List<AdminListPageDTO> dtoList = new ArrayList<>();
        for (Admin admin : adminList) {
            List<Long> adminTownIdList = new ArrayList<>();
            for (Admin_Town at : admin.getAtList()) {
                adminTownIdList.add(at.getTown().getId());
            }
            AdminListPageDTO dto = new AdminListPageDTO(admin, adminTownIdList);
            dtoList.add(dto);
        }
        model.addAttribute("townList", townList);
        model.addAttribute("dtoList", dtoList);
        return "admin/list";
    }
}
