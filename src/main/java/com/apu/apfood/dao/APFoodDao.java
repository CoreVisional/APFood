package com.apu.apfood.dao;

import com.apu.apfood.models.common.BaseModel;

/**
 *
 * @author Alex
 * @param <T>
 */
public abstract class APFoodDao<T extends BaseModel> {
    protected String filePath;
    
    public APFoodDao() {
    }
    
    public void add(T entity) {
        //
    }
    
    public abstract void update(T entity);
}
