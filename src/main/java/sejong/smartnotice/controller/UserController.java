package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.dto.UserDTO;
import sejong.smartnotice.service.EmergencyAlertService;
import sejong.smartnotice.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final EmergencyAlertService alertService;

    @GetMapping
    public String getUserList(Model model, @RequestParam(required = false) String name) {
        log.info("== 마을 주민 목록 조회 ==");
        List<User> userList;
        if(StringUtils.hasText(name)) {
            userList = userService.findByName(name);
        } else {
            userList = userService.findAll();
        }
        model.addAttribute("userList", userList);
        return "user/userList";
    }

    @GetMapping("/{id}")
    public String getUser(@PathVariable Long id, Model model, HttpServletRequest request) {
        log.info("== 마을 주민 조회 ==");
        User user = userService.findById(id);
        model.addAttribute("user", user);

        String referer = request.getHeader("Referer");
        log.info(referer);
        model.addAttribute("referer", referer);

        return "user/userDetail";
    }

    @GetMapping("/{id}/edit")
    public String modifyForm(@PathVariable Long id, Model model) {
        log.info("== 마을 주민 수정 ==");
        User user = userService.findById(id);
        model.addAttribute("user", user);

        return "user/modify";
    }

    @PutMapping("/{id}")
    public String modify(@PathVariable Long id, @ModelAttribute UserDTO userDTO) {
        log.info("== 마을 주민 정보 수정 ==");
        userService.modifyUserInfo(id, userDTO);

        return "redirect:/";
    }

    @DeleteMapping("/{id}")
    public String remove(@PathVariable Long id) {
        log.info("== 마을 주민 삭제 ==");
        userService.delete(id);
        return "redirect:/";
    }

    /**
     * 유저 긴급호출 (테스트)
     * http://localhost:8080/user/emergency/1
     * (사전조건 - 유저랑 보호자랑 연결되있어야됨)
     */
    @GetMapping("/emergency/{userId}")
    public void doUserEmergencyAlert(@PathVariable Long userId) {
        User user = userService.findById(userId);
        alertService.createAlert(user);
    }
}
