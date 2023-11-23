/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apu.apfood.db.models;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Asus
 */
public class Menu {
    private List<String> name;
    private List<String> type;
    private List<Double> price;
    private int count;
    public Menu()
    {
        name = new ArrayList<>();
        type = new ArrayList<>();
        price = new ArrayList<>();
        count = 0;
    }
    
    public void addFood(String name, String type, Double price)
    {
        this.name.add(name);
        this.type.add(type);
        this.price.add(price);
        this.count++;
    }
    
    public List<String[]> getAll() {
        List<String[]> all = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String[] foodDetails = {name.get(i), type.get(i), String.valueOf(price.get(i))};
            all.add(foodDetails);
        }
        return all;
    }
    
    
    public String getName(int id)
    {
        return name.get(id);
    }
    public String getType(int id)
    {
        return type.get(id);
    }
    public Double getPrice(int id)
    {
        return price.get(id);
    }
}
