package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.EmergencyAlert;
import sejong.smartnotice.domain.device.Device;
import sejong.smartnotice.domain.device.Sensor;
import sejong.smartnotice.domain.member.Supporter;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.dto.SensorDataDTO;
import sejong.smartnotice.dto.SupporterModifyDTO;
import sejong.smartnotice.dto.UserModifyDTO;
import sejong.smartnotice.service.EmergencyAlertService;
import sejong.smartnotice.service.SupporterService;
import sejong.smartnotice.service.UserService;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/s")
@RequiredArgsConstructor
public class SupporterPageController {

    private final UserService userService;
    private final SupporterService supporterService;
    private final EmergencyAlertService alertService;

    @GetMapping("/login")
    public String userLogin() {
        return "login";
    }

    @GetMapping
    public String index(Authentication auth, Model model) {
        log.info(auth.getName()); // Supporter Class에 정의된 getUsername() = loginId;
        Supporter supporter = supporterService.findByLoginId(auth.getName());
        List<EmergencyAlert> alertList = alertService.findWithUserByUserId(supporter.getUser().getId());
        model.addAttribute("alertList", alertList);
        model.addAttribute("supporter", supporter);
        return "s/index";
    }

    @GetMapping("/alert/{id}")
    @ResponseBody
    public ResponseEntity<List<EmergencyAlert>> alertList(@PathVariable Long id) {
        List<EmergencyAlert> alertList = alertService.findWithUserByUserId(id);
        return ResponseEntity.status(HttpStatus.OK).body(alertList);
    }

    @PutMapping("/supporter")
    @ResponseBody
    public ResponseEntity<String> supporterModify(@ModelAttribute("supporter") SupporterModifyDTO modifyDTO) {
        Supporter findSupporter = supporterService.findByTel(modifyDTO.getTel());
        if(findSupporter != null && findSupporter.getId() != modifyDTO.getId()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("중복된 전화번호가 존재합니다");
        }
        supporterService.modifySupporterInfo(modifyDTO);
        return ResponseEntity.ok("수정을 완료했습니다");
    }

    @PutMapping("/user")
    @ResponseBody
    public ResponseEntity<String> userModify(@ModelAttribute("user") UserModifyDTO modifyDTO) {
        User findUser = userService.findByTel(modifyDTO.getTel());
        if(findUser != null && findUser.getId() != modifyDTO.getId()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("중복된 전화번호가 존재합니다");
        }
        userService.modifyUserInfo(modifyDTO);
        return ResponseEntity.ok("수정을 완료했습니다");
    }


    private final EntityManager em;

    @ResponseBody
    @GetMapping("/sensor")
    public ResponseEntity<List<SensorDataDTO>> userDeviceSensorData(Authentication auth) {
        Supporter supporter = supporterService.findByLoginId(auth.getName());
        User user = supporter.getUser();
        Device device = user.getDevice();

        log.info("=============");
        List<Sensor> sensorList = em.createQuery("select s from Sensor s where s.device=:device and mod(s.id, 12) = 0", Sensor.class)
                .setParameter("device", device)
                .setMaxResults(360)
                .getResultList();

        List<SensorDataDTO> dtoList = new ArrayList<>();
        sensorList.stream().forEach(sensor -> {
            SensorDataDTO dto = new SensorDataDTO(sensor.getId(), sensor.getMeasureTime(), sensor.getTemp(), sensor.getCo2(), sensor.getLumnc(), sensor.getOxy(), sensor.getCo2());
            dtoList.add(dto);
        });

        return ResponseEntity.ok().body(dtoList);
    }
}
