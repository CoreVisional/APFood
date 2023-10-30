package com.apu.apfood.services;

import com.apu.apfood.db.dao.VendorDao;
import com.apu.apfood.db.models.Vendor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Alex
 */
public class VendorService {
    
    private final VendorDao vendorDao;
    
    public VendorService(VendorDao vendorDao) {
        this.vendorDao = vendorDao;
    }
    
    public List<String> getDistinctVendorNames() {
        List<Vendor> allVendors = vendorDao.getAllVendors();

        Set<String> vendorNames = allVendors.stream()
                                                  .map(Vendor::getVendorName)
                                                  .collect(Collectors.toSet());

        return new ArrayList<>(vendorNames);
    }
}
