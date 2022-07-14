package FeelGoodApp.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDateUpdateCommand {

    @NotNull(message = "must be not null")
    @Schema(description = "new date of Event", example = "2022-12-04T18:00:00")
    private LocalDateTime eventDate;

}
