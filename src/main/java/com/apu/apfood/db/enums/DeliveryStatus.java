package com.apu.apfood.db.enums;

import org.codehaus.plexus.util.StringUtils;

/**
 *
 * @author Alex
 */
public enum DeliveryStatus {
    COMPLETED,
    ONGOING;
    
    @Override
    public String toString() {
        return StringUtils.capitalise(this.name().toLowerCase());
    }
}
