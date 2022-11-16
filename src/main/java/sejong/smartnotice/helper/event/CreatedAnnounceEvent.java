package sejong.smartnotice.helper.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import sejong.smartnotice.domain.announce.Announce;

@Getter
public class CreatedAnnounceEvent extends ApplicationEvent {

    private final Announce announce;
    private final byte[] audioContents;

    public CreatedAnnounceEvent(Object source, Announce announce, byte[] audioContents) {
        super(source);
        this.announce = announce;
        this.audioContents = audioContents;
    }
}
