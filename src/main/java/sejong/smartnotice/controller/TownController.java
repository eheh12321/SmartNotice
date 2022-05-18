package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.*;
import sejong.smartnotice.domain.announce.Announce;
import sejong.smartnotice.domain.member.Account;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.AdminType;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.dto.AdminRegisterDTO;
import sejong.smartnotice.dto.ComplexDTO;
import sejong.smartnotice.dto.TownModifyDTO;
import sejong.smartnotice.dto.TownRegisterDTO;
import sejong.smartnotice.service.*;

import javax.persistence.EntityManager;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/towns")
@RequiredArgsConstructor
public class TownController {

    private final TownService townService;
    private final AdminService adminService;
    private final UserService userService;
    private final AnnounceService announceService;
    private final EmergencyAlertService emService;

    @GetMapping
    public String getTownList(Model model, @RequestParam(required = false) String name) {
        log.info("== 마을 목록 조회 ==");
        List<Town> townList;
        if(StringUtils.hasText(name)) {
            townList = townService.findByName(name);
        } else {
            townList = townService.findAll();
        }
        model.addAttribute("townList", townList);
        return "town/townList";
    }

    @GetMapping("/new")
    public String registerTown(String error, Model model) {
        if(error != null) {
            model.addAttribute("error", "동일한 지역에 중복된 마을이 존재합니다");
        }
        List<Region> regionList = townService.findAllRegion();
        model.addAttribute("regionList", regionList);
        model.addAttribute("town", new TownRegisterDTO());
        return "town/register";
    }

    @PostMapping
    public String register(@Validated @ModelAttribute("town") TownRegisterDTO registerDTO,
                           BindingResult bindingResult, Model model) {
        log.info("== 마을 등록 ==");
        if(registerDTO.getRegionCode() == 1L) {
            bindingResult.addError(new FieldError("town", "regionCode","마을을 선택해주세요"));
        }
        if(bindingResult.hasErrors()) {
            log.warn("검증 오류 발생: {}", bindingResult);

            // 지역 목록 다시 뽑아서 모델에 같이 넘긴다 (마땅한 방법이 없네..)
            List<Region> regionList = townService.findAllRegion();
            model.addAttribute("regionList", regionList);
            return "town/register";
        }
        try {
            townService.register(registerDTO);
            return "redirect:/towns";
        } catch (IllegalStateException e) {
            return "redirect:/towns/new?error";
        }
    }

    private final EntityManager em;

    @GetMapping("/{id}")
    public String getTownDetail(@PathVariable Long id, Model model) {
        log.info("== 마을 상세 조회 ==");

        // (1) 마을 조회
        Town town = townService.findById(id);

        // (2) 관리자 조회
        List<Admin_Town> atList = em.createQuery("select at from Admin_Town at join fetch at.admin where at.town=:town", Admin_Town.class)
                .setParameter("town", town)
                .getResultList();
        List<Admin> adminList = new ArrayList<>();
        for (Admin_Town at : atList) {
            adminList.add(at.getAdmin());
        }

        // (3) 방송 조회
        List<Announce_Town> antList = em.createQuery("select at from Announce_Town at join fetch at.announce where at.town=:town", Announce_Town.class)
                .setParameter("town", town)
                .getResultList();
        List<Announce> announceList = new ArrayList<>();
        for (Announce_Town at : antList) {
            announceList.add(at.getAnnounce());
        }

        // (4) 주민 + 긴급알림 조회
        List<User> userList = em.createQuery("select distinct u from User u left join fetch u.alertList where u.town=:town", User.class)
                .setParameter("town", town)
                .getResultList();

        List<EmergencyAlert> alertList = new ArrayList<>();
        for (User user : userList) {
            for (EmergencyAlert alert : user.getAlertList()) {
                alertList.add(alert);
            }
        }

        ComplexDTO dto = ComplexDTO.builder()
                .town(town)
                .userList(userList)
                .adminList(adminList)
                .announceList(announceList)
                .alertList(alertList)
                .alert_fire(0)
                .alert_user(alertList.size())
                .alert_motion(0)
                .status_ok(userList.size())
                .status_error(0)
                .status_emergency(0).build();

        model.addAttribute("dto", dto);
        return "town/newTownDetail";
    }

    @GetMapping("/{id}/edit")
    public String modify(@PathVariable Long id, String error, Model model) {
        log.info("== 마을 수정 ==");
        if(error != null) {
            model.addAttribute("error", "동일한 지역에 중복된 마을이 존재합니다");
        }
        Town town = townService.findById(id);
        List<Region> regionList = townService.findAllRegion();
        model.addAttribute("town", new TownModifyDTO(town.getId(), town.getName(), town.getRegion().getRegionCode()));
        model.addAttribute("regionList", regionList);
        return "town/modify";
    }

    @PutMapping("/{id}")
    public String modify(@Validated @ModelAttribute("town") TownModifyDTO modifyDTO,
                         BindingResult bindingResult, Model model) {
        log.info("== 마을 수정 ==");
        if(modifyDTO.getRegionCode() == 1L) {
            bindingResult.addError(new FieldError("town", "regionCode","마을을 선택해주세요"));
        }
        if(bindingResult.hasErrors()) {
            log.warn("검증 오류 발생: {}", bindingResult);
            // 마을 목록 다시 집어넣기
            List<Region> regionList = townService.findAllRegion();
            model.addAttribute("regionList", regionList);
            return "town/modify";
        }
        try {
            townService.modifyTownInfo(modifyDTO);
            return "redirect:/towns";
        } catch (IllegalStateException e) {
            return "redirect:/towns/{id}/edit?error";
        }
    }

    @DeleteMapping("/{id}")
    public String remove(@PathVariable Long id) {
        log.info("== 마을 삭제 ==");
        townService.delete(id);
        return "redirect:/towns";
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

        return "town/userList";
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
