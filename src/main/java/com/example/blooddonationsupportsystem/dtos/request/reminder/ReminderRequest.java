package com.example.blooddonationsupportsystem.dtos.request.reminder;

import com.example.blooddonationsupportsystem.utils.ReminderType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReminderRequest {
    @NotNull(message = "Cần cung cấp ID người dùng")
    private Integer userId;

    @NotNull(message = "Cần cung cấp ngày tiếp theo")
    @FutureOrPresent(message = "Ngày tiếp theo là hiện tại hoặc tương lai")
    private LocalDate nextDate;

    @NotNull(message = "Cần cung cấp loại thông báo")
    private ReminderType reminderType;

    @NotBlank(message = "Cần cung cấp lời nhắn")
    @Size(max = 255, message = "Lời nhắn tối đa 255 ký tự")
    private String message;

    private Boolean sent = false;

}
