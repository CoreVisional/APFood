package com.apu.apfood.services;

import com.apu.apfood.db.dao.NotificationDao;
import com.apu.apfood.db.dao.UserDao;
import com.apu.apfood.db.dao.VendorDao;
import com.apu.apfood.db.models.Menu;
import com.apu.apfood.db.models.User;
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
    private User vendor;
    private UserDao userDao = new UserDao();
    private NotificationDao notificationDao = new NotificationDao();
    private final VendorDao vendorDao = new VendorDao();
    
    public VendorService(User vendor) {
        this.vendor = vendor;
    }
    
    public VendorService() {
        
    }
    
    public List<String> getDistinctVendorNames() {
        List<Vendor> allVendors = vendorDao.getAllVendors();

        Set<String> vendorNames = allVendors.stream()
                                                  .map(Vendor::getVendorName)
                                                  .collect(Collectors.toSet());

        return new ArrayList<>(vendorNames);
    }
    
    public String getVendorName()
    {
        String vendorName = vendorDao.getVendorName(vendor.getId());
        return vendorName;
    }
    
    public Menu getMenu(String vendorName)
    {
         Menu menu = vendorDao.getMenu(vendorName);
        return menu;
    }
}
