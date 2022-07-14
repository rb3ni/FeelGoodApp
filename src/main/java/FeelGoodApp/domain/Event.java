package FeelGoodApp.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Integer id;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @Column(name = "available_for_public")
    private boolean availableForPublic;

    @Column(name = "event_price")
    private Double eventPrice;

    @Column(name = "number_of_sold_tickets")
    private Integer ticketCounter;

    @ManyToOne
    @JoinColumn(name = "music_venue_id")
    private MusicVenue musicVenue;

    @OneToMany(mappedBy = "event")
    List<PerformersAtEvents> performersAtEvents;

    @OneToMany(mappedBy = "event")
    private List<Participant> participants;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
