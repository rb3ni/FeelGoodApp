package FeelGoodApp.dto;

import FeelGoodApp.domain.enums.VenueType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MusicVenueInfo {

    @Schema(description = "Id of MusicVenue", example = "1")
    private Integer id;

    @Schema(description = "name of MusicVenue", example = "Budapest Park")
    private String name;

    @Schema(description = "contact phone number of MusicVenue", example = "06-30-544-4445")
    private String contactPhoneNumber;

    @Schema(description = "address of MusicVenue", example = "1111, Budapest, Zenebona utca 1.")
    private String address;

    @Schema(description = "capacity of MusicVenue", example = "4000")
    private Integer capacity;

    @Schema(description = "type of MusicVenue", example = "BANDSTAND")
    private VenueType type;

}
