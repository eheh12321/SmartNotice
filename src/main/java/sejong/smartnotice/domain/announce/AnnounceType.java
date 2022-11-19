package sejong.smartnotice.domain.announce;

import lombok.Getter;

@Getter
public enum AnnounceType {
    VOICE("음성 방송"),
    TEXT("문자 방송");

    private final String name;

    AnnounceType(String name) {
        this.name = name;
    }
}
