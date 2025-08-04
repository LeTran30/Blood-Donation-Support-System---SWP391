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

    @NotNull(message = "Cần cung cấp thông tin về bệnh lây truyền qua máu")
    @Schema(description = "Whether the donor has a blood transmitted disease", example = "false")
    private Boolean hasBloodTransmittedDisease;

    @NotNull(message = "Cần cung cấp thông tin về bệnh mãn tính")
    @Schema(description = "Whether the donor has a chronic disease", example = "false")
    private Boolean hasChronicDisease;

    @NotNull(message = "Cần cung cấp thông tin về các loại thuốc đang sử dụng")
    @Schema(description = "Current medications the donor is taking", example = "None")
    private String currentMedications;

    @NotNull(message = "Cần cung cấp thông tin về việc xăm hình hoặc châm cứu")
    @Schema(description = "Whether the donor has had a tattoo or acupuncture in the last 6 months", example = "false")
    private Boolean hasTattooAcupuncture;

    @NotNull(message = "Cần cung cấp thông tin về việc tiêm vắc-xin gần đây")
    @Schema(description = "Whether the donor has had a vaccine in the last 4 weeks", example = "false")
    private Boolean hasRecentVaccine;

    @NotNull(message = "Cần cung cấp thông tin về việc đi nước ngoài")
    @Schema(description = "Whether the donor has traveled abroad in the last 6 months", example = "false")
    private Boolean hasTravelAbroad;

    @NotNull(message = "Cần cung cấp thông tin về việc quan hệ tình dục không an toàn")
    @Schema(description = "Whether the donor has had unsafe sex in the last 6 months", example = "false")
    private Boolean hasUnsafeSex;

    @NotNull(message = "Cần cung cấp thông tin về lần hiến máu đầu tiên")
    @Schema(description = "Whether this is the donor's first blood donation", example = "true")
    private Boolean isFirstBlood;

    @Schema(description = "Whether the donor is pregnant or breastfeeding (for female donors)", example = "false")
    private Boolean isPregnantOrBreastfeeding;

    @Schema(description = "Whether the donor is menstruating (for female donors)", example = "false")
    private Boolean isMenstruating;
}