package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.form.AdminRegisterForm;
import sejong.smartnotice.form.SupporterRegisterForm;
import sejong.smartnotice.service.AdminService;
import sejong.smartnotice.service.SupporterService;

@Slf4j
@Controller
@RequestMapping
@RequiredArgsConstructor
public class HomeController {

    private final AdminService adminService;
    private final SupporterService supporterService;

    @GetMapping("/login")
    public String adminLoginForm(String error, String logout, Model model) {
        log.info("== 관제 사이트 로그인 == ");
        log.info("error: {}", error);
        log.info("logout: {}", logout);
        if(error != null) {
            model.addAttribute("error", "Check your Account");
        }
        if(logout != null) {
            log.info("== 관리자 로그아웃 ==");
            model.addAttribute("logout", "Logout");
        }
        return "login";
    }

    @PostMapping("/logout")
    public void logout() {
        log.info("== 관리자 로그아웃 ==");
    }

    @GetMapping("/register")
    public String registerAdminForm(Model model) {
        model.addAttribute("admin", new AdminRegisterForm());
        return "register";
    }
    
    // 관리자 회원가입
    @PostMapping("/register")
    public String registerAdmin(@Validated @ModelAttribute("admin") AdminRegisterForm form,
                                BindingResult bindingResult) {
        if(adminService.findByLoginId(form.getLoginId()) != null) {
            bindingResult.addError(new FieldError("admin", "loginId", form.getLoginId(), false, null, null, "중복된 아이디가 존재합니다"));
        }
        if(adminService.findByTel(form.getTel()) != null) {
            bindingResult.addError(new FieldError("admin", "tel", form.getTel(), false, null, null, "중복된 전화번호가 존재합니다"));
        }
        if(bindingResult.hasErrors()) {
            log.warn("검증 오류 발생: {}", bindingResult);
            return "register";
        }
        adminService.register(form.getName(), form.getTel(), form.getLoginId(), form.getLoginPw(), form.getType());
        return "redirect:/";
    }

    @GetMapping("/register/supporter")
    public String registerSupporterForm(Model model) {
        model.addAttribute("supporter", new SupporterRegisterForm());
        return "register/supporterRegister";
    }

    @PostMapping("/register/supporter")
    public String registerSupporter(@Validated @ModelAttribute("supporter") SupporterRegisterForm form,
                                    BindingResult bindingResult) {
        if(supporterService.findByLoginId(form.getLoginId()) != null) {
            bindingResult.addError(new FieldError("supporter", "loginId", form.getLoginId(), false, null, null, "중복된 아이디가 존재합니다"));
        }
        if(supporterService.findByTel(form.getTel()) != null) {
            bindingResult.addError(new FieldError("supporter", "tel", form.getTel(), false, null, null, "중복된 전화번호가 존재합니다"));
        }
        if(bindingResult.hasErrors()) {
            log.warn("검증 오류 발생: {}", bindingResult);
            return "register/supporterRegister";
        }
        supporterService.register(form.getName(), form.getTel(), form.getLoginId(), form.getLoginPw(), form.getUserId());
        return "redirect:/";
    }
}
