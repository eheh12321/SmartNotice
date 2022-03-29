package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.dto.AdminDTO;
import sejong.smartnotice.dto.LoginDTO;
import sejong.smartnotice.dto.UserDTO;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.User;
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

    /**
     * 사용자 등록
     * - 가만 생각해보니 관리자가 사용자 등록을 하는게 맞는것 같음. 사용자는 로그인할 일이 없다
     * http://localhost:8080/admin/user?name=user&address=address&tel=tel&townId=1
     */
    @PostMapping("/user")
    public void addUser(@ModelAttribute UserDTO userDTO, @RequestParam Long townId) {
        Town town = adminService.findTownById(townId); // 유저가 소속될 마을 (DB에 미리 존재해야됨)

        User user = User.builder() // 유저 객체 생성
                .name(userDTO.getName())
                .address(userDTO.getAddress())
                .tel(userDTO.getTel())
                .town(town).build();

        adminService.addUser(user); // 유저 등록
    }

    /**
     * 마을 방송하기 (테스트)
     * http://localhost:8080/admin/announce/3?title=방송제목&category=일반&type=음성
     * (사전조건 - 관리자랑 마을이랑 연결되있어야됨)
     */
    @GetMapping("/announce/{adminId}")
    public void 관리자방송테스트용(@PathVariable Long adminId, @RequestParam String title, @RequestParam String category, @RequestParam String type) {
        announceService.방송테스트(adminId, title, category, type);
    }
}
