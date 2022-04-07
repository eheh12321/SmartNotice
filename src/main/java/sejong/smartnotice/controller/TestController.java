package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import sejong.smartnotice.service.UserService;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final UserService userService;

    @GetMapping("/newUser/{townId}")
    public String createTestUser(@PathVariable Long townId) {
        for(int i = 1; i <= 10; i++) {
            userService.register("이름" + i, LocalDateTime.now().toString(), "주소" + i,
                    i, townId, LocalDateTime.now().toString(), "pw");
        }
        return "redirect:/towns/{townId}";
    }

}
