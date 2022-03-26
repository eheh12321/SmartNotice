package sejong.smartnotice.domain.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.Device;
import sejong.smartnotice.domain.dto.LoginDTO;
import sejong.smartnotice.domain.dto.UserDTO;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.service.UserService;

@Slf4j
@RestController // 프론트 화면 구성되면 일반 Controller로 바꾸기
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 유저 회원가입
     * - 회원가입 창에서 아이디, 비밀번호, 유저 정보 입력 (마을정보, 보호자정보 제외)
     * - 회원 가입하면 연결된 단말기도 같이 생성해야됨
         * http://localhost:8080/user/register?name=name&address=address&tel=tel&loginId=id&loginPw=pw
     */
    @PostMapping("/register")
    public void register (
            @ModelAttribute LoginDTO loginDTO,
            @ModelAttribute UserDTO userDTO) {

        User user = User.builder()
                .loginId(loginDTO.getLoginId())
                .loginPw(loginDTO.getLoginPw())
                .name(userDTO.getName())
                .address(userDTO.getAddress())
                .tel(userDTO.getTel()).build();
        
        userService.register(user);
    }

    // http://localhost:8080/user/emergency/1
    @GetMapping("/emergency/{id}")
    public void 유저긴급호출테스트용(@PathVariable Long id) {
        User user = userService.findById(id);
        userService.makeEmergencyAlert(user);
    }
}
