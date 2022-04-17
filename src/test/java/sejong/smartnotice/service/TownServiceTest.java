package sejong.smartnotice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sejong.smartnotice.domain.Region;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.dto.TownRegisterDTO;
import sejong.smartnotice.repository.TownRepository;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;
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
    private AdminService adminService;

    @Mock
    private EntityManager em;

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

    @Test
    void 마을_생성_실패_지역없음() {
        // given
        when(em.find(eq(Region.class), any())).thenReturn(null);
        TownRegisterDTO registerDTO = TownRegisterDTO.builder()
                .name("마을")
                .regionCode(1L).build();

        // then
        assertThrows(NullPointerException.class, () -> {
            townService.register(registerDTO);
        });
    }

    @Test
    void 마을_삭제() {
        // given
        Region region = new Region(1L, "시/도", "시/군/구");
        Town town = createTown("마을", region);
        when(townRepository.findById(any())).thenReturn(Optional.of(town));
        when(townRepository.existsByRegionAndName(any(), any())).thenReturn(false);

        // when
        townService.delete(town.getId());

        // then
        assertThat(townRepository.existsByRegionAndName(region, town.getName())).isFalse();
    }

    @Test
    void 마을_삭제_실패() {
        // given
        Region region = new Region(1L, "시/도", "시/군/구");
        Town town = createTown("마을", region);
        User user = User.builder()
                .town(town).build();
        town.getUserList().add(user);
        when(townRepository.findById(any())).thenReturn(Optional.of(town));

        // then
        assertThrows(IllegalStateException.class, () -> {
            // when
            townService.delete(town.getId());
        });
    }

    @Test
    void 마을_관리자등록_실패_중복() {
        // given
        Admin admin = Admin.builder()
                .name("관리자").build();
        when(adminService.findById(any())).thenReturn(admin);

        Region region = new Region(1L, "시/도", "시/군/구");
        Town town = createTown("마을", region);
        when(townRepository.findById(any())).thenReturn(Optional.of(town));

        List<Town> adminTownList = new ArrayList<>();
        adminTownList.add(town);
        when(adminService.getTownList(any())).thenReturn(adminTownList);

        // then
        assertThrows(IllegalStateException.class, () -> {
            // when
            townService.addTownAdmin(town.getId(), admin.getId());
        });
    }

    private Town createTown(String townName, Region region) {
        return Town.createTown(townName, region);
    }
}