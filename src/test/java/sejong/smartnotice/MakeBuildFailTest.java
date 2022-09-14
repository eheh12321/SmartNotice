package sejong.smartnotice;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class MakeBuildFailTest {

    @Test
    public void 실패하는_테스트() {
        Assertions.assertThat(1).isEqualTo(2);
    }
}
