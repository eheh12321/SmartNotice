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
import sejong.smartnotice.form.AdminRegisterForm;
import sejong.smartnotice.form.SupporterRegisterForm;
import sejong.smartnotice.service.AdminService;
import sejong.smartnotice.service.SupporterService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@Controller
@RequestMapping
@RequiredArgsConstructor
public class HomeController {

    private final AdminService adminService;

    @GetMapping("/login")
    public String adminLoginForm(String error, String logout, HttpServletRequest request, Model model) {
        log.info("== 관제 사이트 로그인 == ");
        log.info("접속 시각: {}", LocalDateTime.now());
        log.info("접속 IP: {}", getUserIp());
        log.info("error: {}", error);
        log.info("logout: {}", logout);

        if(error != null) {
            model.addAttribute("error", "Check your Account");
        }
        if(logout != null) {
            log.info("== 관리자 로그아웃 ==");
            model.addAttribute("logout", "Logout");
        }
        return "login";
    }

    @PostMapping("/logout")
    public void logout() {
        log.info("== 관리자 로그아웃 ==");
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("admin", new AdminRegisterForm());
        return "register";
    }
    
    // 관리자 회원가입
    @PostMapping("/register")
    public String register(@Validated @ModelAttribute("admin") AdminRegisterForm form,
                                BindingResult bindingResult) {
        if(adminService.findByLoginId(form.getLoginId()) != null) {
            bindingResult.addError(new FieldError("admin", "loginId", form.getLoginId(), false, null, null, "중복된 아이디가 존재합니다"));
        }
        if(adminService.findByTel(form.getTel()) != null) {
            bindingResult.addError(new FieldError("admin", "tel", form.getTel(), false, null, null, "중복된 전화번호가 존재합니다"));
        }
        if(bindingResult.hasErrors()) {
            log.warn("검증 오류 발생: {}", bindingResult);
            return "register";
        }
        adminService.register(form.getName(), form.getTel(), form.getLoginId(), form.getLoginPw(), form.getType());
        return "redirect:/";
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
