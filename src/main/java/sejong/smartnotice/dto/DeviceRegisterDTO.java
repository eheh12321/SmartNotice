package sejong.smartnotice.dto;

import lombok.*;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceRegisterDTO {
    private String mac;
    private boolean available;
}
