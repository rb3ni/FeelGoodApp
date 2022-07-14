package FeelGoodApp.domain;

import FeelGoodApp.domain.enums.GenreType;
import FeelGoodApp.domain.enums.PartnerLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "performer")
public class Performer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "performer_id")
    private Integer id;

    @Column(unique = true, name = "performer_name")
    private String name;

    @Column(name = "performer_email")
    private String email;

    @Column(name = "contact_phone_number")
    private String contactPhoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "performer_genre")
    private GenreType genre;

    @Enumerated(EnumType.STRING)
    @Column(name = "performer_partner_level")
    private PartnerLevel partnerLevel;

    @OneToMany(mappedBy = "performer")
    List<PerformersAtEvents> performersAtEvents;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
