package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.dto.LoginDTO;
import sejong.smartnotice.dto.SupporterDTO;
import sejong.smartnotice.domain.member.Supporter;
import sejong.smartnotice.service.SupporterService;

@Slf4j
@RestController
@RequestMapping("/supporter")
@RequiredArgsConstructor
public class SupporterController {

    private final SupporterService supporterService;

    /**
     * 보호자 회원가입
     * http://localhost:8080/supporter/register?name=name&tel=tel&loginId=id&loginPw=pw
     */
    @PostMapping("/register")
    public void register(
            @ModelAttribute LoginDTO loginDTO,
            @ModelAttribute SupporterDTO supporterDTO) {

        supporterService.register(supporterDTO.getName(), supporterDTO.getTel(), loginDTO.getLoginId(), loginDTO.getLoginPw());
    }

    /**
     * 보호자랑 회원 연결
     * http://localhost:8080/supporter/select/1?userId=1
     * (추가 고려사항)
     * 연결 조건 추가 --> 보호자가 생판 남이랑 막 연결되면 안됨.. 뭔가 인증이 필요
     * 보호자가 뭘 어디까지 수 있나? - 단말기 상태조회
     * 회원가입 하지 않은 보호자에게도 연락이 갈 수 있도록 하는것이 좋아보임
     */
    @PostMapping("/select/{id}")
    public void selectUser(@PathVariable Long id, @RequestParam Long userId) {
        Supporter supporter = supporterService.findSupporterById(id);
        supporterService.connectWithUser(userId, supporter);
    }
}
