package sejong.smartnotice.controller.restController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.helper.dto.request.AdminModifyRequest;
import sejong.smartnotice.helper.dto.response.AdminResponse;
import sejong.smartnotice.helper.validator.AdminModifyValidator;
import sejong.smartnotice.service.AdminService;
import sejong.smartnotice.service.TownService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping( "/api/admin")
@RequiredArgsConstructor
public class AdminApiController {

    private final AdminService adminService;
    private final TownService townService;

    private final AdminModifyValidator adminModifyValidator;

    @GetMapping
    public ResponseEntity<?> getAdmin(@RequestParam(required = false) Long townId) {
        List<AdminResponse> adminList;
        if(townId != null) {
            adminList = adminService.findAdminByTown(townId).stream()
                    .map(AdminResponse::from)
                    .collect(Collectors.toList());
        } else {
            adminList = adminService.findAll().stream()
                    .map(AdminResponse::from)
                    .collect(Collectors.toList());
        }
        return ResponseEntity.ok(adminList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAdmin(@PathVariable Long id) {
        adminService.delete(id);
        return ResponseEntity.ok().body("관리자를 성공적으로 삭제했습니다");
    }

    @PatchMapping("/{adminId}")
    public ResponseEntity<String> modifyAdmin(@PathVariable Long adminId,
                                              @AuthenticationPrincipal Admin admin,
                                              @Validated @ModelAttribute("admin") AdminModifyRequest modifyDto,
                                              BindingResult bindingResult,
                                              @RequestParam(required = false, value = "town") List<Long> townIdList) throws BindException {
        // 1. 입력 폼 검증
        modifyDto.setId(adminId);
        adminModifyValidator.validate(modifyDto, bindingResult);
        if(bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        
        // 2. 회원 정보 수정
        adminService.modifyAdminInfo(modifyDto);
        
        // 3. 관리자 관리 마을 수정 - 최고 관리자 권한 있어야 수정가능
        if(admin.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SUPER"))) {
            townService.modifyAdminManagedTownList(adminId, townIdList);
        }

        return ResponseEntity.ok().body("수정을 완료하였습니다");
    }


}
