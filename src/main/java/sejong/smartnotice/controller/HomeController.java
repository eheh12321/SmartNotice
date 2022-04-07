package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.form.AdminRegisterForm;
import sejong.smartnotice.dto.LoginDTO;
import sejong.smartnotice.dto.UserDTO;
import sejong.smartnotice.form.SupporterRegisterForm;
import sejong.smartnotice.form.UserRegisterForm;
import sejong.smartnotice.service.AdminService;
import sejong.smartnotice.service.SupporterService;
import sejong.smartnotice.service.UserService;

@Slf4j
@Controller
@RequestMapping
@RequiredArgsConstructor
public class HomeController {

    private final AdminService adminService;
    private final UserService userService;
    private final SupporterService supporterService;

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
        if(bindingResult.hasErrors()) {
            log.warn("검증 오류 발생: {}", bindingResult);
            return "register/adminRegister";
        }
        adminService.register(form.getName(), form.getTel(), form.getLoginId(), form.getLoginPw(), form.getType());
        return "redirect:/";
    }

    @GetMapping("/register/user")
    public String registerUserForm(Model model) {
        model.addAttribute("user", new UserRegisterForm());
        return "register/userRegister";
    }

    // 마을 주민 회원가입
    @PostMapping("/register/user")
    public String registerUser(@Validated @ModelAttribute("user") UserRegisterForm form,
            BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            log.warn("검증 오류 발생: {}", bindingResult);
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
        if(bindingResult.hasErrors()) {
            log.warn("검증 오류 발생: {}", bindingResult);
            return "register/supporterRegister";
        }
        supporterService.register(form.getName(), form.getTel(), form.getLoginId(), form.getLoginPw(), form.getUserId());
        return "redirect:/";
    }
}
