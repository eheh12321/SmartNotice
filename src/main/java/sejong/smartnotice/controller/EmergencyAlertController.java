package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sejong.smartnotice.domain.EmergencyAlert;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.service.AdminService;
import sejong.smartnotice.service.EmergencyAlertService;
import sejong.smartnotice.service.TownService;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class EmergencyAlertController {

    private final AdminService adminService;
    private final TownService townService;
    private final EmergencyAlertService alertService;
    private final EntityManager em;

    @GetMapping
    public String getList(Authentication auth, Model model) {
        List<EmergencyAlert> alertList;
        if(!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SUPER"))) {
            Admin authAdmin = adminService.findByLoginId(auth.getName());
            List<Town> townList = townService.findTownByAdmin(authAdmin);
            alertList = em.createQuery("select distinct ea from EmergencyAlert ea join fetch ea.user u join fetch u.town t where t in(:townList)", EmergencyAlert.class)
                    .setParameter("townList", townList)
                    .getResultList();
        } else {
            alertList = alertService.findAllWithUser();
        }
        model.addAttribute("alertList", alertList);
        return "alert/list";
    }
}
