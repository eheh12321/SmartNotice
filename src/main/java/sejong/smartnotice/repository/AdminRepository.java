package sejong.smartnotice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sejong.smartnotice.domain.member.Admin;

import java.util.List;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    @Query("select a from Admin a where a.name like %?1%")
    List<Admin> findByNameContaining(String name);

    Admin findByName(String name);

    Admin findByLoginId(String loginId);

    Admin findByTel(String tel);

    boolean existsAdminByLoginIdOrTel(String loginId, String tel);
}
