package FeelGoodApp.domain;

import FeelGoodApp.domain.enums.VenueType;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "music_venue")
public class MusicVenue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "music_venue_id")
    private Integer id;

    @Column(unique = true, name = "music_venue_name")
    private String name;

    @Column(name = "contact_phone_number")
    private String contactPhoneNumber;

    @Column(name = "music_venue_address")
    private String address;

    @Column(name = "music_venue_capacity")
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "music_venue_type")
    private VenueType type;

    @OneToMany(mappedBy = "musicVenue")
    private List<Event> events;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
