package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sejong.smartnotice.domain.EmergencyAlert;
import sejong.smartnotice.service.EmergencyAlertService;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class EmergencyAlertController {

    private final EmergencyAlertService alertService;

    @GetMapping
    public String getList(Model model) {
        List<EmergencyAlert> alertList = alertService.findAllWithUser();
        model.addAttribute("alertList", alertList);
        return "alert/list";
    }
}
