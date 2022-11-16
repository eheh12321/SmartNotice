package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.smartnotice.domain.device.Device;
import sejong.smartnotice.domain.device.Sensor;
import sejong.smartnotice.helper.dto.DeviceRegisterDTO;
import sejong.smartnotice.helper.dto.MqttSensorJson;
import sejong.smartnotice.repository.DeviceRepository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DeviceService {

    private final EntityManager em;
    private final DeviceRepository deviceRepository;

    public Long register(DeviceRegisterDTO registerDTO) {
        Device device = Device.builder()
                .mac(registerDTO.getMac())
                .available(registerDTO.isAvailable()).build();

        deviceRepository.save(device);
        return device.getId();
    }
    
    public Device findDeviceById(Long deviceId) {
        return validateDeviceId(deviceId);
    }

    public void addSensorData(Long id, MqttSensorJson json) {
        Device device = findDeviceById(id);
        Sensor sensor = Sensor.builder()
                .device(device)
                .measureTime(json.getMeasureTime())
                .action(json.getAction())
                .temp(json.getTemp())
                .lumnc(json.getLumnc())
                .oxy(json.getOxy())
                .co2(json.getCo2()).build();

        em.persist(sensor);
    }

    public List<Long> findByEmergency_fireIsTrue() {
        return deviceRepository.findByEmergency_fireIsTrue();
    }

    private Device validateDeviceId(Long deviceId) {
        Optional<Device> opt = deviceRepository.findById(deviceId);
        if(opt.isEmpty()) {
            log.warn("단말기가 존재하지 않습니다");
            throw new RuntimeException("에러");
        }
        return opt.get();
    }

    public List<Device> findAll() {
        log.info("== 단말기 전체 목록 조회 ==");
        return deviceRepository.findAll();
    }
}
