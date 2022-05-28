package sejong.smartnotice.dto;

import lombok.*;
import sejong.smartnotice.domain.EmergencyAlert;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.announce.Announce;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.User;

import java.util.List;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplexDTO {

    private Town town;

    private List<User> userList;
    private List<Admin> adminList;
    private List<Announce> announceList;
    private List<EmergencyAlert> alertList;

    private int townAdminCnt; // 마을 관리자 수
    
    private int alert_fire;
    private int alert_user;
    private int alert_motion;

    private int status_notConnected; // 단말기 미연결
    private int status_error_mqtt; // 통신 장애
    private int status_error_sensor; // 센서 장애
}
