package FeelGoodApp.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantCreateCommand {

    @NotBlank(message = "must be not blank")
    @Schema(description = "name of Participant", example = "Csendrői Szabolcs")
    private String name;

    @Email(message = "must be an email")
    @Schema(description = "email of Participant", example = "csendszab@gmail.com")
    private String email;

}
