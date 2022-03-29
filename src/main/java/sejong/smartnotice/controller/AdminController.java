package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.dto.AdminDTO;
import sejong.smartnotice.dto.LoginDTO;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.service.AdminService;
import sejong.smartnotice.service.AnnounceService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final AnnounceService announceService;

    /**
     * 관리자 회원가입
     * http://localhost:8080/admin/register?name=name&type=ADMIN&tel=tel&loginId=id&loginPw=pw
     */
    @PostMapping("/register")
    public void register(
            @ModelAttribute LoginDTO loginDTO,
            @ModelAttribute AdminDTO adminDTO) {

        adminService.register(adminDTO, loginDTO);
    }

    /**
     * 관리자랑 마을 연결
     * - 관리자가 여러 마을을 동시에 관리할 수도 있음 (ROOT)
     * - 한 마을에 여러 관리자가 있을수도 있음
     * http://localhost:8080/admin/modify/town/1?tid=1&tid=2
     */
    @PostMapping("/modify/town/{id}")
    public void modifyManageTown(@PathVariable Long id, @RequestParam List<Long> tid) {
        Admin admin = adminService.findById(id);
        adminService.setManageTown(admin, tid);
    }

    /**
     * 마을 방송하기 (테스트)
     * http://localhost:8080/admin/announce/3?title=방송제목&category=일반&type=음성
     * (사전조건 - 관리자랑 마을이랑 연결되있어야됨)
     */
    @GetMapping("/announce/{adminId}")
    public void 관리자방송테스트용(@PathVariable Long adminId, @RequestParam String title, @RequestParam String category, @RequestParam String type) {

    }
}
