package com.example.blooddonationsupportsystem.dtos.responses.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticationResponse {
    private int status;
    private String message;
    private ResponseData data;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ResponseData {
        private UserResponse user;
        @JsonProperty("token")
        private String token;

        @JsonProperty("refresh_token")
        private String refreshToken;
    }
}
