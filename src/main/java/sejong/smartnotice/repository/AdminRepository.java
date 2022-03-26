package sejong.smartnotice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sejong.smartnotice.domain.member.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
}
