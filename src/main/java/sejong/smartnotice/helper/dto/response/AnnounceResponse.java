package sejong.smartnotice.helper.dto.response;

import lombok.*;
import sejong.smartnotice.domain.announce.Announce;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class AnnounceResponse {

    private Long id;
    private String title;
    private String producer;
    private String src;
    private String content;
    private String time;
    private String status;
    private String category;
    private List<String> townList;

    public static AnnounceResponse from(Announce announce) {
        List<String> townList = announce.getTownAnnounceList().stream()
                .map(townAnnounce -> townAnnounce.getTown().getName())
                .collect(Collectors.toList());

        return AnnounceResponse.builder()
                .id(announce.getId())
                .title(announce.getTitle())
                .producer(announce.getProducer())
                .src(announce.getFullPath())
                .content(announce.getContents())
                .status(announce.getStatus().name())
                .category(announce.getCategory().name())
                .time(announce.getTime().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")))
                .townList(townList).build();
    }
}
