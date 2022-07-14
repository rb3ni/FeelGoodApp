package FeelGoodApp.dto.command;

import FeelGoodApp.domain.enums.GenreType;
import FeelGoodApp.domain.enums.PartnerLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerformerCreateCommand {

    @NotBlank(message = "must be not blank")
    @Schema(description = "name of Performer", example = "Elefánt")
    private String name;

    @Email(message = "must be an email")
    @Schema(description = "email of Performer", example = "elefant@gmail.com")
    private String email;

    @NotBlank(message = "must be not blank")
    @Size(max = 15, message = "size must be between 0 and 15")
    @Schema(description = "contact phone number of Performer", example = "06-20-345-4455")
    private String contactPhoneNumber;

    @NotNull(message = "must be not null")
    @Schema(description = "genre of Performer", example = "ROCK")
    private GenreType genre;

    @NotNull(message = "must be not null")
    @Schema(description = "partner level of Performer", example = "TIER_2")
    private PartnerLevel partnerLevel;

}
