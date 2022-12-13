package sejong.smartnotice.helper.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DataTableResponse<T> {
    int draw;
    int recordsTotal;
    int recordsFiltered;
    List<T> data;
}
