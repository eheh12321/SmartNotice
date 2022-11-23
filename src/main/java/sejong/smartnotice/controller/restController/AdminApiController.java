package sejong.smartnotice.controller.restController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.helper.dto.request.AdminRequest.AdminModifyRequest;
import sejong.smartnotice.helper.dto.request.AdminRequest.AdminRegisterRequest;
import sejong.smartnotice.helper.dto.response.AdminResponse;
import sejong.smartnotice.helper.dto.response.Response;
import sejong.smartnotice.helper.dto.response.SingleResponse;
import sejong.smartnotice.helper.validator.AdminModifyValidator;
import sejong.smartnotice.helper.validator.AdminModifyValidator.AdminModifyValidateDto;
import sejong.smartnotice.helper.validator.UserAccountRegisterValidator;
import sejong.smartnotice.service.AdminService;
import sejong.smartnotice.service.TownService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminApiController {

    private final AdminService adminService;
    private final TownService townService;

    private final AdminModifyValidator adminModifyValidator;
    private final UserAccountRegisterValidator userAccountRegisterValidator;

    @GetMapping
    public ResponseEntity<Response<?>> getAdmin(@RequestParam(required = false) Long townId) {
        List<AdminResponse> adminList;
        if (townId != null) {
            adminList = adminService.findAdminByTown(townId);
        } else {
            adminList = adminService.findAll();
        }
        return ResponseEntity.ok(Response.<AdminResponse>builder()
                .data(adminList)
                .message("관리자를 조회했습니다.").build());
    }

    @PostMapping
    public ResponseEntity<SingleResponse<?>> createAdmin(
            @Valid @RequestBody AdminRegisterRequest registerRequest,
            BindingResult bindingResult) throws BindException {
        userAccountRegisterValidator.validate(registerRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        AdminResponse response = adminService.register(registerRequest);
        return ResponseEntity.ok(SingleResponse.<AdminResponse>builder()
                .data(response)
                .message("관리자를 생성했습니다").build());
    }

    @Secured("ROLE_SUPER")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<?>> deleteAdmin(@PathVariable Long id) {
        adminService.delete(id);
        return ResponseEntity.ok(Response.<String>builder()
                .data(List.of())
                .message("관리자를 성공적으로 삭제했습니다.").build());
    }

    @PatchMapping("/{adminId}")
    public ResponseEntity<SingleResponse<?>> modifyAdmin(@PathVariable Long adminId,
                                                         @AuthenticationPrincipal Admin admin,
                                                         @Validated @RequestBody AdminModifyRequest modifyRequest,
                                                         BindingResult bindingResult) throws BindException {
        // 1. 입력 폼 검증
        adminModifyValidator.validate(new AdminModifyValidateDto(adminId, modifyRequest), bindingResult);
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        // 2. 회원 정보 수정
        AdminResponse changedAdminResponse = adminService.modifyAdminInfo(adminId, modifyRequest);

        // 3. 관리자 관리 마을 수정 - 최고 관리자 권한 있어야 수정가능
        if (admin.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SUPER"))) {
            townService.modifyAdminManagedTownList(adminId, modifyRequest.getTownIdList());
        }

        return ResponseEntity.ok(SingleResponse.<AdminResponse>builder()
                .data(changedAdminResponse)
                .message("관리자를 성공적으로 수정했습니다.").build());
    }
}
