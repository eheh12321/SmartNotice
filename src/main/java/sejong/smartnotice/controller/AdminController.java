package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.form.AdminModifyForm;
import sejong.smartnotice.service.AdminService;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    public String getAdminList(Model model, @RequestParam(required = false) String name) {
        log.info("== 관리자 목록 조회 ==");
        List<Admin> adminList;
        if(StringUtils.hasText(name)) {
            adminList = adminService.findByName(name);
        } else {
            adminList = adminService.findAll();
        }
        model.addAttribute("adminList", adminList);
        return "admin/adminList";
    }

    @GetMapping("/{id}")
    public String getAdmin(@PathVariable Long id, Model model) {
        log.info("== 관리자 조회 ==");
        Admin admin = adminService.findById(id);
        model.addAttribute("admin", admin);

        return "admin/adminDetail";
    }
    
    @GetMapping("/{id}/edit")
    public String modifyForm(@PathVariable Long id, Model model) {
        log.info("== 관리자 수정 ==");
        Admin admin = adminService.findById(id);
        model.addAttribute("admin", admin);

        return "admin/modify";
    }

    @PutMapping("/{id}")
    public String modify(@PathVariable Long id, @Validated @ModelAttribute("admin") AdminModifyForm form,
                         BindingResult bindingResult) {
        log.info("== 관리자 정보 수정 ==");
        if(bindingResult.hasErrors()) {
            log.warn("검증 오류 발생: {}", bindingResult);
            return "admin/modify";
        }
        adminService.modifyAdminInfo(id, form.getName(), form.getTel());
        return "redirect:/admin";
    }

    @DeleteMapping("/{id}")
    public String remove(@PathVariable Long id) {
        log.info("== 관리자 삭제 ==");
        adminService.delete(id);

        return "redirect:/admin";
    }
}
