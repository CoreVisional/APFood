package com.apu.apfood.db.enums;

/**
 *
 * @author Alex
 */
public enum DeliveryFee {
    
    BLOCK_A(5.0),
    BLOCK_B(3.0),
    BLOCK_D(2.0),
    BLOCK_E(2.0);

    private final double fee;
    private 

    DeliveryFee(double fee) {
        this.fee = fee;
    }

    public double getFee() {
        return fee;
    }
}
