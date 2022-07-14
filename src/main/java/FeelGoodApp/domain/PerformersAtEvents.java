package FeelGoodApp.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "performer_at_event")
public class PerformersAtEvents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "performer_at_event_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "performer_id")
    private Performer performer;

    @Column(name = "headliner")
    private boolean isHeadliner;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

}
