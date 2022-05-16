package sejong.smartnotice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sejong.smartnotice.domain.device.Device;
import sejong.smartnotice.dto.DeviceRegisterDTO;
import sejong.smartnotice.service.DeviceService;

import java.util.List;

@Controller
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

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
}
