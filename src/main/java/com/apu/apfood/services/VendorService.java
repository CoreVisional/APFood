package com.apu.apfood.services;

import com.apu.apfood.db.dao.NotificationDao;
import com.apu.apfood.db.dao.UserDao;
import com.apu.apfood.db.dao.VendorDao;
import com.apu.apfood.db.models.Menu;
import com.apu.apfood.db.models.User;
import com.apu.apfood.db.dao.MenuDao;
import com.apu.apfood.db.dao.VendorDao;
import com.apu.apfood.db.models.Menu;
import com.apu.apfood.db.models.Vendor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Alex
 */
public class VendorService {
    private User vendor;
    private UserDao userDao = new UserDao();
    private NotificationDao notificationDao = new NotificationDao();
    private VendorDao vendorDao = new VendorDao();
    private MenuDao menuDao = new MenuDao();

    public VendorService(User vendor) {
        this.vendor = vendor;
    }
    
    public VendorService() {
        
    }
    
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
    
    public String getVendorName()
    {
        String vendorName = vendorDao.getVendorName(vendor.getId());
        return vendorName;
    }
    
    public List<Menu> getVendorMenuItems(String vendorName) {
        return menuDao.getAllMenuItems(vendorName);
    }
    
     public static List<Menu> convertJTableToMenuList(JTable table) {
        List<Menu> menuList = new ArrayList<>();

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int rowCount = model.getRowCount();
        int idColumnIndex = 0; // Adjust this index based on the actual column order

        for (int i = 0; i < rowCount; i++) {
            int id = Integer.parseInt(model.getValueAt(i, idColumnIndex).toString());
            String menuName = model.getValueAt(i, 1).toString(); // Assuming 1 is the index of the menuName column
            String menuType = model.getValueAt(i, 2).toString(); // Assuming 2 is the index of the menuType column
            double price = Double.parseDouble(model.getValueAt(i, 3).toString()); // Assuming 3 is the index of the price column

            Menu menu = new Menu(id, menuName, menuType, price);
            menuList.add(menu);
        }

        return menuList;
    }
    
    public void updateMenuItems (String vendorName, List<Menu> menus)
    {
        menuDao.updateMenuItems(vendorName, menus);
    }
}
