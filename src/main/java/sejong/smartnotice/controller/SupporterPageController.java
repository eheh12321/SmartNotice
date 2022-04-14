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
import sejong.smartnotice.dto.SupporterRegisterDTO;
import sejong.smartnotice.service.SupporterService;

@Slf4j
@Controller
@RequestMapping("/s")
@RequiredArgsConstructor
public class SupporterPageController {
    
    private final SupporterService supporterService;

    @GetMapping
    public String index() {
        return "s/index";
    }

    @GetMapping("/login")
    public String userLoginForm(String error, String logout, Model model) {
        log.info("== 보호자 로그인 == ");
        log.info("error: {}", error);
        log.info("logout: {}", logout);
        if(error != null) {
            model.addAttribute("error", "Check your Account");
        }
        if(logout != null) {
            log.info("== 보호자 로그아웃 ==");
            model.addAttribute("logout", "Logout");
        }
        return "s/login";
    }

    @PostMapping("/logout")
    public void logout() {
        log.info("== 보호자 로그아웃 ==");
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("supporter", new SupporterRegisterDTO());
        return "s/register";
    }

    @PostMapping("/register")
    public String register(@Validated @ModelAttribute("supporter") SupporterRegisterDTO registerDTO,
                                    BindingResult bindingResult) {
        if(supporterService.findByLoginId(registerDTO.getLoginId()) != null) {
            bindingResult.addError(new FieldError("supporter", "loginId", registerDTO.getLoginId(), false, null, null, "중복된 아이디가 존재합니다"));
        }
        if(supporterService.findByTel(registerDTO.getTel()) != null) {
            bindingResult.addError(new FieldError("supporter", "tel", registerDTO.getTel(), false, null, null, "중복된 전화번호가 존재합니다"));
        }
        if(bindingResult.hasErrors()) {
            log.warn("검증 오류 발생: {}", bindingResult);
            return "s/register";
        }
        supporterService.register(registerDTO);
        return "redirect:/s";
    }
}
