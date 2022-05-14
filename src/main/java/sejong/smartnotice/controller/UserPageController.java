package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/u")
@RequiredArgsConstructor
public class UserPageController {

    @GetMapping("/login")
    public String userLogin() {
        log.info("== 마을 주민 로그인 ==");
        return "login";
    }

    @GetMapping
    public String index() {
        return "u/index";
    }
}
