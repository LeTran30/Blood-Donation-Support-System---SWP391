    package com.example.blooddonationsupportsystem.dtos.request.distanceSearch;

    import jakarta.validation.constraints.NotNull;
    import jakarta.validation.constraints.Positive;
    import lombok.*;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class DistanceSearchRequest {
        @NotNull(message = "ID người dùng không được để trống")
        @Positive(message = "ID người dùng phải là một số nguyên dương")
        private Integer userId;

        @NotNull(message = "Nhóm máu không được để trống")
        @Positive(message = "ID nhóm máu phải là một số nguyên dương")
        private Integer bloodTypeId;

        @NotNull(message = "Vĩ độ không được để trống")
        private Double latitude;

        @NotNull(message = "Kinh độ không được để trống")
        private Double longitude;
    }