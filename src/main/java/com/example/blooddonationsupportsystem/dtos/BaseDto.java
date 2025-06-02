package com.example.blooddonationsupportsystem.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

import java.time.LocalDate;

@Data//toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class BaseDto {
    @JsonProperty("created_at")
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDate createdAt;

    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @JsonProperty("updated_at")
    private LocalDate updatedAt;
}
