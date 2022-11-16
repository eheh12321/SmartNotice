package sejong.smartnotice.controller.viewController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sejong.smartnotice.domain.AlertType;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.announce.Announce;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.service.EmergencyAlertService;
import sejong.smartnotice.service.UserService;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/u")
@RequiredArgsConstructor
public class UserPageController {

    private final EmergencyAlertService alertService;
    private final UserService userService;
    private final EntityManager em;

    @GetMapping("/login")
    public String userLogin() {
        log.info("== 마을 주민 로그인 ==");
        return "login";
    }

    @GetMapping
    public String index(Authentication auth, Model model) {
        User authUser = userService.findByLoginId(auth.getName());
        model.addAttribute("userName", authUser.getName());
        return "u/index";
    }

    @GetMapping("/announces/normal")
    public String getNormalAnnounceListPage(Authentication auth, Model model) {
        User authUser = userService.findByLoginId(auth.getName());
        Town town = authUser.getTown();
        List<Announce> announceList = em.createQuery("select distinct a from Announce a join fetch a.atList at where at.town.id=:townId", Announce.class)
                .setParameter("townId", town.getId())
                .getResultList();

        model.addAttribute("userName", authUser.getName());
        model.addAttribute("announceList", announceList);
        return "u/normalAnnounceList";
    }

    @GetMapping("/announces/emergency")
    public String getEmergencyAnnounceListPage(Authentication auth, Model model) {
        User authUser = userService.findByLoginId(auth.getName());
        Town town = authUser.getTown();
        List<Announce> announceList = em.createQuery("select distinct a from Announce a join fetch a.atList at where at.town.id=:townId", Announce.class)
                .setParameter("townId", town.getId())
                .getResultList();

        model.addAttribute("userName", authUser.getName());
        model.addAttribute("announceList", announceList);
        return "u/emergencyAnnounceList";
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<String> makeAlert(Authentication auth) {
        User authUser = userService.findByLoginId(auth.getName());
        alertService.createAlert(authUser, AlertType.USER);
        return ResponseEntity.ok().body("보호자에게 긴급알림을 전송하였습니다");
    }
}
