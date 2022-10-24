package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.device.Device;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.dto.UserModifyDTO;
import sejong.smartnotice.service.AdminService;
import sejong.smartnotice.service.TownService;
import sejong.smartnotice.service.UserService;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final AdminService adminService;
    private final UserService userService;
    private final TownService townService;
    private final EntityManager em;

    @GetMapping
    public String getUserList(Authentication auth, Model model, @RequestParam(required = false) String name) {
        log.info("== 마을 주민 목록 조회 ==");
        List<User> userList;
        if(!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SUPER"))) {
            // 마을 관리자인 경우 관리 마을 대상으로 한 주민 목록만 조회
            Admin authAdmin = adminService.findByLoginId(auth.getName());
            List<Town> managedTownList = townService.findTownByAdmin(authAdmin);
            userList = em.createQuery("select distinct u from User u join fetch u.supporterList s join fetch u.town where u.town in(:townList)", User.class)
                    .setParameter("townList", managedTownList)
                    .getResultList();
        } else {
            userList = em.createQuery("select distinct u from User u left join fetch u.supporterList s join fetch u.town", User.class)
                    .getResultList();
        }
        model.addAttribute("userList", userList);
        return "user/list";
    }

    @GetMapping("/{id}")
    public String getUser(@PathVariable Long id, Model model) {
        log.info("== 마을 주민 조회 ==");
        User user = userService.findById(id);
        Device device = user.getDevice();

        List<Device> deviceList = em.createQuery("select d from Device d", Device.class).getResultList();
        model.addAttribute("user", user);
        model.addAttribute("device", device);
        model.addAttribute("supporterList", user.getSupporterList());
        model.addAttribute("alertList", user.getAlertList());
        model.addAttribute("deviceList", deviceList);
        return "user/detail";
    }


    @PutMapping
    public ResponseEntity<String> modify(@ModelAttribute UserModifyDTO modifyDTO) {
        log.info("== 마을 주민 정보 수정 ==");
        userService.modifyUserInfo(modifyDTO);
        return ResponseEntity.ok().body("수정을 완료했습니다");
    }

    @PostMapping("/{id}/device")
    public String modifyUserDevice(@PathVariable Long id, @RequestParam Long deviceId) {
        log.info("== 마을 주민 단말기 수정 ==");
        userService.modifyUserDevice(id, deviceId);
        return "redirect:/users/" + id;
    }

    @DeleteMapping("/{id}")
    public String remove(@PathVariable Long id) {
        log.info("== 마을 주민 삭제 ==");
        userService.delete(id);
        return "redirect:/";
    }
}
