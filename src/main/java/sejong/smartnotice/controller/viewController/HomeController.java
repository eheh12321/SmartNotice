package sejong.smartnotice.controller.viewController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sejong.smartnotice.domain.*;
import sejong.smartnotice.domain.announce.Announce;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.AdminType;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.helper.dto.ComplexDTO;
import sejong.smartnotice.service.*;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping
@RequiredArgsConstructor
public class HomeController {

    private final AdminService adminService;
    private final TownService townService;
    private final EntityManager em;
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

    @GetMapping("/lab")
    public String labPage() {
        return "lab";
    }

    /**
     * 대체 이 코드를 어떻게 효율적으로 만들 수 있을까.. 스파게티 그 자체
     */
    @GetMapping
    public String indexPage(@AuthenticationPrincipal Admin authAdmin, Model model) {

        List<Town> townList;
        List<Admin> adminList;
        List<User> userList;
        List<Announce> announceList;
        List<EmergencyAlert> alertList;

        boolean navbar_userAlert = false;
        boolean navbar_fireAlert = false;

        // 최고 관리자는 전체 마을을 모두 조회할 수 있어야 한다
        if (authAdmin.getType() == AdminType.SUPER) {
            log.info("# 권한: 최고 관리자=============");
            // (1) 마을 조회
            townList = townService.findAll();
            // (2) 마을 + 관리자 fetch
            adminList = em.createQuery("select distinct a from Admin a join fetch a.townAdminList at join fetch at.town", Admin.class)
                    .getResultList();
            // (3) 마을 + 주민 + 단말기 + 긴급알림 fetch
            userList = em.createQuery("select distinct u from User u left join fetch u.alertList left join fetch u.device", User.class)
                    .getResultList();
            // (4) 마을 + 방송 fetch
            announceList = em.createQuery("select distinct a from Announce a join fetch a.atList at join fetch at.town order by a.time desc", Announce.class)
                    .getResultList();

            // (5) 마을 추가에 들어가는 지역 목록
            List<Region> regionList = em.createQuery("select r from Region r", Region.class).getResultList();
            model.addAttribute("regionList", regionList);

            alertList = em.createQuery("select a from EmergencyAlert a", EmergencyAlert.class).getResultList();
            log.info("쿼리 종료 ===================");

        }
        // 마을 관리자는 본인이 관리하는 마을만 접근/조회할 수 있어야 한다
        else {
            log.info("# 권한: 마을 관리자===========");
            // (2) 마을 조회
            townList = em.createQuery("select distinct t from Town t join fetch t.region left join t.townAdminList at where at.admin.id=:adminId", Town.class)
                    .setParameter("adminId", authAdmin.getId())
                    .getResultList();
            // (3) 마을 + 관리자 fetch
            adminList = em.createQuery("select distinct a from Admin a join fetch a.townAdminList at join fetch at.town", Admin.class)
                    .getResultList();
            // (4) 마을 + 주민 + 단말기 + 긴급알림 fetch
            userList = em.createQuery("select distinct u from User u left join fetch u.alertList left join fetch u.device", User.class)
                    .getResultList();
            // (5) 마을 + 방송 fetch
            announceList = em.createQuery("select distinct a from Announce a join fetch a.atList at join fetch at.town order by a.time desc", Announce.class)
                    .getResultList();

            alertList = em.createQuery("select a from EmergencyAlert a", EmergencyAlert.class).getResultList();
            log.info("쿼리 종료 ===================");
        }

        List<ComplexDTO> complexDTOList = new ArrayList<>();
        for (Town town : townList) {

            List<Admin> al = new ArrayList<>();
            int townAdminCnt = 0;
            for (Admin admin : adminList) {
                for (TownAdmin at : admin.getTownAdminList()) {
                    if (at.getTown().equals(town)) {
                        al.add(admin);
                        if (admin.getType() == AdminType.ADMIN) {
                            townAdminCnt++;
                        }
                    }
                }
            }

            List<Announce> aal = new ArrayList<>();
            for (Announce announce : announceList) {
                for (Announce_Town at : announce.getAtList()) {
                    if (at.getTown().equals(town)) {
                        aal.add(announce);
                    }
                }
            }

            int userAlertCnt = 0;
            int fireAlertCnt = 0;
            int motionAlertCnt = 0;
            List<EmergencyAlert> el = new ArrayList<>();

            int mqttErrorCnt = 0;
            int sensorErrorCnt = 0;
            int notConnectedCnt = 0;
            boolean fireAlertStatus = false;
            boolean notConfirmedAlertStatus = false;
            List<User> ul = new ArrayList<>();
            for (User user : userList) {
                if (user.getTown().equals(town)) {
                    ul.add(user);
                    // 긴급호출 목록 내림차순 정렬
                    List<EmergencyAlert> sortedList = user.getAlertList();
                    sortedList.sort(new Comparator<EmergencyAlert>() {
                        @Override
                        public int compare(EmergencyAlert o1, EmergencyAlert o2) {
                            return o2.getAlertTime().compareTo(o1.getAlertTime());
                        }
                    });
                    for (EmergencyAlert alert : sortedList) {
                        if (alert.getAlertType() == AlertType.USER) {
                            userAlertCnt++;
                        } else if (alert.getAlertType() == AlertType.FIRE) {
                            fireAlertCnt++;
                        } else {
                            motionAlertCnt++;
                        }
                        if (!alert.isConfirmed()) {
                            el.add(alert);
                            notConfirmedAlertStatus = true;
                            navbar_userAlert = true;
                        }
                    }
                    if (user.getDevice() != null) {
                        if (user.getDevice().isError_sensor()) {
                            sensorErrorCnt++;
                        }
                        if (user.getDevice().isError_mqtt()) {
                            mqttErrorCnt++;
                        }
                        if (user.getDevice().isEmergency_fire()) {
                            fireAlertStatus = true;
                            navbar_fireAlert = true;
                        }
                    } else {
                        notConnectedCnt++;
                    }
                }
            }

            ComplexDTO dto = ComplexDTO.builder()
                    .town(town)
                    .userList(ul)
                    .adminList(al)
                    .announceList(aal)
                    .alertList(el)
                    .townAdminCnt(townAdminCnt)
                    .emergency_fire(fireAlertStatus)
                    .notConfirmedAlert(notConfirmedAlertStatus)
                    .alert_fire(fireAlertCnt)
                    .alert_user(userAlertCnt)
                    .alert_motion(motionAlertCnt)
                    .status_notConnected(notConnectedCnt)
                    .status_error_sensor(sensorErrorCnt)
                    .status_error_mqtt(mqttErrorCnt).build();
            complexDTOList.add(dto);
        }

        model.addAttribute("navbar_userAlert", navbar_userAlert);
        model.addAttribute("navbar_fireAlert", navbar_fireAlert);
        model.addAttribute("dtoList", complexDTOList);

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

    @GetMapping("/register")
    public String SelectRegisterAuthPage() {
        return "register/index";
    }


    @GetMapping("/login")
    public String loginForm(@ModelAttribute("error") String errorMessage,
                            @ModelAttribute("logout") String logoutMessage,
                            @RequestParam(required = false) String domain,
                            String registerMessage, Model model) {
        log.info("== 사이트 로그인 == ");
        log.info("접속 시각: {}", LocalDateTime.now());
        log.info("접속 IP: {}", getUserIp());
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
