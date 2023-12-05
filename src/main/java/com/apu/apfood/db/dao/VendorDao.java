package com.apu.apfood.db.dao;

import com.apu.apfood.db.models.Vendor;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Alex
 */
public class VendorDao extends APFoodDao<Vendor> {
    private static final String VENDOR_FILEPATH = "/src/main/java/com/apu/apfood/db/datafiles/VendorUsers.txt";
    private static final String HEADERS = "id| userId| vendor\n";
    
    public VendorDao() {
        super(VENDOR_FILEPATH, HEADERS);
    }
    
    public List<Vendor> getAllVendors() {
        List<String[]> rawData = super.getAll();
        return rawData.stream()
                      .map(this::deserialize)
                      .collect(Collectors.toList());
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
    
    @Override
    protected String serialize(Vendor vendor) {
        return "";
    }
    
    @Override
    protected Vendor deserialize(String[] data) {
        int id = Integer.parseInt(data[0].trim());
        int userId = Integer.parseInt(data[1].trim());
        String vendorName = data[2].trim();
        return new Vendor(id, userId, vendorName);
    }
    
    @Override
    public void update(Vendor vendor) {
        
    }
}
