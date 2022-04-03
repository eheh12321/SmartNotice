package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.service.EmergencyAlertService;
import sejong.smartnotice.service.UserService;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final EmergencyAlertService alertService;

    @GetMapping
    public String getUserList(Model model) {
        log.info("== 마을 주민 목록 조회 ==");
        List<User> userList = userService.getUserList();
        model.addAttribute("userList", userList);
        return "/user/userList";
    }



    /**
     * 유저 긴급호출 (테스트)
     * http://localhost:8080/user/emergency/1
     * (사전조건 - 유저랑 보호자랑 연결되있어야됨)
     */
    @GetMapping("/emergency/{userId}")
    public void doUserEmergencyAlert(@PathVariable Long userId) {
        User user = userService.findUserById(userId);
        alertService.createAlert(user);
    }
}
