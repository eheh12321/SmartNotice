package sejong.smartnotice.controller.restController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.helper.dto.request.TownModifyRequest;
import sejong.smartnotice.helper.dto.TownRegisterDTO;
import sejong.smartnotice.service.TownService;

import javax.validation.constraints.Positive;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api/towns")
@RequiredArgsConstructor
public class TownApiController {

    private final TownService townService;

    @Secured({"ROLE_SUPER"}) // 최고 관리자만 접근 가능
    @PostMapping
    public ResponseEntity<String> register(@ModelAttribute("town") TownRegisterDTO registerDTO) {
        log.info("== 마을 등록 ==");
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
        log.info("== 마을 수정 ==");
        modifyDTO.setId(townId);
        townService.modifyTownInfo(modifyDTO);
        return ResponseEntity.ok("수정을 완료했습니다");
    }

    @Secured({"ROLE_SUPER"})
    @DeleteMapping("/{townId}")
    public ResponseEntity<String> remove(@PathVariable @Positive Long townId) {
        log.info("== 마을 삭제 ==");
        townService.delete(townId);
        return ResponseEntity.ok("성공적으로 삭제했습니다");
    }

}
