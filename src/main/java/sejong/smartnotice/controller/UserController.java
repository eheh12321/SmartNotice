package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.dto.UserModifyDTO;
import sejong.smartnotice.service.UserService;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public String getUserList(Model model, @RequestParam(required = false) String name) {
        log.info("== 마을 주민 목록 조회 ==");
        List<User> userList;
        if(StringUtils.hasText(name)) {
            userList = userService.findByName(name);
        } else {
            userList = userService.findAllWithTown();
        }
        model.addAttribute("userList", userList);
        return "user/list";
    }

    @GetMapping("/{id}")
    public String getUser(@PathVariable Long id, Model model) {
        log.info("== 마을 주민 조회 ==");
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("supporterList", user.getSupporterList());
        return "user/detail";
    }

    @GetMapping("/{id}/edit")
    public String modifyForm(@PathVariable Long id, Model model) {
        log.info("== 마을 주민 수정 ==");
        User user = userService.findById(id);
        model.addAttribute("user", user);

        return "user/modify";
    }

    @PutMapping("/{id}")
    public String modify(@PathVariable Long id, @Validated @ModelAttribute("user") UserModifyDTO modifyDTO,
                         BindingResult bindingResult) {
        log.info("== 마을 주민 정보 수정 ==");
        User findUser = userService.findByTel(modifyDTO.getTel());
        if(findUser != null && findUser.getId() != modifyDTO.getId()) {
            bindingResult.addError(new FieldError("user", "tel", modifyDTO.getTel(), false, null, null, "중복된 전화번호가 존재합니다"));
        }
        if(bindingResult.hasErrors()) {
            log.warn("검증 오류 발생: {}", bindingResult);
            return "user/modify";
        }
        userService.modifyUserInfo(modifyDTO);

        return "redirect:/";
    }

    @DeleteMapping("/{id}")
    public String remove(@PathVariable Long id) {
        log.info("== 마을 주민 삭제 ==");
        userService.delete(id);
        return "redirect:/";
    }
}
