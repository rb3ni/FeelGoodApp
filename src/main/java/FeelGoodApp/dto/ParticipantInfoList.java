package FeelGoodApp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ParticipantInfoList {

    @Schema(description = "Id of Participant", example = "1")
    private Integer id;

    @Schema(description = "name of Participant", example = "Csendrői Szabolcs")
    private String name;

    @Schema(description = "email of Participant", example = "csendszab@gmail.com")
    private String email;

}
