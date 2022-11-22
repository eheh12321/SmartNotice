package sejong.smartnotice.helper.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SingleResponse<T> {
    T data;
    String message;
}
