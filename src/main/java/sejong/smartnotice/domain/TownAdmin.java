package sejong.smartnotice.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import sejong.smartnotice.domain.member.Admin;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@IdClass(TownAdmin.TownAdminId.class)
@Entity
public class TownAdmin {

    // Admin(FK)과 Town(FK)을 이용한 복합키를 PK로 사용
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TownAdminId implements Serializable {
        private Admin admin;
        private Town town;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TownAdminId that = (TownAdminId) o;
            return Objects.equals(admin, that.admin) && Objects.equals(town, that.town);
        }

        @Override
        public int hashCode() {
            return Objects.hash(admin, town);
        }
    }

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id")
    private Admin admin;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "town_id")
    private Town town;

    @CreatedDate
    private LocalDateTime createdAt;

    public TownAdmin(Town town, Admin admin) {
        this.admin = admin;
        this.town = town;
    }

    public void addTownAdmin() {
        town.getTownAdminList().add(this);
        admin.getTownAdminList().add(this);
    }

    public void removeTownAdmin() {
        town.getTownAdminList().remove(this);
        admin.getTownAdminList().remove(this);
    }

    @Override
    public String toString() {
        return String.format("%s-%s", admin.getName(), town.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TownAdmin that = (TownAdmin) o;
        return Objects.equals(admin, that.admin) && Objects.equals(town, that.town);
    }

    @Override
    public int hashCode() {
        return Objects.hash(admin, town);
    }
}
