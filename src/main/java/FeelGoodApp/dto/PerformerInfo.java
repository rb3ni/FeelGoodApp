package FeelGoodApp.dto;

import FeelGoodApp.domain.enums.GenreType;
import FeelGoodApp.domain.enums.PartnerLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PerformerInfo {

    @Schema(description = "Id of the Performer", example = "1")
    private Integer id;

    @Schema(description = "name of Performer", example = "Elefánt")
    private String name;

    @Schema(description = "email of Performer", example = "elefant@gmail.com")
    private String email;

    @Schema(description = "contact phone number of Performer", example = "06-20-345-4455")
    private String contactPhoneNumber;

    @Schema(description = "genre of Performer", example = "ROCK")
    private GenreType genre;

    @Schema(description = "partner level of Performer", example = "TIER_2")
    private PartnerLevel partnerLevel;

    private List<EventInfoList> events;

}
