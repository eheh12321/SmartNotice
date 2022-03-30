package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sejong.smartnotice.domain.device.Device;
import sejong.smartnotice.domain.device.Sensor;
import sejong.smartnotice.repository.DeviceRepository;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public Long register() {
        Device device = Device.builder().build();
        deviceRepository.save(device);
        return device.getId();
    }
    
    public Device findDeviceById(Long deviceId) {
        return validateDeviceId(deviceId);
    }

    public Map<String, Double> getRecentData(Long deviceId) {
        Device device = validateDeviceId(deviceId);
        Sensor sensor = device.getSensor();
        return sensor.getRecentData();
    }

    private Device validateDeviceId(Long deviceId) {
        Optional<Device> opt = deviceRepository.findById(deviceId);
        if(opt.isEmpty()) {
            log.warn("단말기가 존재하지 않습니다");
            throw new RuntimeException("에러");
        }
        return opt.get();
    }
}
