package FeelGoodApp.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "participant")
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Integer id;

    @Column(name = "participant_name")
    private String name;

    @Column(name = "participant_email")
    private String email;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

}
