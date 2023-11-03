package com.apu.apfood.db.models;

import com.apu.apfood.db.models.common.BaseModel;

/**
 *
 * @author Alex
 */
public class Vendor extends BaseModel {
    private int userId;
    private String vendorName;
    
    public Vendor() {
        
    }
    
    public Vendor(int id, int userId, String vendorName) {
        setId(id);
        this.userId = userId;
        this.vendorName = vendorName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }
}
