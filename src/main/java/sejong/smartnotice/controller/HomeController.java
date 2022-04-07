package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.form.AdminRegisterForm;
import sejong.smartnotice.form.SupporterRegisterForm;
import sejong.smartnotice.form.UserRegisterForm;
import sejong.smartnotice.service.AdminService;
import sejong.smartnotice.service.SupporterService;
import sejong.smartnotice.service.TownService;
import sejong.smartnotice.service.UserService;

import java.util.List;

@Slf4j
@Controller
@RequestMapping
@RequiredArgsConstructor
public class HomeController {

    private final AdminService adminService;
    private final UserService userService;
    private final SupporterService supporterService;
    private final TownService townService;

    @GetMapping("/register")
    public void register() { }

    @GetMapping("/register/admin")
    public String registerAdminForm(Model model) {
        model.addAttribute("admin", new AdminRegisterForm());
        return "register/adminRegister";
    }
    
    // 관리자 회원가입
    @PostMapping("/register/admin")
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
            return "register/adminRegister";
        }
        adminService.register(form.getName(), form.getTel(), form.getLoginId(), form.getLoginPw(), form.getType());
        return "redirect:/";
    }

    @GetMapping("/register/user")
    public String registerUserForm(Model model) {
        List<Town> townList = townService.findAll();
        model.addAttribute("user", new UserRegisterForm());
        model.addAttribute("townList", townList);
        return "register/userRegister";
    }

    // 마을 주민 회원가입
    @PostMapping("/register/user")
    public String registerUser(@Validated @ModelAttribute("user") UserRegisterForm form,
            BindingResult bindingResult, Model model) {
        if(userService.findByLoginId(form.getLoginId()) != null) {
            bindingResult.addError(new FieldError("user", "loginId", form.getLoginId(), false, null, null, "중복된 아이디가 존재합니다"));
        }
        if(userService.findByTel(form.getTel()) != null) {
            bindingResult.addError(new FieldError("user", "tel", form.getTel(), false, null, null, "중복된 전화번호가 존재합니다"));
        }
        if(bindingResult.hasErrors()) {
            log.warn("검증 오류 발생: {}", bindingResult);
            List<Town> townList = townService.findAll();
            model.addAttribute("townList", townList);
            return "register/userRegister";
        }
        userService.register(form.getName(), form.getTel(), form.getAddress(), form.getAge(),
                form.getTownId(), form.getLoginId(), form.getLoginPw());

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
