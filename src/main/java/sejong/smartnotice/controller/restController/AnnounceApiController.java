package sejong.smartnotice.controller.restController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.helper.dto.request.AnnounceRequest;
import sejong.smartnotice.helper.dto.response.AnnounceResponse;
import sejong.smartnotice.helper.dto.response.Response;
import sejong.smartnotice.helper.dto.response.SingleResponse;
import sejong.smartnotice.service.AnnounceService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/announces")
@RequiredArgsConstructor
public class AnnounceApiController {

    private final MessageSource messageSource;
    private final AnnounceService announceService;

    @GetMapping("/{id}")
    public ResponseEntity<SingleResponse<?>> getAnnounce(@PathVariable @Positive Long id,
                                                         HttpServletRequest request) {
        AnnounceResponse response = AnnounceResponse.from(announceService.findById(id));
        return ResponseEntity.ok(SingleResponse.<AnnounceResponse>builder()
                .data(response)
                .message(messageSource.getMessage("api.get.announce", null, request.getLocale())).build());
    }

    @PostMapping
    public ResponseEntity<SingleResponse<?>> createAnnounce(@Valid @RequestBody AnnounceRequest announceRequest,
                                                  HttpServletRequest request) {
        AnnounceResponse announceResponse = announceService.registerAnnounce(announceRequest);
        return ResponseEntity.ok().body(SingleResponse.<AnnounceResponse>builder()
                .data(announceResponse)
                .message(messageSource.getMessage("api.create.announce", null, request.getLocale())).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<?>> deleteAnnounce(@PathVariable @Positive Long id,
                                                    HttpServletRequest request) {
        announceService.delete(id);
        return ResponseEntity.ok().body(Response.<String>builder()
                .data(List.of())
                .message(messageSource.getMessage("api.delete.announce", null, request.getLocale())).build()
        );
    }
}
