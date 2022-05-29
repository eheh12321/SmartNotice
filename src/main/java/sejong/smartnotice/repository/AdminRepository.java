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

    @Query("select distinct a from Admin a left join fetch a.atList at left join fetch at.town t")
    List<Admin> findAllWithTown();

    @Query("select distinct a from Admin a join fetch a.atList at where at.town.id=?1")
    List<Admin> findAdminByTown(Long townId);

    @Query("select a from Admin a join fetch a.atList at join fetch at.town where at.admin.id=?1")
    Admin findAdminWithTown(Long id);

    Admin findByName(String name);

    Admin findByAccountLoginId(String loginId);

    Admin findByTel(String tel);

    boolean existsAdminByTelOrAccountLoginId(String tel, String loginId);
}
