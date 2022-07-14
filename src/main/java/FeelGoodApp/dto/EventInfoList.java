package FeelGoodApp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class EventInfoList {

    @Schema(description = "Id of Event", example = "1")
    private Integer id;

    @Schema(description = "name of Event", example = "Elefánt - Budapest Park")
    private String eventName;

    @Schema(description = "date of Event", example = "2022-12-04T18:00:00")
    private LocalDateTime eventDate;

    @Schema(description = "is Event available for public", example = "true")
    private boolean availableForPublic;

    @Schema(description = "price of Event", example = "5000.0")
    private Double eventPrice;

    @Schema(description = "sold tickets for Event. Not necessarily zero because of reserved tickets", example = "200")
    private Integer ticketCounter;

    private MusicVenueForEventsInfo musicVenue;

}
