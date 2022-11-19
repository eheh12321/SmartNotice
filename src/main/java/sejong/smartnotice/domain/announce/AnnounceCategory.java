package sejong.smartnotice.domain.announce;

import lombok.Getter;

@Getter
public enum AnnounceCategory {
    NORMAL("일반 방송"),
    DISASTER("재난 방송");

    private final String name;

    AnnounceCategory(String name) {
        this.name = name;
    }
}
