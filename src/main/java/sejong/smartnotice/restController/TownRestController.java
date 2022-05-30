package sejong.smartnotice.restController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.member.AdminType;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.dto.AdminRegisterDTO;
import sejong.smartnotice.dto.TownModifyDTO;
import sejong.smartnotice.dto.TownRegisterDTO;
import sejong.smartnotice.service.AdminService;
import sejong.smartnotice.service.TownService;
import sejong.smartnotice.service.UserService;

@Slf4j
@RestController
@RequestMapping(value = "/towns", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TownRestController {

    private final TownService townService;
    private final UserService userService;
    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<String> register(Authentication auth,
                                           @ModelAttribute("town") TownRegisterDTO registerDTO) {
        log.info("== 마을 등록 ==");
        checkAuthority(auth);
        if(registerDTO.getRegionCode() == 1L) {
            throw new IllegalArgumentException("마을을 선택해주세요");
        }
        townService.register(registerDTO);
        return ResponseEntity.ok("마을을 추가하였습니다");
    }

    @PutMapping
    public ResponseEntity<String> modify(Authentication auth,
                                         @ModelAttribute("town") TownModifyDTO modifyDTO) {
        log.info("== 마을 수정 ==");
        checkAuthority(auth);
        if(modifyDTO.getRegionCode() == 1L) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("마을을 선택해주세요");
        }
        townService.modifyTownInfo(modifyDTO);
        return ResponseEntity.ok("수정을 완료했습니다");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> remove(Authentication auth, @PathVariable Long id) {
        log.info("== 마을 삭제 ==");
        checkAuthority(auth);
        townService.delete(id);
        return ResponseEntity.ok("성공적으로 삭제했습니다");
    }

    private void checkAuthority(Authentication auth) {
        if(!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SUPER"))) {
            throw new SecurityException("접근 권한이 없습니다");
        }
    }
}
