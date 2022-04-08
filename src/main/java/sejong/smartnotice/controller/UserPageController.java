package sejong.smartnotice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/u")
public class UserPageController {

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
}
