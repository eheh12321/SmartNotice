package sejong.smartnotice.restController;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.dto.TownRegisterDTO;
import sejong.smartnotice.service.TownService;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/api/faker")
@RequiredArgsConstructor
public class FakerApiController {
    
    private final TownService townService;
    private static final Faker FAKER = new Faker(Locale.KOREA);
    
    @PostMapping("/town")
    public ResponseEntity<String> createFakeTown(@Nullable Authentication auth) {
        if(auth == null || !isSuperAuthority(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다");
        }

        String cityName = FAKER.address().cityName();
        List<Town> sameTownList = townService.findByName(cityName);
        if(sameTownList.size() != 0) { // 이미 마을이 존재하는 경우
            cityName = cityName + sameTownList.size(); // 숫자 붙이기
        }
        cityName = cityName + "마을";

        TownRegisterDTO registerDTO
                = new TownRegisterDTO(cityName, 1L);

        townService.register(registerDTO);
        return ResponseEntity.ok("[" + cityName + "]을 생성했습니다");
    }

    private boolean isSuperAuthority(Authentication auth) {
        return auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SUPER"));
    }
}
