package FeelGoodApp.dto.command;

import FeelGoodApp.domain.enums.PartnerLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerformerPartnerLevelUpdateCommand {

    @NotNull(message = "must be not null")
    @Schema(description = "new partner level of Performer", example = "TIER_2")
    private PartnerLevel partnerLevel;

}
