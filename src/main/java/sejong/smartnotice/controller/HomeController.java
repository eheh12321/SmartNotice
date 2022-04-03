package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.member.Member;
import sejong.smartnotice.dto.AdminDTO;
import sejong.smartnotice.dto.LoginDTO;
import sejong.smartnotice.dto.UserDTO;
import sejong.smartnotice.service.AdminService;
import sejong.smartnotice.service.UserService;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
@Controller
@RequestMapping
@RequiredArgsConstructor
public class HomeController {

    private final AdminService adminService;
    private final UserService userService;

    @GetMapping("/register")
    public void register() { }

    @GetMapping("/register/admin")
    public String registerAdminForm() {
        return "register/adminRegister";
    }

    @GetMapping("/register/user")
    public String registerUserForm() {
        return "register/userRegister";
    }
    
    // 관리자 회원가입
    @PostMapping("/register/admin")
    public String registerAdmin(
            @ModelAttribute LoginDTO loginDTO,
            @ModelAttribute AdminDTO adminDTO) {

        log.info("LoginDTO: {}", loginDTO);
        log.info("adminDTO: {}" ,adminDTO);

        adminService.register(adminDTO, loginDTO);
        return "redirect:/";
    }

    // 마을 주민 회원가입
    @PostMapping("/register/user")
    public String registerUser(
            @ModelAttribute UserDTO userDTO,
            @ModelAttribute LoginDTO loginDTO,
            @RequestParam Long townId) {

        log.info("adminDTO: {}" ,userDTO);
        log.info("LoginDTO: {}", loginDTO);
        log.info("townId: {}", townId);

        userService.register(userDTO, loginDTO, townId);

        return "redirect:/";
    }

    // 테스트용 전체 회원목록 조회
    private final EntityManager em;

    @GetMapping("/members")
    public String getMemberList(Model model) {
        List<Member> memberList = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        model.addAttribute("memberList", memberList);
        return "memberList";
    }
}
