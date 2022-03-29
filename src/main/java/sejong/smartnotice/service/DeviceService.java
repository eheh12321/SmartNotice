package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sejong.smartnotice.domain.device.Device;
import sejong.smartnotice.repository.DeviceRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public Long registerDevice() {
        Device device = Device.builder().build();
        deviceRepository.save(device);
        return device.getId();
    }
    
    public Device findDeviceById(Long deviceId) {
        Optional<Device> opt = deviceRepository.findById(deviceId);
        if(opt.isEmpty()) {
            log.warn("단말기가 존재하지 않습니다");
            throw new RuntimeException("에러");
        }
        return opt.get();
    }
}
