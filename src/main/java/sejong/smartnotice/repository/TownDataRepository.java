package sejong.smartnotice.repository;

import org.springframework.data.repository.CrudRepository;
import sejong.smartnotice.domain.TownData;

public interface TownDataRepository extends CrudRepository<TownData, Long> {
}
