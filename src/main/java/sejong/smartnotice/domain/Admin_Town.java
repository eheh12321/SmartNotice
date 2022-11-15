package sejong.smartnotice.domain;

import lombok.*;
import sejong.smartnotice.domain.member.Admin;

import javax.persistence.*;

@Getter @Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Admin_Town {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_town_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id", nullable = false)
    private Admin admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "town_id", nullable = false)
    private Town town;

    @Override
    public String toString() {
        return town.getName();
    }
}
