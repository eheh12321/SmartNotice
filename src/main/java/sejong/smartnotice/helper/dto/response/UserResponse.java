package sejong.smartnotice.helper.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.domain.member.UserAccount;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String tel;
    private String birth;
    private String address;
    private String townName;
    private List<String> supporterList;

    public static UserResponse from(User user) {
        List<String> supporterList = user.getSupporterList().stream()
                .map(UserAccount::getName)
                .collect(Collectors.toList());

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .tel(user.getTel())
                .birth(user.getBirth())
                .address(user.getAddress())
                .townName(user.getTown().getName())
                .supporterList(supporterList).build();
    }
}
