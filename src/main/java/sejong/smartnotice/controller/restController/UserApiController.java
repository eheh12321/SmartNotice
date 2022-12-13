package sejong.smartnotice.controller.restController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.helper.dto.response.DataTableResponse;
import sejong.smartnotice.helper.dto.response.UserResponse;
import sejong.smartnotice.service.UserService;


@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    // 테이블 컬럼 순서 (정렬을 위해 필요)
    private static final String[] tableColumns = new String[]{"id", "name", "birth", "tel", "address", "townName", "supporterList"};

    @GetMapping
    public ResponseEntity<DataTableResponse<?>> getUser(
            Integer draw,
            Integer start,
            Integer length,
            @RequestParam(name = "order[0][column]") Integer column,
            @RequestParam(name = "order[0][dir]") String direction) {

        int startPage = (start / length);
        //startPage에서 length개 가져오겠다
        // column 기준으로 direction 방향 정렬
        PageRequest pageRequest = PageRequest.of(startPage, length, Sort.Direction.fromString(direction), tableColumns[column]);
        Page<UserResponse> userPages = userService.findAll(pageRequest);

        return ResponseEntity.ok(DataTableResponse.<UserResponse>builder()
                .draw(draw)
                .recordsTotal((int)userPages.getTotalElements())
                .recordsFiltered((int)userPages.getTotalElements())
                .data(userPages.getContent())
                .build());
    }
}
