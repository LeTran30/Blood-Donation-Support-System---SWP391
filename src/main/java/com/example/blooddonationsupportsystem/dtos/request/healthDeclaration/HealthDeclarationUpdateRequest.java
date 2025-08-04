package com.example.blooddonationsupportsystem.dtos.request.healthDeclaration;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthDeclarationUpdateRequest {

    @NotNull(message = "Blood transmitted disease information is required")
    @Schema(description = "Whether the donor has a blood transmitted disease", example = "false")
    private Boolean hasBloodTransmittedDisease;

    @NotNull(message = "Chronic disease information is required")
    @Schema(description = "Whether the donor has a chronic disease", example = "false")
    private Boolean hasChronicDisease;

    @Schema(description = "Current medications the donor is taking", example = "None")
    private String currentMedications;

    @NotNull(message = "Tattoo or acupuncture information is required")
    @Schema(description = "Whether the donor has had a tattoo or acupuncture in the last 6 months", example = "false")
    private Boolean hasTattooAcupuncture;

    @NotNull(message = "Recent vaccine information is required")
    @Schema(description = "Whether the donor has had a vaccine in the last 4 weeks", example = "false")
    private Boolean hasRecentVaccine;

    @NotNull(message = "Travel abroad information is required")
    @Schema(description = "Whether the donor has traveled abroad in the last 6 months", example = "false")
    private Boolean hasTravelAbroad;

    @NotNull(message = "Unsafe sex information is required")
    @Schema(description = "Whether the donor has had unsafe sex in the last 6 months", example = "false")
    private Boolean hasUnsafeSex;

    @NotNull(message = "First blood donation information is required")
    @Schema(description = "Whether this is the donor's first blood donation", example = "true")
    private Boolean isFirstBlood;

    @Schema(description = "Whether the donor is pregnant or breastfeeding (for female donors)", example = "false")
    private Boolean isPregnantOrBreastfeeding;

    @Schema(description = "Whether the donor is menstruating (for female donors)", example = "false")
    private Boolean isMenstruating;
}