package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.announce.Announce;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.dto.AnnounceModalDTO;
import sejong.smartnotice.dto.AnnounceOutputDTO;
import sejong.smartnotice.dto.AnnounceRegisterDTO;
import sejong.smartnotice.service.AdminService;
import sejong.smartnotice.service.AnnounceService;
import sejong.smartnotice.service.TownService;

import javax.persistence.EntityManager;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/announces")
public class AnnounceController {

    private final AnnounceService announceService;
    private final TownService townService;
    private final AdminService adminService;

    @GetMapping
    public String getAnnounceList(Authentication auth, Model model) {
        List<Announce> announceList;
        if(!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SUPER"))) {
            // 마을 관리자인 경우 관리 마을 대상으로 한 방송 목록만 조회
            Admin authAdmin = adminService.findByLoginId(auth.getName());
            List<Town> managedTownList = townService.findTownByAdmin(authAdmin);
            announceList = announceService.findAllAnnounceToTown(managedTownList);
        } else {
            announceList = announceService.findAll();
        }
        model.addAttribute("announceList", announceList);
        return "announce/list";
    }

    @GetMapping("/normal")
    public String getNormalAnnounceListPage(Authentication auth, Model model) {
        List<Announce> announceList;
        if(!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SUPER"))) {
            // 마을 관리자인 경우 관리 마을 대상으로 한 방송 목록만 조회
            Admin authAdmin = adminService.findByLoginId(auth.getName());
            List<Town> managedTownList = townService.findTownByAdmin(authAdmin);
            announceList = announceService.findAllAnnounceToTown(managedTownList);
        } else {
            announceList = announceService.findAll();
        }
        model.addAttribute("announceList", announceList);
        return "announce/normalAnnounceList";
    }

    @GetMapping("/disaster")
    public String getDisasterAnnounceListPage(Authentication auth, Model model) {
        List<Announce> announceList;
        if(!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SUPER"))) {
            // 마을 관리자인 경우 관리 마을 대상으로 한 방송 목록만 조회
            Admin authAdmin = adminService.findByLoginId(auth.getName());
            List<Town> managedTownList = townService.findTownByAdmin(authAdmin);
            announceList = announceService.findAllAnnounceToTown(managedTownList);
        } else {
            announceList = announceService.findAll();
        }

        model.addAttribute("announceList", announceList);
        return "announce/disasterAnnounceList";
    }

    @GetMapping("/text")
    public String getTextAnnounceForm(@AuthenticationPrincipal Admin admin, Model model) {
        model.addAttribute("admin", admin);
        model.addAttribute("townList", adminService.getTownList(admin));
        return "announce/newTextAnnounce";
    }

    @PostMapping("/text")
    public String getTextAnnounce(@Valid @ModelAttribute AnnounceRegisterDTO announceRegisterDTO) {
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
        return "announce/newVoiceAnnounce";
    }

    @PostMapping("/voice")
    public String getVoiceAnnounce(@Valid @ModelAttribute AnnounceRegisterDTO registerDTO) {
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
