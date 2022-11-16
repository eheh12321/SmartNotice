package sejong.smartnotice.helper.dto;

import lombok.*;
import sejong.smartnotice.domain.AlertType;
import sejong.smartnotice.domain.EmergencyAlert;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.announce.Announce;
import sejong.smartnotice.domain.member.Admin;
import sejong.smartnotice.domain.member.User;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
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

    private boolean emergency_fire; // 화재 발생 유무
    private boolean notConfirmedAlert; // 미확인 긴급알림 존재 유무

    public static ComplexDTO from(Town town,
                                  List<Admin> adminList,
                                  List<User> userList,
                                  List<Announce> announceList) {

        int townAdminCnt = town.getAtList().size();
        int notConnectedCnt = 0;
        int mqttErrorCnt = 0;
        int sensorErrorCnt = 0;
        int userAlertCnt = 0;
        int fireAlertCnt = 0;
        int motionAlertCnt = 0;
        List<EmergencyAlert> alertList = new ArrayList<>();

        for (User user : userList) {
            for (EmergencyAlert alert : user.getAlertList()) {
                alertList.add(alert);
                if (alert.getAlertType() == AlertType.USER) {
                    userAlertCnt++;
                } else if (alert.getAlertType() == AlertType.FIRE) {
                    fireAlertCnt++;
                } else {
                    motionAlertCnt++;
                }
            }
            if (user.getDevice() != null) {
                if (user.getDevice().isError_sensor()) {
                    sensorErrorCnt++;
                }
                if (user.getDevice().isError_mqtt()) {
                    mqttErrorCnt++;
                }
            } else {
                notConnectedCnt++;
            }
        }

        return ComplexDTO.builder()
                .town(town)
                .userList(userList)
                .adminList(adminList)
                .announceList(announceList)
                .alertList(alertList)
                .townAdminCnt(townAdminCnt)
                .alert_fire(fireAlertCnt)
                .alert_user(userAlertCnt)
                .alert_motion(motionAlertCnt)
                .status_notConnected(notConnectedCnt)
                .status_error_mqtt(mqttErrorCnt)
                .status_error_sensor(sensorErrorCnt).build();
    }
}
