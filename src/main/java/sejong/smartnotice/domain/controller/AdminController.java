package sejong.smartnotice.domain.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.Admin_Town;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.dto.AdminDTO;
import sejong.smartnotice.domain.dto.LoginDTO;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.service.AdminService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * 관리자 회원가입
     * http://localhost:8080/admin/register?name=name&type=ADMIN&tel=tel&loginId=id&loginPw=pw
     */
    @PostMapping("/register")
    public void register(
            @ModelAttribute LoginDTO loginDTO,
            @ModelAttribute AdminDTO adminDTO) {

        Admin admin = Admin.builder()
                .loginId(loginDTO.getLoginId())
                .loginPw(loginDTO.getLoginPw())
                .name(adminDTO.getName())
                .tel(adminDTO.getTel())
                .type(adminDTO.getType()).build();

        adminService.register(admin);
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
     * 마을 추가 (나중에 옮기기)
     * http://localhost:8080/admin/town?name=마을
     */
    @PostMapping("/town")
    public void addTown(@RequestParam String name) {
        adminService.addTown(name);
    }

    // http://localhost:8080/admin/announce/1
    @GetMapping("/announce/{id}")
    public void 관리자방송테스트용(@PathVariable Long id) {
        Admin admin = adminService.findById(id);
        adminService.makeAnnounce(admin, "방송해보자", "일반");
    }
}
