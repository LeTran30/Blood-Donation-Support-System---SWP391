package com.example.blooddonationsupportsystem.dtos.request.bloodRequest;

import com.example.blooddonationsupportsystem.utils.UrgencyLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Data
public class BloodRequestAllocationRequest {

    @NotNull(message = "Nhóm máu không được để trống")
    private Integer bloodTypeId;

    @NotNull(message = "Thành phần máu không được để trống")
    private Integer bloodComponentId;

    @Schema(description = "Urgency level of the request", example = "HIGH")
    @NotNull(message = "Mức độ nghiêm trọng không được để trống")
    private UrgencyLevel urgencyLevel;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng không được dưới 1")
    private Integer quantity;
}
