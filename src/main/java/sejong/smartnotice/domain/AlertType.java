package sejong.smartnotice.domain;

import lombok.Getter;

@Getter
public enum AlertType {
    USER("사용자 호출"),
    FIRE("화재 발생"),
    MOTION("동작 감지");

    private final String name;

    AlertType(String name) {
        this.name = name;
    }
}
