package FeelGoodApp.dto.command;

import FeelGoodApp.domain.enums.VenueType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicVenueCreateCommand {

    @NotBlank(message = "must be not blank")
    @Size(max = 50, message = "the size must be between 0 and 50")
    @Schema(description = "name of MusicVenue", example = "Budapest Park")
    private String name;

    @NotBlank(message = "must be not blank")
    @Size(max = 15, message = "the size must be between 0 and 15")
    @Schema(description = "contact phone number of MusicVenue", example = "06-30-544-4445")
    private String contactPhoneNumber;

    @NotBlank(message = "must be not blank")
    @Size(max = 150, message = "the size must be between 0 and 150")
    @Schema(description = "address of MusicVenue", example = "1111, Budapest, Zenebona utca 1.")
    private String address;

    @Min(value = 200, message = "must be more than or equal to 200")
    @NotNull(message = "must be not null")
    @Schema(description = "capacity of MusicVenue", example = "4000")
    private Integer capacity;

    @NotNull(message = "must be not null")
    @Schema(description = "type of MusicVenue", example = "BANDSTAND")
    private VenueType type;

}
