package sejong.smartnotice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sejong.smartnotice.domain.Town;

@Repository
public interface TownRepository extends JpaRepository<Town, Long> {
}
