package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.member.Supporter;
import sejong.smartnotice.form.AdminModifyForm;
import sejong.smartnotice.form.SupporterModifyForm;
import sejong.smartnotice.service.SupporterService;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/supporters")
@RequiredArgsConstructor
public class SupporterController {

    private final SupporterService supporterService;

    @GetMapping
    public String getSupporterList(Model model, @RequestParam(required = false) String name) {
        log.info("== 보호자 목록 조회 ==");
        List<Supporter> supporterList = supporterService.findAll();
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

    @PutMapping("/{id}")
    public String modify(@PathVariable Long id, @Validated @ModelAttribute("supporter") SupporterModifyForm form,
                         BindingResult bindingResult) {
        log.info("== 관리자 정보 수정 ==");
        if(bindingResult.hasErrors()) {
            log.warn("검증 오류 발생: {}", bindingResult);
            return "supporter/modify";
        }
        supporterService.modifySupporterInfo(id, form.getName(), form.getTel());
        return "redirect:/supporters";
    }

    @DeleteMapping("/{id}")
    public String remove(@PathVariable Long id) {
        log.info("== 보호자 삭제 ==");
        supporterService.delete(id);

        return "redirect:/supporters";
    }
}
