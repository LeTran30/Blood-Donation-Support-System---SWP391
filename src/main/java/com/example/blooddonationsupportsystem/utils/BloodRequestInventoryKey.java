package com.example.blooddonationsupportsystem.utils;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BloodRequestInventoryKey implements Serializable {
    private Integer requestId;
    private Integer inventoryId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BloodRequestInventoryKey)) return false;
        BloodRequestInventoryKey that = (BloodRequestInventoryKey) o;
        return Objects.equals(requestId, that.requestId) &&
                Objects.equals(inventoryId, that.inventoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId, inventoryId);
    }
}
