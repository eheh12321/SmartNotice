package sejong.smartnotice.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Town {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "town_id")
    private Long id;

    private String name;
}
