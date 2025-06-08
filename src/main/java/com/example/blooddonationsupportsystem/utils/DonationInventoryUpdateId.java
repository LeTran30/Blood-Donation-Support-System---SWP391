package com.example.blooddonationsupportsystem.utils;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonationInventoryUpdateId implements Serializable {
    private Integer bloodDonation;
    private Integer inventory;
}
