package com.example.blooddonationsupportsystem.models;

import com.example.blooddonationsupportsystem.utils.TokenType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "token")
@Builder
public class Token extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tokenId;

    @Column(name = "token")
    private String token;

    @Column(name = "expired")
    private boolean expired;

    @Column(name = "revoked")
    private boolean revoked;

    @Column(name = "refresh_token")
    private String refreshToken;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;
}
