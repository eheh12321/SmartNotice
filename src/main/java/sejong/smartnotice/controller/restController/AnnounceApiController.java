package sejong.smartnotice.controller.restController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.announce.Announce;
import sejong.smartnotice.helper.dto.request.AnnounceRequest;
import sejong.smartnotice.helper.dto.response.AnnounceResponse;
import sejong.smartnotice.service.AnnounceService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/announces")
@RequiredArgsConstructor
public class AnnounceApiController {

    private final AnnounceService announceService;

    @GetMapping("/{id}")
    public ResponseEntity<AnnounceResponse> getAnnounce(@PathVariable @Positive Long id) {
        Announce announce = announceService.findById(id);
        AnnounceResponse dto = AnnounceResponse.from(announce);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @PostMapping
    public ResponseEntity<String> getTextAnnounce(@Valid @ModelAttribute AnnounceRequest announceRequest) {
        announceService.registerAnnounce(announceRequest);
        return ResponseEntity.ok().body("방송을 성공적으로 생성했습니다");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> remove(@PathVariable @Positive Long id) {
        announceService.delete(id);
        return ResponseEntity.ok().body("방송을 성공적으로 삭제했습니다");
    }
}
