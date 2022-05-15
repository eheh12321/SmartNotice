package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.member.Supporter;
import sejong.smartnotice.dto.SupporterModifyDTO;
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
        List<Supporter> supporterList = supporterService.findAllWithUser();
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
    public String modify(@PathVariable Long id, @Validated @ModelAttribute("supporter") SupporterModifyDTO modifyDTO,
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
