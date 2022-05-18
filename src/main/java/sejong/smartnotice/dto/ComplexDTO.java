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

    private int alert_fire;
    private int alert_user;
    private int alert_motion;

    private int status_ok;
    private int status_error;
    private int status_emergency;
}
