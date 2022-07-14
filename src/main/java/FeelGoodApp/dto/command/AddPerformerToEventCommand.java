package FeelGoodApp.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddPerformerToEventCommand {

    @Positive(message = "must be positive number")
    @NotNull(message = "must be not null")
    @Schema(description = "id of Performer", example = "1")
    private Integer performerId;

    @NotNull(message = "must be not null")
    @Schema(description = "is headliner on Event", example = "true")
    private Boolean isHeadliner;

}
