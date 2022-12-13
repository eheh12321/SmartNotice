package sejong.smartnotice.controller.restController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sejong.smartnotice.helper.dto.response.DataTableResponse;
import sejong.smartnotice.helper.dto.response.UserResponse;
import sejong.smartnotice.service.UserService;


@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<DataTableResponse<?>> getUser(
            Integer draw,
            Integer start,
            Integer length) {
        PageRequest pageRequest = PageRequest.of(start, length);
        Page<UserResponse> userPages = userService.findAll(pageRequest);

        return ResponseEntity.ok(DataTableResponse.<UserResponse>builder()
                .draw(draw)
                .recordsTotal(userPages.getTotalPages())
                .recordsFiltered(userPages.getTotalPages())
                .data(userPages.getContent())
                .build());
    }
}
