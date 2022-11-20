package sejong.smartnotice.controller.viewController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.*;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.service.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping
@RequiredArgsConstructor
public class HomeController {

    private final TownDataService townDataService;
    private final AdminService adminService;
    private final TownService townService;
    private final Environment env;

    @GetMapping("/profile")
    @ResponseBody
    public String profile() {
        List<String> profiles = Arrays.asList(env.getActiveProfiles()); // 현재 실행중인 ActiveProfile 조회 (real, oauth, real-db)
        List<String> realProfiles = Arrays.asList("real", "real1", "real2");
        String defaultProfile = profiles.isEmpty() ? "default" : profiles.get(0);

        return profiles.stream()
                .filter(realProfiles::contains)
                .findAny()
                .orElse(defaultProfile);
    }

    @GetMapping
    public String indexPage(@AuthenticationPrincipal Admin admin, Model model) {
        List<TownData> townDataList = new ArrayList<>();
        // 관리중인 마을 목록을 불러온 이후 해당 마을에 속하는 redis 데이터를 반환 (없으면 새로 생성)
        townService.findByAdmin(admin.getId())
                .forEach(town -> {
                    TownData townData = townDataService.findById(town.getId());
                    townDataList.add(townData);
                });
        model.addAttribute("townDataList", townDataList);
        return "index";
    }

    @GetMapping("/index")
    public String adminIndexPage() {
        return "admin_index";
    }

    @GetMapping("/edit")
    public String adminModifyPage(@AuthenticationPrincipal Admin admin, Model model) {
        // AuthenticationPrincipal을 그대로 사용하면 안됨 -> 세션에 저장되어있을 뿐이고 트랜잭션이 적용되지 않아 DB 반영 X
        Admin authAdmin = adminService.findById(admin.getId());
        List<Town> townList = townService.findAll(); // 전체 마을 목록
        List<Long> managedTownIdList = townService.findTownByAdmin(authAdmin).stream()
                .map(Town::getId)
                .collect(Collectors.toList()); // 관리하고 있는 마을 ID 목록

        model.addAttribute("admin", authAdmin);
        model.addAttribute("townList", townList);
        model.addAttribute("managedTownIdList", managedTownIdList);
        return "admin/modify";
    }

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("error") String errorMessage,
                            @ModelAttribute("logout") String logoutMessage,
                            @RequestParam(required = false) String domain,
                            String registerMessage, Model model) {
        log.info("== 사이트 로그인 == ");
        if (errorMessage != null && errorMessage.length() != 0) {
            model.addAttribute("errorMessage", errorMessage);
        }
        if (logoutMessage != null && logoutMessage.length() != 0) {
            model.addAttribute("logoutMessage", logoutMessage);
        }
        if (registerMessage != null && registerMessage.length() != 0) {
            model.addAttribute("registerMessage", registerMessage);
        }
        model.addAttribute("port", env.getProperty("server.port", "-"));
        model.addAttribute("domain", domain);
        return "login";
    }

    @PostMapping("/logout")
    public void logout() {
        log.info("== 로그아웃 ==");
    }

    @GetMapping("/lab")
    public String labPage() {
        return "lab";
    }

}
