package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.AlertType;
import sejong.smartnotice.domain.EmergencyAlert;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.device.Device;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.dto.AlertReceiveDTO;
import sejong.smartnotice.service.*;

import javax.persistence.EntityManager;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class EmergencyAlertController {

    private final DeviceService deviceService;
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

    @PostMapping("/{id}")
    public String receiveAlertConfirm(@PathVariable Long id) {
        alertService.alertConfirm(id);
        return "redirect:/";
    }

    @PostMapping("/api")
    @ResponseBody
    @Transactional
    public ResponseEntity<String> receiveAlerts(@ModelAttribute AlertReceiveDTO alertDTO) {
        Map<Long, Long> map = new HashMap<>();
        List<Long> newList = alertDTO.getFire();
        log.info("newList: {}", newList);
        List<Long> oldList = deviceService.findByEmergency_fireIsTrue();
        log.info("oldList: {}", oldList);
        // oldList??? Map??? ????????? ??????
        for (Long deviceId : oldList) {
            map.put(deviceId, 1L);
        }
        for (Long deviceId : newList) {
            if(map.containsKey(deviceId)) {
                // ?????? ?????? ??????
                map.remove(deviceId);
            } else {
                // ?????? ?????? ??????
                Device device = deviceService.findDeviceById(deviceId);
                device.setDeviceFireEmergencyStatus(true);
                alertService.createAlert(device.getUser(), AlertType.FIRE);
            }
        }
        // ?????? ?????? ??????
        for (Long key : map.keySet()) {
            Device device = deviceService.findDeviceById(key);
            device.setDeviceFireEmergencyStatus(false);
        }

        return ResponseEntity.ok().body("?????? ??????");
    }
}
