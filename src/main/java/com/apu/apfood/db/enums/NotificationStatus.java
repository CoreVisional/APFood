package com.apu.apfood.db.enums;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Alex
 */
public enum NotificationStatus {
    NOTIFIED,
    UNNOTIFIED;
    
    @Override
    public String toString() {
        return StringUtils.capitalize(this.name().toLowerCase());
    }
}
