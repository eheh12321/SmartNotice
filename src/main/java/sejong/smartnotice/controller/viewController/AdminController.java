package sejong.smartnotice.controller.viewController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.helper.dto.response.AdminResponse;
import sejong.smartnotice.helper.dto.response.TownResponse;
import sejong.smartnotice.service.AdminService;
import sejong.smartnotice.service.TownService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping(value = "/admin", produces = MediaType.TEXT_HTML_VALUE)
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final TownService townService;

    @GetMapping
    public String getAdminList(Model model) {
        log.info("== 관리자 목록 조회 ==");
        List<TownResponse> townList = townService.findAll().stream() // 전체 마을 조회
                .map(TownResponse::from)
                .collect(Collectors.toList());
        List<AdminResponse> adminList = adminService.findAllWithTown().stream() // 마을 관리자 조회
                .map(AdminResponse::from)
                .collect(Collectors.toList());

        model.addAttribute("townList", townList);
        model.addAttribute("adminList", adminList);
        return "admin/list";
    }
}
