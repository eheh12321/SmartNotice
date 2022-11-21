package sejong.smartnotice.helper.function;

import sejong.smartnotice.domain.TownData;

@FunctionalInterface
public interface TownDataAction {
    TownData action(TownData townData);
}
