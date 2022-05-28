package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.Admin_Town;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.dto.AdminListPageDTO;
import sejong.smartnotice.dto.AdminModifyDTO;
import sejong.smartnotice.service.AdminService;
import sejong.smartnotice.service.TownService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin")
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
            log.info(admin.getName());
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

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteAdmin(@PathVariable Long id) {
        log.info("== 관리자 삭제 ==");
        adminService.delete(id);
        return ResponseEntity.ok().body("관리자를 성공적으로 삭제했습니다");
    }

    @PutMapping
    public String updateAdmin(@RequestParam Long id, @RequestParam(required = false, value = "town") List<Long> townIdList) {
        log.info("== 관리자 관리 마을 추가 ==");
        Admin admin = adminService.findById(id);

        // 현재 관리 마을 목록
        List<Town> townList = adminService.getTownList(admin);

        // 현재 관리중인 마을 전체 취소 후 새로 생성
        for (Town town : townList) {
            townService.removeTownAdmin(town.getId(), admin.getId());
        }
        if (townIdList != null) {
            for (Long townId : townIdList) {
                townService.addTownAdmin(townId, admin.getId());
            }
        }
        return "redirect:/admin";
    }

    @GetMapping("/{id}")
    public String getAdmin(@PathVariable Long id, Model model) {
        log.info("== 관리자 조회 ==");
        Admin admin = adminService.findById(id);
        model.addAttribute("admin", admin);

        return "admin/detail";
    }

    @GetMapping("/{id}/edit")
    public String modifyForm(@PathVariable Long id, Model model) {
        log.info("== 관리자 수정 ==");
        Admin admin = adminService.findById(id);
        model.addAttribute("admin", admin);

        return "admin/modify";
    }

    @PutMapping("/{id}")
    public String modify(@PathVariable Long id, @Validated @ModelAttribute("admin") AdminModifyDTO modifyDTO,
                         BindingResult bindingResult) {
        log.info("== 관리자 정보 수정 ==");
        Admin findAdmin = adminService.findByTel(modifyDTO.getTel());
        if (findAdmin != null && findAdmin.getId() != modifyDTO.getId()) {
            bindingResult.addError(new FieldError("admin", "tel", modifyDTO.getTel(), false, null, null, "중복된 전화번호가 존재합니다"));
        }
        if (bindingResult.hasErrors()) {
            log.warn("검증 오류 발생: {}", bindingResult);
            return "admin/modify";
        }
        adminService.modifyAdminInfo(modifyDTO);
        return "redirect:/admin";
    }
}
