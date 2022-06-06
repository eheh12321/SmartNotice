package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sejong.smartnotice.domain.device.Device;
import sejong.smartnotice.domain.device.Sensor;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.dto.DeviceRegisterDTO;
import sejong.smartnotice.dto.SensorDataDTO;
import sejong.smartnotice.service.DeviceService;
import sejong.smartnotice.service.UserService;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final UserService userService;
    private final DeviceService deviceService;
    private final EntityManager em;

    @GetMapping
    public String getDeviceList(Model model) {
        List<Device> deviceList = deviceService.findAll();
        model.addAttribute("deviceList", deviceList);
        return "device/list";
    }

    @PostMapping
    public String registerDevice(DeviceRegisterDTO registerDTO) {
        deviceService.register(registerDTO);
        return "redirect:/devices";
    }

    @ResponseBody
    @GetMapping("/{id}")
    public ResponseEntity<List<SensorDataDTO>> userDeviceSensorData(@PathVariable Long id) {
        User user = userService.findById(id);
        Device device = user.getDevice();

        List<SensorDataDTO> dtoList = new ArrayList<>();
        if(device == null) {
            return ResponseEntity.ok().body(dtoList);
        }
        List<Sensor> sensorList = em.createQuery("select s from Sensor s where s.device=:device and mod(s.id, 1) = 0 order by s.id desc", Sensor.class)
                .setParameter("device", device)
                .setMaxResults(360)
                .getResultList();

        sensorList.sort(new Comparator<Sensor>() {
            @Override
            public int compare(Sensor o1, Sensor o2) {
                return o1.getMeasureTime().compareTo(o2.getMeasureTime());
            }
        });

        sensorList.stream().forEach(sensor -> {
            SensorDataDTO dto = new SensorDataDTO(sensor.getId(), sensor.getMeasureTime(), sensor.getTemp(), sensor.getCo2(), sensor.getOxy(), sensor.getLumnc(), sensor.getAction());
            dtoList.add(dto);
        });

        return ResponseEntity.ok().body(dtoList);
    }

    @ResponseBody
    @GetMapping("/{id}/update")
    public ResponseEntity<SensorDataDTO> updateUserDeviceSensorData(@PathVariable Long id) {
        User user = userService.findById(id);
        Device device = user.getDevice();
        if(device == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Sensor sensor = em.createQuery("select s from Sensor s where s.device=:device order by s.id desc", Sensor.class)
                .setParameter("device", device).setMaxResults(1).getSingleResult();

        SensorDataDTO dto = new SensorDataDTO(sensor.getId(), sensor.getMeasureTime(), sensor.getTemp(), sensor.getCo2(), sensor.getOxy(), sensor.getLumnc(), sensor.getAction());
        return ResponseEntity.ok().body(dto);
    }
}
