package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.*;
import sejong.smartnotice.domain.announce.Announce;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.AdminType;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.dto.AdminRegisterDTO;
import sejong.smartnotice.dto.ComplexDTO;
import sejong.smartnotice.dto.TownModifyDTO;
import sejong.smartnotice.service.*;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping(value = "/towns", produces = MediaType.TEXT_HTML_VALUE)
@RequiredArgsConstructor
public class TownController {

    private final TownService townService;
    private final AdminService adminService;
    private final UserService userService;
    private final EntityManager em;

    @GetMapping("/{id}")
    public String getTownDetail(Authentication auth, @PathVariable Long id, Model model, HttpServletResponse response) throws IOException {
        log.info("== 마을 상세 조회 ==");

        // (1) 마을 조회
        Town town = townService.findById(id);

        // (2) 관리자 조회
        List<Admin_Town> atList = em.createQuery("select at from Admin_Town at join fetch at.admin where at.town=:town", Admin_Town.class)
                .setParameter("town", town)
                .getResultList();
        List<Admin> adminList = new ArrayList<>();
        int townAdminCnt = 0;
        for (Admin_Town at : atList) {
            Admin admin = at.getAdmin();
            if(admin.getType() == AdminType.ADMIN) {
                townAdminCnt++;
            }
            adminList.add(admin);
        }

        // (3) 관리자 권한 조회
        if(!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SUPER"))) {
            // 마을 관리자인 경우
            Admin authAdmin = adminService.findByLoginId(auth.getName());
            if(!adminList.contains(authAdmin)) {
                log.warn("마을 접근 권한이 없습니다!");
                response.sendError(403); // 403 Error page 이동
                return null;
            }
        }

        // (3) 방송 조회
        List<Announce_Town> antList = em.createQuery("select at from Announce_Town at join fetch at.announce where at.town=:town", Announce_Town.class)
                .setParameter("town", town)
                .getResultList();
        List<Announce> announceList = new ArrayList<>();
        for (Announce_Town at : antList) {
            announceList.add(at.getAnnounce());
        }

        // (4) 주민 + 긴급알림 + 단말기 조회
        List<User> userList = em.createQuery("select distinct u from User u left join fetch u.alertList left join fetch u.device where u.town=:town", User.class)
                .setParameter("town", town)
                .getResultList();

        int notConnectedCnt = 0;
        int mqttErrorCnt = 0;
        int sensorErrorCnt = 0;
        int userAlertCnt = 0;
        int fireAlertCnt = 0;
        int motionAlertCnt = 0;
        List<EmergencyAlert> alertList = new ArrayList<>();
        for (User user : userList) {
            for (EmergencyAlert alert : user.getAlertList()) {
                alertList.add(alert);
                if(alert.getAlertType() == AlertType.USER) {
                    userAlertCnt++;
                } else if (alert.getAlertType() == AlertType.FIRE) {
                    fireAlertCnt++;
                } else {
                    motionAlertCnt++;
                }
            }
            if(user.getDevice() != null) {
                if(user.getDevice().isError_sensor()) {
                    sensorErrorCnt++;
                }
                if(user.getDevice().isError_mqtt()) {
                    mqttErrorCnt++;
                }
            } else {
                notConnectedCnt++;
            }
        }

        ComplexDTO dto = ComplexDTO.builder()
                .town(town)
                .userList(userList)
                .adminList(adminList)
                .announceList(announceList)
                .alertList(alertList)
                .townAdminCnt(townAdminCnt)
                .alert_fire(fireAlertCnt)
                .alert_user(userAlertCnt)
                .alert_motion(motionAlertCnt)
                .status_notConnected(notConnectedCnt)
                .status_error_mqtt(mqttErrorCnt)
                .status_error_sensor(sensorErrorCnt).build();

        List<Region> regionList = em.createQuery("select r from Region r", Region.class).getResultList();

        model.addAttribute("dto", dto);
        model.addAttribute("regionList", regionList);
        model.addAttribute("town", new TownModifyDTO(town.getId(), town.getName(), town.getRegion().getRegionCode()));
        return "town/townDetail";
    }

    // 마을 관리자 목록 조회
    @GetMapping("/{id}/admin")
    public String getTownAdminList(@PathVariable Long id, Model model) {
        log.info("== 마을 관리자 목록 조회 ==");
        Town town = townService.findById(id);
        model.addAttribute("town", town);

        return "town/adminList";
    }

    @GetMapping("/{id}/admin/new")
    public String addTownAdminForm(@PathVariable Long id, Model model) {
        log.info("== 마을 관리자 신규 등록 ==");
        Town town = townService.findById(id);
        List<User> userList = town.getUserList();
        List<Admin> adminList = adminService.findNotTownAdmin(town);

        model.addAttribute("userList", userList);
        model.addAttribute("adminList", adminList);
        model.addAttribute("townId", id);

        return "town/townAdminRegister";
    }

    // 마을 관리자 등록(등록)
    @PostMapping("/{id}/admin/new")
    public String addTownAdmin(@PathVariable Long id, @RequestParam(required = false) Long userId, @RequestParam(required = false) Long adminId) {
        log.info("== 마을 관리자 등록 ==");

        if(userId != null && adminId == null) {
            User user = userService.findById(userId); // 주민 정보 조회
            AdminRegisterDTO registerDTO = new AdminRegisterDTO(user.getName(), user.getTel(),
                    user.getAccount().getLoginId(), user.getAccount().getLoginPw(), AdminType.ADMIN);
            Long newAdminId = adminService.register(registerDTO); // 주민 계정 정보로 관리자 계정 생성
            user.modifyUserIsAdmin(); // 마을 주민이 마을 관리자라는 상태 표시
            townService.addTownAdmin(id, newAdminId); // 관리자와 마을 연결
        } else if (userId == null && adminId != null) {
            townService.addTownAdmin(id, adminId); // 관리자와 마을 연결
        } else {
            log.warn("잘못된 요청입니다");
        }
        return "redirect:/towns/{id}";
    }

    // 마을 관리자 삭제
    @DeleteMapping("/{id}/admin")
    public String removeTownAdmin(@PathVariable Long id, @RequestParam Long adminId) {
        log.info("== 마을 관리자 삭제 ==");
        townService.removeTownAdmin(id, adminId);
        return "redirect:/towns/{id}";
    }
}
