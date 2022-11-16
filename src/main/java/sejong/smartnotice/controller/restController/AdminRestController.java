package sejong.smartnotice.controller.restController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.helper.dto.AdminModifyDTO;
import sejong.smartnotice.service.AdminService;
import sejong.smartnotice.service.TownService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/admin", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AdminRestController {
    private final AdminService adminService;
    private final TownService townService;

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAdmin(@PathVariable Long id) {
        log.info("== 관리자 삭제 ==");
        adminService.delete(id);
        return ResponseEntity.ok().body("관리자를 성공적으로 삭제했습니다");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> modifyAdmin(@ModelAttribute("admin") AdminModifyDTO modifyDTO,
                                              @RequestParam(required = false, value = "town") List<Long> townIdList) {
        log.info("== 관리자 정보 수정 ==");
        adminService.modifyAdminInfo(modifyDTO);
        if(townIdList != null) {
            townService.modifyAdminManagedTownList(modifyDTO.getId(), townIdList);
        }
        return ResponseEntity.ok().body("수정을 완료하였습니다");
    }
}
