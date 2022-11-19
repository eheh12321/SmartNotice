package sejong.smartnotice.controller.viewController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.helper.dto.response.AnnounceResponse;
import sejong.smartnotice.service.AnnounceService;
import sejong.smartnotice.service.TownService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/announces")
public class AnnounceController {

    private final AnnounceService announceService;
    private final TownService townService;

    @GetMapping
    public String getAnnounceList(
            @AuthenticationPrincipal Admin admin,
            @RequestParam(defaultValue = "all") String category,
            Model model) {
        List<AnnounceResponse> announceList = announceService.findManagedTownAnnounces(admin, category)
                .stream().map(AnnounceResponse::from).collect(Collectors.toList());

        model.addAttribute("category", category);
        model.addAttribute("announceList", announceList);
        return "announce/list";
    }

    @GetMapping("/text")
    public String getTextAnnounceForm(@AuthenticationPrincipal Admin admin, Model model) {
        model.addAttribute("admin", admin);
        model.addAttribute("townList", townService.findTownByAdmin(admin));
        return "announce/newTextAnnounce";
    }

    @GetMapping("/voice")
    public String getVoiceAnnounceForm(@AuthenticationPrincipal Admin admin, Model model) {
        model.addAttribute("admin", admin);
        model.addAttribute("townList", townService.findTownByAdmin(admin));
        return "announce/newVoiceAnnounce";
    }
}
