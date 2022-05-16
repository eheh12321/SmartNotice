package sejong.smartnotice.dto;

import lombok.*;
import sejong.smartnotice.domain.announce.Announce;

import java.time.format.DateTimeFormatter;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnounceModalDTO {

    private String title;
    private String producer;
    private String src;
    private String contents;
    private String time;

    public static AnnounceModalDTO entityToDto(Announce announce) {
        return AnnounceModalDTO.builder()
                .title(announce.getTitle())
                .producer(announce.getProducer())
                .src(announce.getFullPath())
                .contents(announce.getContents())
                .time(announce.getTime().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))).build();
    }
}
