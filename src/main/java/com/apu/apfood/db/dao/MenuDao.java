package com.apu.apfood.db.dao;

import com.apu.apfood.db.models.Menu;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Alex
 */
public class MenuDao extends APFoodDao<Menu> {
    private static final String MENU_FILEPATH = "/src/main/java/com/apu/apfood/db/datafiles/vendors/";
    private static final String HEADERS = "id| name| type| price\n";
    
    public MenuDao() {
        super(MENU_FILEPATH, HEADERS);
    }
    
    public List<Menu> getAllMenuItems(String vendorName) {
        this.filePath = getFullPath(MENU_FILEPATH + vendorName + "/Menu.txt");
        
        List<String[]> rawData = super.getAll();
        
        List<Menu> menus = rawData.stream()
                                  .map(this::deserialize)
                                  .collect(Collectors.toList());

        return menus;
    }
    
    @Override
    protected String serialize(Menu menu) {
        return "";
    }
    
    @Override
    protected Menu deserialize(String[] data) {
        int id = Integer.parseInt(data[0].trim());
        String name = data[1].trim();
        String type = data[2].trim();
        double price = Double.parseDouble(data[3].trim());

        return new Menu(id, name, type, price);
    }
    
    @Override
    public void update(Menu menu) {
        
    }   
}
