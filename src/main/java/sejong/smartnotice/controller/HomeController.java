package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sejong.smartnotice.domain.*;
import sejong.smartnotice.domain.announce.Announce;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.AdminType;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.dto.*;
import sejong.smartnotice.service.*;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Controller
@RequestMapping
@RequiredArgsConstructor
public class HomeController {

    private final AdminService adminService;
    private final UserService userService;
    private final TownService townService;
    private final SupporterService supporterService;
    private final EmergencyAlertService emService;
    private final AnnounceService announceService;
    private final EntityManager em;

    @GetMapping
    public String indexPage(Authentication auth, Model model) {

        List<Town> townList;
        List<Admin> adminList;
        List<User> userList;
        List<Announce> announceList;
        List<EmergencyAlert> alertList;

        // 최고 관리자는 전체 마을을 모두 조회할 수 있어야 한다
        if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SUPER"))) {
            log.info("# 권한: 최고 관리자=============");
            // (1) 마을 조회
            townList = townService.findAll();
            // (2) 마을 + 관리자 fetch
            adminList = em.createQuery("select distinct a from Admin a join fetch a.atList at join fetch at.town", Admin.class)
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
            // (1) 관리자 조회
            Admin admin = adminService.findByLoginId(auth.getName());
            // (2) 마을 조회
            townList = em.createQuery("select distinct t from Town t join fetch t.region left join t.atList at where at.admin.id=:adminId", Town.class)
                    .setParameter("adminId", admin.getId())
                    .getResultList();
            // (3) 마을 + 관리자 fetch
            adminList = em.createQuery("select distinct a from Admin a join fetch a.atList at join fetch at.town", Admin.class)
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
                for (Admin_Town at : admin.getAtList()) {
                    if(at.getTown().equals(town)) {
                        al.add(admin);
                        if(admin.getType() == AdminType.ADMIN) {
                            townAdminCnt++;
                        }
                    }
                }
            }

            List<Announce> aal = new ArrayList<>();
            for (Announce announce : announceList) {
                for (Announce_Town at : announce.getAtList()) {
                    if(at.getTown().equals(town)) {
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
            List<User> ul = new ArrayList<>();
            for (User user : userList) {
                if(user.getTown().equals(town)) {
                    ul.add(user);
                    // 긴급호출 목록 내림차순 정렬
                    List<EmergencyAlert> sortedList = user.getAlertList();
                    sortedList.sort(new Comparator<EmergencyAlert>() {
                        @Override
                        public int compare(EmergencyAlert o1, EmergencyAlert o2) {
                            return Long.valueOf(o2.getId() - o1.getId()).intValue();
                        }
                    });
                    for (EmergencyAlert alert : sortedList) {
                        el.add(alert);
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
                        if(user.getDevice().isEmergency_fire()) {
                            fireAlertStatus = true;
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
                    .alert_fire(fireAlertCnt)
                    .alert_user(userAlertCnt)
                    .alert_motion(motionAlertCnt)
                    .status_notConnected(notConnectedCnt)
                    .status_error_sensor(sensorErrorCnt)
                    .status_error_mqtt(mqttErrorCnt).build();
            complexDTOList.add(dto);
        }

        model.addAttribute("dtoList", complexDTOList);

        return "index";
    }

    @GetMapping("/index")
    public String adminIndexPage() {
        return "admin_index";
    }

    @GetMapping("/edit")
    public String adminModifyPage(Authentication auth, Model model) {
        Admin authAdmin = adminService.findByLoginId(auth.getName());
        List<Town> townList = townService.findAll();
        List<Town> managedTownList = townService.findTownByAdmin(authAdmin);
        List<Long> managedTownIdList = new ArrayList<>();
        for (Town town : managedTownList) {
            managedTownIdList.add(town.getId());
        }
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
