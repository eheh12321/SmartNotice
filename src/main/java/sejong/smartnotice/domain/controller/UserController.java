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
     * (추가 고려사항)
     * 보호자 인증은 어떻게 할까?
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

    /**
     * 유저랑 마을 연결
     * http://localhost:8080/user/select/1?townId=1
     * (마을 추가는 관리자가 함!!) 유저는 이미 생성되어있는 마을 중에서 선택
     */
    @PostMapping("/select/{id}")
    public void selectTown(@PathVariable Long id, @RequestParam Long townId) {
        User user = userService.findById(id);
        userService.selectTown(townId, user);
    }

    /**
     * 유저 긴급호출 (테스트)
     * http://localhost:8080/user/emergency/1
     * (사전조건 - 유저랑 보호자랑 연결되있어야됨)
     */
    @GetMapping("/emergency/{id}")
    public void 유저긴급호출테스트용(@PathVariable Long id) {
        User user = userService.findById(id);
        userService.makeEmergencyAlert(user);
    }
}
