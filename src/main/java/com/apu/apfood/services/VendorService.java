package com.apu.apfood.services;

import com.apu.apfood.db.dao.MenuDao;
import com.apu.apfood.db.dao.VendorDao;
import com.apu.apfood.db.models.Menu;
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
    private final MenuDao menuDao;
    
    public VendorService(VendorDao vendorDao, MenuDao menuDao) {
        this.vendorDao = vendorDao;
        this.menuDao = menuDao;
    }
    
    public List<String> getDistinctVendorNames() {
        List<Vendor> allVendors = vendorDao.getAllVendors();

        Set<String> vendorNames = allVendors.stream()
                                                  .map(Vendor::getVendorName)
                                                  .collect(Collectors.toSet());

        return new ArrayList<>(vendorNames);
    }
    
    public List<Menu> getVendorMenuItems(String vendorName) {
        return menuDao.getAllMenuItems(vendorName);
    }
}
