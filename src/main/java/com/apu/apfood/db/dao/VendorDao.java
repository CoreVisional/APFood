package com.apu.apfood.db.dao;

import com.apu.apfood.db.models.Menu;
import com.apu.apfood.db.models.Vendor;
import java.io.BufferedReader;
import java.io.FileReader;
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
    
    public String getVendorName(int id)
    {
        String vendorName = "";
        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fileReader);
            br.readLine(); // Skip first row
            String row;

            while ((row = br.readLine()) != null) {
                String[] rowArray = row.split("\\| ");
                int userID = Integer.parseInt(rowArray[1]);
                if (userID == id) {
                    vendorName = rowArray[2];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vendorName;
    }
    
    public Menu getMenu(String vendorName)
    {
        Menu menu = new Menu();
        try 
        {
            String filePath = "/src/main/java/com/apu/apfood/db/datafiles/vendors/" + vendorName + "//Menu.txt";
            FileReader fileReader = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fileReader);
            br.readLine(); // Skip first row
            String row;

            while ((row = br.readLine()) != null) 
            {
                String[] rowArray = row.split("\\| ");
                menu.addFood(rowArray[1],rowArray[2],Double.valueOf(rowArray[3]));

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return menu;
    }
    
    @Override
    protected String serialize(Vendor vendor) {
        return "";
    }
    
    @Override
    public void update(Vendor vendor) {
        
    }    
}
