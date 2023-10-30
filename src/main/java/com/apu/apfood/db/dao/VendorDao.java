package com.apu.apfood.db.dao;

import com.apu.apfood.db.models.Vendor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alex
 */
public class VendorDao extends APFoodDao<Vendor> {
    private static final String VENDOR_FILEPATH = "/src/main/java/com/apu/apfood/db/datafiles/Vendors.txt";
    private static final String HEADERS = "id| userId| vendor_name\n";
    
    public VendorDao() {
        super(VENDOR_FILEPATH, HEADERS);
    }

    public List<Vendor> getAllVendors() {
        List<String[]> rawData = super.getAll();
        List<Vendor> vendors = new ArrayList<>();
        
        for (String[] data : rawData) {
            int id = Integer.parseInt(data[0].trim());
            int userId = Integer.parseInt(data[1].trim());
            String vendorName = data[2].trim();

            Vendor vendor = new Vendor(id, userId, vendorName);
            vendors.add(vendor);
        }

        return vendors;
    }
    
    @Override
    protected String serialize(Vendor vendor) {
        return "";
    }
    
    @Override
    public void update(Vendor vendor) {
        
    }    
}
