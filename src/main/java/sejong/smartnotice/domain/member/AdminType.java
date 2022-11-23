package sejong.smartnotice.domain.member;

import lombok.Getter;

@Getter
public enum AdminType {
    SUPER("최고 관리자"),
    ADMIN("마을 관리자");

    private final String name;

    AdminType(String name) {
        this.name = name;
    }
}
