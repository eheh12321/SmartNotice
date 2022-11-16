package sejong.smartnotice.controller.viewController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.helper.dto.request.register.AdminRegisterDTO;
import sejong.smartnotice.helper.dto.request.register.SupporterRegisterDTO;
import sejong.smartnotice.helper.dto.request.register.UserRegisterDTO;
import sejong.smartnotice.helper.validator.UserAccountRegisterValidator;
import sejong.smartnotice.service.AdminService;
import sejong.smartnotice.service.SupporterService;
import sejong.smartnotice.service.TownService;
import sejong.smartnotice.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/register")
@Controller
public class RegisterController {

    private final AdminService adminService;
    private final UserService userService;
    private final SupporterService supporterService;
    private final TownService townService;

    private final UserAccountRegisterValidator userAccountRegisterValidator;

    @InitBinder
    protected void initBinder(WebDataBinder dataBinder) {
        dataBinder.addValidators(userAccountRegisterValidator);
    }

    @GetMapping("/admin")
    public String registerAdminForm(Model model) {
        model.addAttribute("admin", new AdminRegisterDTO());
        return "register/admin";
    }

    @PostMapping("/admin")
    public String registerAdmin(@Valid @ModelAttribute("admin") AdminRegisterDTO registerDTO,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.info("관리자 회원가입 검증 실패 -> {}", bindingResult);
            return "register/admin";
        }
        adminService.register(registerDTO);
        redirectAttributes.addFlashAttribute("registerMessage", "정상적으로 회원가입 되었습니다!");
        return "redirect:/login";
    }

    @GetMapping("/user")
    public String registerUserForm(Model model) {
        List<Town> townList = townService.findAll();
        model.addAttribute("townList", townList);
        model.addAttribute("user", new UserRegisterDTO());
        return "register/user";
    }

    @PostMapping("/user")
    public String registerUser(@Valid @ModelAttribute("user") UserRegisterDTO registerDTO,
                               BindingResult bindingResult,
                               Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.info("마을 주민 회원가입 검증 실패 -> {}", bindingResult);
            List<Town> townList = townService.findAll();
            model.addAttribute("townList", townList);
            return "register/user";
        }
        userService.register(registerDTO);
        redirectAttributes.addFlashAttribute("registerMessage", "정상적으로 회원가입 되었습니다!");
        return "redirect:/login?domain=user";
    }

    @GetMapping("/supporter")
    public String registerSupporterForm(Model model) {
        model.addAttribute("supporter", new SupporterRegisterDTO());
        return "register/supporter";
    }

    @PostMapping("/supporter")
    public String registerSupporter(@Valid @ModelAttribute("supporter") SupporterRegisterDTO registerDTO,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.info("보호자 회원가입 검증 실패 -> {}", bindingResult);
            return "register/supporter";
        }
        supporterService.register(registerDTO);
        redirectAttributes.addFlashAttribute("registerMessage", "정상적으로 회원가입 되었습니다!");
        return "redirect:/login?domain=supporter";
    }
}
