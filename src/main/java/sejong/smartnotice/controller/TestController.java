package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import sejong.smartnotice.dto.UserRegisterDTO;
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
            UserRegisterDTO registerDTO = new UserRegisterDTO("이름" + i, LocalDateTime.now().toString(), "주소" + i,
                    townId, i, LocalDateTime.now().toString(), "pw");
            userService.register(registerDTO);
        }
        return "redirect:/towns/{townId}";
    }

}
