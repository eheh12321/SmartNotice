package sejong.smartnotice.domain.announce;

import lombok.Getter;

@Getter
public enum AnnounceStatus {
    READY("방송 준비중"),
    ABLE("청취 가능"),
    DISABLE("청취 불가능"),
    ERROR("방송 실패");

    private final String name;

    AnnounceStatus(String name) {
        this.name = name;
    }
}
