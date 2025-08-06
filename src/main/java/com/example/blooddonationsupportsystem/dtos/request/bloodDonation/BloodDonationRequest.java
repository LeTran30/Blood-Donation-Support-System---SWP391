package com.example.blooddonationsupportsystem.dtos.request.bloodDonation;


import com.example.blooddonationsupportsystem.utils.DonationStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BloodDonationRequest {

    @NotNull(message = "Cần cung cấp ID người dùng.")
    private Integer user;

    @NotNull(message = "Cần cung cấp ngày hiến máu.")
    private LocalDate donationDate;

    @NotNull(message = "Cần cung cấp nhóm máu.")
    private Integer bloodType;

    @NotNull(message = "Cần cung cấp thành phần máu.")
    private Integer bloodComponent;

    @NotNull(message = "Cần cung cấp thể tích máu(ml).")
    @Min(value = 50, message = "Thể tích máu(ml) tối thiểu 50ml.")
    private Integer volumeMl;

    @NotNull(message = "Cần cung cấp trạng thái cuộc hiến máu.")
    private DonationStatus status;

    @NotNull(message = "Cần cung cấp thông tin kiểm tra y tế.")
    private Integer healthCheck;

}
