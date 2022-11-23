package sejong.smartnotice.helper.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class Response<T> {
    List<T> data;
    String message;
}
