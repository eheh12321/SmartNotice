package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.announce.Announce;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.dto.AnnounceOutputDTO;
import sejong.smartnotice.dto.AnnounceRegisterDTO;
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
        model.addAttribute("townList", adminService.getTownList(admin));
        return "announce/textAnnounce";
    }

    @PostMapping("/text")
    public String getTextAnnounce(@Valid @ModelAttribute AnnounceRegisterDTO announceRegisterDTO, BindingResult bindingResult) throws Exception {
        announceService.registerTextAnnounce(announceRegisterDTO);
        return "redirect:/announces";
    }

    @PostMapping("/text/api")
    public ResponseEntity<AnnounceOutputDTO> getAnnounceFile(String text) {
        AnnounceOutputDTO outputDTO = announceService.makeTextAnnounce(text);
        return ResponseEntity.ok().body(outputDTO);
    }

    @DeleteMapping("/{id}")
    public String remove(@PathVariable Long id) {
        announceService.delete(id);
        return "redirect:/announces";
    }

    @GetMapping("/{id}")
    public String getAnnounce(@PathVariable Long id, Model model) {
        Announce announce = announceService.findById(id);

        String fullPath = announce.getFullPath();
        log.info("방송 탐색 경로: {}", fullPath);

        model.addAttribute("announce", announce);
        model.addAttribute("filePath", fullPath);
        return "announce/getAnnounce";
    }
}
