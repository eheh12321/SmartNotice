package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sejong.smartnotice.dto.AdminDTO;
import sejong.smartnotice.dto.LoginDTO;
import sejong.smartnotice.service.AdminService;

@Slf4j
@Controller
@RequestMapping
@RequiredArgsConstructor
public class HomeController {

    private final AdminService adminService;

    @GetMapping("/register")
    public void register() { }

    @GetMapping("/register/admin")
    public String registerAdmin() {
        return "adminRegister";
    }

    /**
     * 관리자 회원가입
     * http://localhost:8080/admin/register?name=name&type=ADMIN&tel=tel&loginId=id&loginPw=pw
     */
    @PostMapping("/register/admin")
    public String registerAdmin(
            @ModelAttribute LoginDTO loginDTO,
            @ModelAttribute AdminDTO adminDTO) {

        log.info("LoginDTO: {}", loginDTO);
        log.info("adminDTO: {}" ,adminDTO);

        adminService.register(adminDTO, loginDTO);
        return "redirect:/";
    }
}
