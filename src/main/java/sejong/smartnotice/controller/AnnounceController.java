package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.announce.Announce;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.dto.AnnounceModalDTO;
import sejong.smartnotice.dto.AnnounceOutputDTO;
import sejong.smartnotice.dto.AnnounceRegisterDTO;
import sejong.smartnotice.service.AdminService;
import sejong.smartnotice.service.AnnounceService;

import javax.validation.Valid;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;
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
        List<Announce> announceList = announceService.findAll();
        // 내림차순 정렬(임시)
        Collections.sort(announceList, new Comparator<Announce>() {
            @Override
            public int compare(Announce o1, Announce o2) {
                return o2.getId().compareTo(o1.getId());
            }
        });
        model.addAttribute("separator", File.separator);
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
    public String getTextAnnounce(@Valid @ModelAttribute AnnounceRegisterDTO announceRegisterDTO, BindingResult bindingResult) {
        announceService.registerAnnounce(announceRegisterDTO);
        return "redirect:/announces";
    }

    @PostMapping("/text/api")
    public ResponseEntity<AnnounceOutputDTO> getAnnounceFile(String text) {
        AnnounceOutputDTO outputDTO = announceService.makeTextAnnounce(text);
        return ResponseEntity.ok().body(outputDTO);
    }

    @GetMapping("/voice")
    public String getVoiceAnnounceForm(@AuthenticationPrincipal Admin admin, Model model) {
        model.addAttribute("admin", admin);
        model.addAttribute("townList", adminService.getTownList(admin));
        return "announce/voiceAnnounce";
    }

    @PostMapping("/voice")
    public String getVoiceAnnounce(@Valid @ModelAttribute AnnounceRegisterDTO registerDTO, BindingResult bindingResult) {
        announceService.registerAnnounce(registerDTO);
        return "redirect:/announces";
    }

    @DeleteMapping("/{id}")
    public String remove(@PathVariable Long id) {
        announceService.delete(id);
        return "redirect:/announces";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<AnnounceModalDTO> getAnnounce(@PathVariable Long id) {
        Announce announce = announceService.findById(id);
        AnnounceModalDTO dto = AnnounceModalDTO.entityToDto(announce);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }
}
