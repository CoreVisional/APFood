package com.apu.apfood.db.dao;

import com.apu.apfood.db.models.Menu;
import com.apu.apfood.helpers.FileHelper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Alex
 */
public class MenuDao extends APFoodDao<Menu> {
    private static final String MENU_FILEPATH = "/src/main/java/com/apu/apfood/db/datafiles/vendors/";
    private static final String HEADERS = "id| name| type| price\n";
    private FileHelper fileHelper = new FileHelper();
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
    
    public void updateMenuItems(String vendorName, List<Menu> menus)
    {
        this.filePath = getFullPath(MENU_FILEPATH + vendorName + "/Menu.txt");
        try {
            // Open the file with WRITE mode, which truncates the file to size 0
            Files.newBufferedWriter(Path.of(filePath), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        String[] lines = new String[menus.size()];
        for (int i = 0; i < menus.size(); i++) {
            Menu menu = menus.get(i);
            fileHelper.writeFile(filePath, new File(filePath),HEADERS, menu.getMenuName() + "| " + menu.getMenuType() + "| " + menu.getPrice() + "\n");
        }
        
        
    }
    
    public Menu getMenuItem(String vendorName, int id)
    {
        Menu menu = new Menu();
        this.filePath = getFullPath(MENU_FILEPATH + vendorName + "/Menu.txt");
        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fileReader);
            br.readLine(); // Skip first row
            String row;

            while ((row = br.readLine()) != null) {
                String[] rowArray = row.split("\\| ");
                int menuId = Integer.parseInt(rowArray[0]);
                if (menuId == id) {
                    menu = new Menu(menuId, rowArray[1], rowArray[2], Double.parseDouble(rowArray[3]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return menu;
    }
    
    public String getFoodName(String vendorName, int id)
    {
        String foodName = "";
        this.filePath = getFullPath(MENU_FILEPATH + vendorName + "/Menu.txt");
        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fileReader);
            br.readLine(); // Skip first row
            String row;

            while ((row = br.readLine()) != null) {
                String[] rowArray = row.split("\\| ");
                int menuId = Integer.parseInt(rowArray[0]);
                if (menuId == id) {
                    foodName = rowArray[1];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return foodName;
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
