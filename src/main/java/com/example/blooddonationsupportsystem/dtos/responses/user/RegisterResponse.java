package com.example.blooddonationsupportsystem.dtos.responses.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterResponse {
    private int status;
    private String message;
    private Object data;

}
