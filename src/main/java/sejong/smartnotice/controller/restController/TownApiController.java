package sejong.smartnotice.controller.restController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.member.AdminType;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.helper.dto.request.AdminRequest.AdminRegisterRequest;
import sejong.smartnotice.helper.dto.request.TownRequest.TownCreateRequest;
import sejong.smartnotice.helper.dto.request.TownRequest.TownModifyRequest;
import sejong.smartnotice.helper.dto.response.Response;
import sejong.smartnotice.helper.dto.response.SingleResponse;
import sejong.smartnotice.helper.dto.response.TownResponse;
import sejong.smartnotice.service.AdminService;
import sejong.smartnotice.service.TownService;
import sejong.smartnotice.service.UserService;


import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api/towns")
@RequiredArgsConstructor
public class TownApiController {

    private final MessageSource messageSource;

    private final TownService townService;
    private final UserService userService;
    private final AdminService adminService;

    @Secured({"ROLE_SUPER"}) // 최고 관리자만 접근 가능
    @PostMapping
    public ResponseEntity<SingleResponse<?>> createTown(@RequestBody TownCreateRequest registerDTO,
                                                      HttpServletRequest request) {
        if (registerDTO.getRegionCode() == 1L) {
            throw new IllegalArgumentException("마을을 선택해주세요");
        }
        TownResponse response = townService.register(registerDTO);
        return ResponseEntity.ok(SingleResponse.<TownResponse>builder()
                .data(response)
                .message(messageSource.getMessage("api.create.town", null, request.getLocale())).build());
    }

    @Secured({"ROLE_SUPER"})
    @PatchMapping("/{townId}")
    public ResponseEntity<SingleResponse<?>> updateTown(
            @PathVariable @Positive Long townId,
            @RequestBody TownModifyRequest modifyDTO,
            HttpServletRequest request) {
        TownResponse response = townService.modifyTownInfo(townId, modifyDTO);
        return ResponseEntity.ok(SingleResponse.<TownResponse>builder()
                .data(response)
                .message(messageSource.getMessage("api.update.town", null, request.getLocale())).build());
    }

    @Secured({"ROLE_SUPER"})
    @DeleteMapping("/{townId}")
    public ResponseEntity<Response<?>> deleteTown(@PathVariable @Positive Long townId,
                                                  HttpServletRequest request) {
        townService.delete(townId);
        return ResponseEntity.ok(Response.<String>builder()
                .data(List.of())
                .message(messageSource.getMessage("api.delete.town", null, request.getLocale())).build());
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
            AdminRegisterRequest registerDTO = new AdminRegisterRequest(user.getName(), user.getTel(),
                    user.getAccount().getLoginId(), user.getAccount().getLoginPw(), AdminType.ADMIN);
            adminService.register(registerDTO); // 주민 계정 정보로 관리자 계정 생성
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
