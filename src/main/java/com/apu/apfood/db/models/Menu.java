package com.apu.apfood.db.models;

import com.apu.apfood.db.models.common.BaseModel;

/**
 *
 * @author Alex
 */
public class Menu extends BaseModel {
    private String menuName, menuType;
    private double price;
    
    public Menu() {
        
    }
    
    public Menu(int id, String menuName, String menuType, double price) {
        setId(id);
        this.menuName = menuName;
        this.menuType = menuType;
        this.price = price;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuType() {
        return menuType;
    }

    public void setMenuType(String menuType) {
        this.menuType = menuType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
