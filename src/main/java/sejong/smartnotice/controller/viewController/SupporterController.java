package sejong.smartnotice.controller.viewController;

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
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.Supporter;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.helper.dto.SupporterModifyDTO;
import sejong.smartnotice.service.AdminService;
import sejong.smartnotice.service.SupporterService;
import sejong.smartnotice.service.TownService;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/supporters")
@RequiredArgsConstructor
public class SupporterController {

    private final SupporterService supporterService;
    private final AdminService adminService;
    private final TownService townService;
    private final EntityManager em;

    @GetMapping
    public String getSupporterList(Authentication auth, Model model, @RequestParam(required = false) String name) {
        log.info("== 보호자 목록 조회 ==");
        List<Supporter> supporterList;

        if(!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SUPER"))) {
            // 마을 관리자인 경우 관리 마을 대상으로 한 주민 목록만 조회
            Admin authAdmin = adminService.findByLoginId(auth.getName());
            List<Town> managedTownList = townService.findTownByAdmin(authAdmin);
            List<User> userList = em.createQuery("select u from User u join fetch u.town where u.town in(:townList)", User.class)
                    .setParameter("townList", managedTownList)
                    .getResultList();
            supporterList = em.createQuery("select s from Supporter s join fetch s.user u where u in(:userList)", Supporter.class)
                    .setParameter("userList", userList)
                    .getResultList();
        } else {
            supporterList = supporterService.findAllWithUser();
        }
        model.addAttribute("supporterList", supporterList);
        return "supporter/list";
    }

    @GetMapping("/{id}")
    public String getSupporter(@PathVariable Long id, Model model) {
        log.info("== 보호자 상제 조회 ==");
        Supporter supporter = supporterService.findById(id);
        model.addAttribute("supporter", supporter);
        return "supporter/detail";
    }

    @GetMapping("/{id}/edit")
    public String modifyForm(@PathVariable Long id, Model model) {
        log.info("== 보호자 수정 ==");
        Supporter supporter = supporterService.findById(id);
        model.addAttribute("supporter", supporter);
        return "supporter/modify";
    }

    @PutMapping
    public String modify(@Validated @ModelAttribute("supporter") SupporterModifyDTO modifyDTO,
                         BindingResult bindingResult) {
        log.info("== 관리자 정보 수정 ==");
        Supporter findSupporter = supporterService.findByTel(modifyDTO.getTel());
        if(findSupporter != null && findSupporter.getId() != modifyDTO.getId()) {
            bindingResult.addError(new FieldError("supporter", "tel", modifyDTO.getTel(), false, null, null, "중복된 전화번호가 존재합니다"));
        }
        if(bindingResult.hasErrors()) {
            log.warn("검증 오류 발생: {}", bindingResult);
            return "supporter/modify";
        }
        supporterService.modifySupporterInfo(modifyDTO);
        return "redirect:/supporters";
    }

    @DeleteMapping("/{id}")
    public String remove(@PathVariable Long id) {
        log.info("== 보호자 삭제 ==");
        supporterService.delete(id);

        return "redirect:/supporters";
    }
}
