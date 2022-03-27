package sejong.smartnotice.domain.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.service.UserService;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 유저 긴급호출 (테스트)
     * http://localhost:8080/user/emergency/1
     * (사전조건 - 유저랑 보호자랑 연결되있어야됨)
     */
    @GetMapping("/emergency/{userId}")
    public void 유저긴급호출테스트용(@PathVariable Long userId) {
        userService.makeEmergencyAlert(userId);
    }
}
