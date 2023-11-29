/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apu.apfood.db.enums;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Maxwell
 */
public enum Rating {
   FIVE_STAR,
   FOUR_STAR,
   THREE_STAR,
   TWO_STAR,
   ONE_STAR;
   
    @Override
    public String toString() {
        return StringUtils.capitalize(this.name().toLowerCase());
    }
    
    
}
