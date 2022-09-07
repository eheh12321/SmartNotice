package sejong.smartnotice.restController;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sejong.smartnotice.domain.Region;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.dto.TownRegisterDTO;
import sejong.smartnotice.service.TownService;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/faker")
@RequiredArgsConstructor
public class FakerApiController {

    private final EntityManager em;
    private final TownService townService;
    private static final Faker FAKER = new Faker(Locale.KOREA);

    @Value("${resources.location}")
    private String resourceLocation;

    @PostMapping("/town")
    public ResponseEntity<String> createFakeTown(@Nullable Authentication auth) {
        if (auth == null || !isSuperAuthority(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다");
        }

        long len = em.createQuery("select count(r) from Region r", Long.class)
                .getSingleResult();

        long newRegionNum;
        do {
            newRegionNum = (long) (Math.random() * len);
        } while (newRegionNum == 0);
        log.info("새 마을 지역 번호: {}", newRegionNum);

        Region newRegion = em.createQuery("select r from Region r where r.id=:code", Region.class)
                .setParameter("code", newRegionNum)
                .getSingleResult();

        StringBuilder sb = new StringBuilder();
        sb.append(newRegion.getParentRegion());
        sb.append(" ");
        sb.append(newRegion.getChildRegion());

        String newCityName = sb.toString();
        List<Town> prevTownList = townService.findByName(newCityName);
        if (prevTownList.size() != 0) {
            sb.append(prevTownList.size());
        }
        sb.append(" 마을");
        newCityName = sb.toString();

        TownRegisterDTO registerDTO = new TownRegisterDTO(newCityName, newRegion.getId());
        townService.register(registerDTO);

        return ResponseEntity.ok("[" + newCityName + "]을 생성했습니다");
    }

    /**
     * 지역 목록을 초기화 한다
     * - 마을이 하나라도 등록이 되어있으면 외래키 제약조건 때문에 초기화 불가
     * - region 테이블을 처음에 싹 비우고 파일을 읽어서 다시 싹 채우는 방식
     *
     * @return
     */
    @PostMapping("/region")
    @Transactional
    public ResponseEntity<String> resetRegionList(Authentication auth) {
        if (auth == null || !isSuperAuthority(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다");
        }

        List<Town> townList = townService.findAll();
        if (townList.size() != 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("마을이 존재하고 있어 초기화가 불가능합니다");
        }

        em.createQuery("delete from Region r where r.id > 0").executeUpdate();

        String fileLocation = resourceLocation + "/init/regionList.csv"; // 설정파일에 설정된 경로 뒤에 붙인다
        Path path = Paths.get(fileLocation);
        URI uri = path.toUri();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new UrlResource(uri).getInputStream()))
        ) {
            String line = br.readLine(); // head 떼기
            while ((line = br.readLine()) != null) {
                String[] splits = line.split(",");
                em.persist(new Region(Long.parseLong(splits[0]), splits[1], splits[2],
                        Integer.parseInt(splits[3]), Integer.parseInt(splits[4])));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("뭔가 오류가 생겼는데요");
        }
        return ResponseEntity.ok("초기화에 성공했습니다");
    }

    private boolean isSuperAuthority(Authentication auth) {
        return auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SUPER"));
    }
}
