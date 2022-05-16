package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.dto.AdminRegisterDTO;
import sejong.smartnotice.dto.SupporterRegisterDTO;
import sejong.smartnotice.dto.UserRegisterDTO;
import sejong.smartnotice.service.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Controller
@RequestMapping
@RequiredArgsConstructor
public class HomeController {

    private final AdminService adminService;
    private final UserService userService;
    private final TownService townService;
    private final SupporterService supporterService;

    @GetMapping("/register")
    public String SelectRegisterAuthPage() {
        return "register/index";
    }


    @GetMapping("/login")
    public String loginForm(@ModelAttribute("error") String errorMessage,
                            @ModelAttribute("logout") String logoutMessage,
                            @RequestParam(required = false) String loginAuth,
                            String registerMessage, Model model) {
        log.info("== 사이트 로그인 == ");
        log.info("접속 시각: {}", LocalDateTime.now());
        log.info("접속 IP: {}", getUserIp());
        if(errorMessage != null && errorMessage.length() != 0) {
            model.addAttribute("errorMessage", errorMessage);
        }
        if(logoutMessage != null && logoutMessage.length() != 0) {
            model.addAttribute("logoutMessage", logoutMessage);
        }
        if(registerMessage != null && registerMessage.length() != 0) {
            model.addAttribute("registerMessage", registerMessage);
        }
        model.addAttribute("auth", loginAuth);
        return "login";
    }

    @PostMapping("/logout")
    public void logout() {
        log.info("== 로그아웃 ==");
    }

    @GetMapping("/register/admin")
    public String registerAdminForm(Model model) {
        model.addAttribute("admin", new AdminRegisterDTO());
        return "register/admin";
    }

    @PostMapping("/register/admin")
    public String registerAdmin(@Validated @ModelAttribute("admin") AdminRegisterDTO registerDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        log.info("<<중복 검증>>");
        if(adminService.findByLoginId(registerDTO.getLoginId()) != null) {
            bindingResult.addError(new FieldError("admin", "loginId", registerDTO.getLoginId(), false, null, null, "중복된 아이디가 존재합니다"));
        }
        if(adminService.findByTel(registerDTO.getTel()) != null) {
            bindingResult.addError(new FieldError("admin", "tel", registerDTO.getTel(), false, null, null, "중복된 전화번호가 존재합니다"));
        }
        if(bindingResult.hasErrors()) {
            log.warn("검증 오류 발생: {}", bindingResult);
            return "register/admin";
        }
        adminService.register(registerDTO);
        redirectAttributes.addFlashAttribute("registerMessage", "정상적으로 회원가입 되었습니다!");
        return "redirect:/login";
    }

    @GetMapping("/register/user")
    public String registerUserForm(Model model) {
        List<Town> townList = townService.findAll();
        model.addAttribute("townList", townList);
        model.addAttribute("user", new UserRegisterDTO());
        return "register/user";
    }

    @PostMapping("/register/user")
    public String registerUser(@Validated @ModelAttribute("user") UserRegisterDTO registerDTO, BindingResult bindingResult,
                               Model model, RedirectAttributes redirectAttributes) {
        if(userService.findByLoginId(registerDTO.getLoginId()) != null) {
            bindingResult.addError(new FieldError("user", "loginId", registerDTO.getLoginId(), false, null, null, "중복된 아이디가 존재합니다"));
        }
        if(userService.findByTel(registerDTO.getTel()) != null) {
            bindingResult.addError(new FieldError("user", "tel", registerDTO.getTel(), false, null, null, "중복된 전화번호가 존재합니다"));
        }
        if(bindingResult.hasErrors()) {
            log.warn("검증 오류 발생: {}", bindingResult);
            List<Town> townList = townService.findAll();
            model.addAttribute("townList", townList);
            return "register/user";
        }
        userService.register(registerDTO);
        redirectAttributes.addFlashAttribute("registerMessage", "정상적으로 회원가입 되었습니다!");
        return "redirect:/login?loginAuth=user";
    }

    @GetMapping("/register/supporter")
    public String registerSupporterForm(Model model) {
        model.addAttribute("supporter", new SupporterRegisterDTO());
        return "register/supporter";
    }

    @PostMapping("/register/supporter")
    public String registerSupporter(@Validated @ModelAttribute("supporter") SupporterRegisterDTO registerDTO,
                                    BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if(supporterService.findByLoginId(registerDTO.getLoginId()) != null) {
            bindingResult.addError(new FieldError("supporter", "loginId", registerDTO.getLoginId(), false, null, null, "중복된 아이디가 존재합니다"));
        }
        if(supporterService.findByTel(registerDTO.getTel()) != null) {
            bindingResult.addError(new FieldError("supporter", "tel", registerDTO.getTel(), false, null, null, "중복된 전화번호가 존재합니다"));
        }
        if(userService.findByTel(registerDTO.getUserTel()) == null) { // 전화번호에 해당하는 마을 주민이 없을 경우
            bindingResult.addError(new FieldError("supporter", "userTel", registerDTO.getUserTel(), false, null, null, "해당하는 마을 주민이 존재하지 않습니다"));
        }
        if(bindingResult.hasErrors()) {
            log.warn("검증 오류 발생: {}", bindingResult);
            return "register/supporter";
        }
        supporterService.register(registerDTO);
        redirectAttributes.addFlashAttribute("registerMessage", "정상적으로 회원가입 되었습니다!");
        return "redirect:/login?loginAuth=supporter";
    }

    public String getUserIp() {

        String ip = null;
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-RealIP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("REMOTE_ADDR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }
}
