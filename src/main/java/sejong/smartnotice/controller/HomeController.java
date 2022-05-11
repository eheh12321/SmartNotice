package sejong.smartnotice.controller;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Pause;
import com.twilio.twiml.voice.Say;
import com.twilio.type.PhoneNumber;
import com.twilio.type.Twiml;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sejong.smartnotice.config.MqttConfig;
import sejong.smartnotice.dto.AdminRegisterDTO;
import sejong.smartnotice.dto.MqttInboundDTO;
import sejong.smartnotice.handler.AdminAuthenticationFailureHandler;
import sejong.smartnotice.service.AdminService;
import sejong.smartnotice.service.MqttService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.MessageHandler;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Controller
@RequestMapping
@RequiredArgsConstructor
public class HomeController {

    private final AdminService adminService;
    private final MqttService mqttService;

    @GetMapping("/register")
    public String SelectRegisterAuthPage() {
        return "register-authority";
    }

    @Value("${twilio.sid}")
    private String ACCOUNT_SID;

    @Value("${twilio.token}")
    private String AUTH_TOKEN;

    @Value("${twilio.myTel}")
    private String MY_TEL;

    @Value("${twilio.verifiedToTel}")
    private String TO_TEL;

    @ResponseBody
    @PostMapping("/test-twilio")
    public String testTwilioPage() throws URISyntaxException {
        log.info("== Twilio 호출 ==");
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        String from = MY_TEL;
        String to = TO_TEL;

        Pause pause = new Pause.Builder().length(2).build();
        Say say1 = new Say.Builder("안녕하세요. 테스트 문장입니다. 이 문장은 두번 반복 재생됩니다.").voice(Say.Voice.POLLY_SEOYEON).build();
        Say say2 = new Say.Builder("안녕하세요. 테스트 문장입니다. 문장이 끝나면 통화가 종료됩니다. 안녕히 계세요").voice(Say.Voice.POLLY_SEOYEON).build();
        VoiceResponse response = new VoiceResponse.Builder().say(say1).pause(pause).say(say2).build();

        Call call = Call.creator(new PhoneNumber(to), new PhoneNumber(from), new Twiml(response.toXml())).create();

        System.out.println(call.getSid());
        return "SUCCESS";
    }

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("error") String errorMessage,
                            @ModelAttribute("logout") String logoutMessage,
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
        return "login";
    }

    @PostMapping("/logout")
    public void logout() {
        log.info("== 관리자 로그아웃 ==");
    }

    @GetMapping("/register/admin")
    public String registerAdminForm(Model model) {
        model.addAttribute("admin", new AdminRegisterDTO());
        return "register-admin";
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
            return "register-admin";
        }
        adminService.register(registerDTO);
        redirectAttributes.addFlashAttribute("registerMessage", "정상적으로 회원가입 되었습니다!");
        return "redirect:/login";
    }

    @GetMapping("/register/user")
    public String registerUserForm() {
        return "register-user";
    }

    @PostMapping("/register/user")
    public String registerUser(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("registerMessage", "정상적으로 회원가입 되었습니다!");
        return "redirect:/login";
    }

    @GetMapping("/register/supporter")
    public String registerSupporterForm() {
        return "register-supporter";
    }

    @PostMapping("/register/supporter")
    public String registerSupporter(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("registerMessage", "정상적으로 회원가입 되었습니다!");
        return "redirect:/login";
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
