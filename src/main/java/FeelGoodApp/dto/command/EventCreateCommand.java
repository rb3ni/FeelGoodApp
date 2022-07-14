package FeelGoodApp.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventCreateCommand {

    @Future(message = "must be a future date")
    @NotNull(message = "must be not null")
    @Schema(description = "date of Event", example = "2022-12-04T18:00:00")
    private LocalDateTime eventDate;

    @Positive(message = "must be positive number")
    @Max(value = 200, message = "must be less than or equal to 200")
    @NotNull(message = "must be not null")
    @Schema(description = "sold tickets for Event. Not necessarily zero because of reserved tickets", example = "200")
    private Integer ticketCounter;

    @Positive(message = "must be positive number")
    @NotNull(message = "must be not null")
    @Schema(description = "id of MusicVenue", example = "1")
    private Integer musicVenueId;

}
