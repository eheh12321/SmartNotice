package sejong.smartnotice.controller;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Pause;
import com.twilio.twiml.voice.Say;
import com.twilio.type.PhoneNumber;
import com.twilio.type.Twiml;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import sejong.smartnotice.domain.member.Supporter;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.dto.AdminRegisterDTO;
import sejong.smartnotice.dto.SupporterRegisterDTO;
import sejong.smartnotice.dto.UserRegisterDTO;
import sejong.smartnotice.service.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;
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
    private final MqttService mqttService;

    @GetMapping("/register")
    public String SelectRegisterAuthPage() {
        return "register/index";
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
    @PostMapping("/emergency/{userId}")
    public String makeEmergencyCall(@PathVariable Long userId) throws URISyntaxException {
        log.info("== Twilio 호출 ==");

        // 1. 전화 대상 조회
        User user = null;
        try {
            user = userService.findById(userId);
        } catch (NullPointerException e) {
            return "마을 주민이 존재하지 않습니다";
        }

        // 2. Twilio 초기화
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        String from = MY_TEL; // Twilio 가상 번호

        // 3. 주민과 연결된 보호자를 대상으로 모두 전화
        List<Supporter> supporterList = user.getSupporterList();
        for (Supporter supporter : supporterList) {
            log.info("보호자 이름: {}, 번호: {}에게 알림", supporter.getName(), supporter.getTel());
            // 형식에 맞게 전화번호 변경
            String originalTel = supporter.getTel();
            String localTelNum = "+82";
            String to = localTelNum.concat(originalTel.replace("-", "").substring(1));
            log.info("변환된 전화번호: {}", to);

            try {
                Pause pause = new Pause.Builder().length(2).build();
                Say say = new Say.Builder("스마트 마을 알림 시스템 긴급 알림입니다." + user.getName() + "님에게 긴급 상황이 발생했습니다.").voice(Say.Voice.POLLY_SEOYEON).build();
                Say endMessage = new Say.Builder("스마트 마을 알림 시스템 긴급 알림입니다." + user.getName() + "님에게 긴급 상황이 발생했습니다. 문장은 여기까지입니다.").voice(Say.Voice.POLLY_SEOYEON).build();
                VoiceResponse response = new VoiceResponse.Builder().say(say).pause(pause).say(say).pause(pause).say(endMessage).build();

                Call call = Call.creator(new PhoneNumber(to), new PhoneNumber(from), new Twiml(response.toXml())).create();
                log.info("성공!: {}", call.getSid());
            } catch (ApiException e) {
                e.printStackTrace();
                return "API 호출에 실패했습니다 (전화번호가 유효하지 않음)";
            }
        }
        return "SUCCESS";
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
