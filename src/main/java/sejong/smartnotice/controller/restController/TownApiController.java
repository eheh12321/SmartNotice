package sejong.smartnotice.controller.restController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.member.AdminType;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.helper.dto.request.TownModifyRequest;
import sejong.smartnotice.helper.dto.TownRegisterDTO;
import sejong.smartnotice.helper.dto.request.register.AdminRegisterDTO;
import sejong.smartnotice.service.AdminService;
import sejong.smartnotice.service.TownService;
import sejong.smartnotice.service.UserService;


import javax.validation.constraints.Positive;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api/towns")
@RequiredArgsConstructor
public class TownApiController {

    private final TownService townService;
    private final UserService userService;
    private final AdminService adminService;

    @Secured({"ROLE_SUPER"}) // 최고 관리자만 접근 가능
    @PostMapping
    public ResponseEntity<String> register(@ModelAttribute("town") TownRegisterDTO registerDTO) {
        if(registerDTO.getRegionCode() == 1L) {
            throw new IllegalArgumentException("마을을 선택해주세요");
        }
        townService.register(registerDTO);
        return ResponseEntity.ok("마을을 추가하였습니다");
    }

    @Secured({"ROLE_SUPER"})
    @PatchMapping("/{townId}")
    public ResponseEntity<String> modify(@PathVariable @Positive Long townId,
            @ModelAttribute("town") TownModifyRequest modifyDTO) {
        modifyDTO.setId(townId);
        townService.modifyTownInfo(modifyDTO);
        return ResponseEntity.ok("수정을 완료했습니다");
    }

    @Secured({"ROLE_SUPER"})
    @DeleteMapping("/{townId}")
    public ResponseEntity<String> remove(@PathVariable @Positive Long townId) {
        townService.delete(townId);
        return ResponseEntity.ok("성공적으로 삭제했습니다");
    }

    @Secured({"ROLE_SUPER"})
    @PostMapping("/{townId}/rep")
    public ResponseEntity<String> setRepresentativeTownAdmin(@PathVariable @Positive Long townId,
                                                             @RequestParam Long adminId) {
        townService.setTownRepresentativeAdmin(townId, adminId);
        return ResponseEntity.ok("대표 관리자를 설정했습니다");
    }

    // 마을 관리자 등록
    @Secured({"ROLE_SUPER"})
    @PostMapping("/{id}/admin/new")
    public ResponseEntity<String> addTownAdmin(@PathVariable @Positive Long id,
                               @RequestParam(required = false) Long userId,
                               @RequestParam(required = false) Long adminId) {
        Town town = townService.findById(id);
        if (userId != null && adminId == null) {
            User user = userService.findById(userId); // 주민 정보 조회
            AdminRegisterDTO registerDTO = new AdminRegisterDTO(user.getName(), user.getTel(),
                    user.getAccount().getLoginId(), user.getAccount().getLoginPw(), AdminType.ADMIN);
            Long newAdminId = adminService.register(registerDTO); // 주민 계정 정보로 관리자 계정 생성
            user.modifyUserIsAdmin(); // 마을 주민이 마을 관리자라는 상태 표시
            townService.addTownAdmin(adminService.findById(adminId), town); // 관리자와 마을 연결
        } else if (userId == null && adminId != null) {
            townService.addTownAdmin(adminService.findById(adminId), town); // 관리자와 마을 연결
        } else {
            log.warn("잘못된 요청입니다");
        }
        return ResponseEntity.ok("OK");
    }

    // 마을 관리자 삭제
    @Secured({"ROLE_SUPER"})
    @DeleteMapping("/{id}/admin")
    public ResponseEntity<String> removeTownAdmin(@PathVariable @Positive Long id,
                                  @RequestParam Long adminId) {
        townService.removeTownAdmin(adminService.findById(adminId), townService.findById(id));
        return ResponseEntity.ok("OK");
    }
}
