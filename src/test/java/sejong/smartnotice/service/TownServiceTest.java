package sejong.smartnotice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sejong.smartnotice.domain.Region;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.dto.TownRegisterDTO;
import sejong.smartnotice.repository.TownRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TownServiceTest {

    @Mock
    private TownRepository townRepository;

    @Mock
    private EntityManager em;

    @Mock
    private Query query;

    @InjectMocks
    private TownService townService;

    @Test
    void 마을_생성() {
        // given
        Region region = new Region(1L, "시/도", "시/군/구");
        when(em.find(eq(Region.class), any())).thenReturn(region);

        Town town = createTown("마을", region);
        when(townRepository.findById(any())).thenReturn(Optional.of(town));

        TownRegisterDTO registerDTO = TownRegisterDTO.builder()
                .name("마을")
                .regionCode(1L).build();

        // when
        Long townId = townService.register(registerDTO);

        // then
        Town findTown = townRepository.findById(townId).get();

        assertThat(findTown.getName()).isEqualTo("마을");
        assertThat(findTown.getRegion()).isEqualTo(region);
    }

    @Test
    void 마을_생성_실패_중복() {
        // given
        Region region = new Region(1L, "시/도", "시/군/구");
        when(em.find(eq(Region.class), any())).thenReturn(region);
        when(townRepository.existsByRegionAndName(any(), any())).thenReturn(true);

        TownRegisterDTO registerDTO = TownRegisterDTO.builder()
                .name("마을")
                .regionCode(1L).build();

        // then
        assertThrows(IllegalStateException.class, () -> {
            // when
            townService.register(registerDTO);
        });
    }




    private Town createTown(String townName, Region region) {
        return Town.createTown(townName, region);
    }
}