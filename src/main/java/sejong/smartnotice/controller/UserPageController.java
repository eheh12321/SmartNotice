package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.form.UserRegisterForm;
import sejong.smartnotice.service.TownService;
import sejong.smartnotice.service.UserService;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/u")
@RequiredArgsConstructor
public class UserPageController {

    private final UserService userService;
    private final TownService townService;

    @GetMapping
    public String index() {
        return "u/index";
    }

    @GetMapping("/login")
    public String userLoginForm(String error, String logout, Model model) {
        log.info("== 마을주민 로그인 == ");
        log.info("error: {}", error);
        log.info("logout: {}", logout);
        if(error != null) {
            model.addAttribute("error", "Check your Account");
        }
        if(logout != null) {
            log.info("== 마을 주민 로그아웃 ==");
            model.addAttribute("logout", "Logout");
        }
        return "u/login";
    }

    @PostMapping("/logout")
    public void logout() {
        log.info("== 마을 주민 로그아웃 ==");
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        List<Town> townList = townService.findAll();
        model.addAttribute("user", new UserRegisterForm());
        model.addAttribute("townList", townList);
        return "u/register";
    }

    // 마을 주민 회원가입
    @PostMapping("/register")
    public String register(@Validated @ModelAttribute("user") UserRegisterForm form,
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
            return "u/register";
        }
        userService.register(form.getName(), form.getTel(), form.getAddress(), form.getAge(),
                form.getTownId(), form.getLoginId(), form.getLoginPw());

        return "redirect:/u";
    }
}
