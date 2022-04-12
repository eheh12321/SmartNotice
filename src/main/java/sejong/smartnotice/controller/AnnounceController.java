package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.announce.Announce;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.dto.AnnounceDTO;
import sejong.smartnotice.service.AdminService;
import sejong.smartnotice.service.AnnounceService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/announces")
public class AnnounceController {

    private final AnnounceService announceService;
    private final AdminService adminService;

    @GetMapping
    public String getAnnounceList(Model model) {
        List<Announce> announceList = announceService.findAllAnnounce();
        model.addAttribute("announceList", announceList);
        return "announce/list";
    }

    @GetMapping("/text")
    public String getTextAnnounceForm(@AuthenticationPrincipal Admin admin, Model model) {
        model.addAttribute("admin", admin);
        model.addAttribute("townList", adminService.getAdminTownList(admin));
        return "announce/textAnnounce";
    }

    @PostMapping("/text")
    public String getTextAnnounce(@Valid @ModelAttribute AnnounceDTO announceDTO, BindingResult bindingResult) throws Exception {
        announceService.makeTextAnnounce(announceDTO);
        return "redirect:/announces";
    }


}
