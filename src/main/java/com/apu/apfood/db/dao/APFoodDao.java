package com.apu.apfood.db.dao;

import com.apu.apfood.helpers.FileHelper;
import com.apu.apfood.db.models.common.BaseModel;
import java.io.File;
import java.util.List;

/**
 *
 * @author Alex
 * @param <T> Generic type parameter to be used on models
 */
public abstract class APFoodDao<T extends BaseModel> {
    
    protected static final String BASE_PATH = System.getProperty("user.dir");
    protected String filePath;
    protected FileHelper fileHelper;
    protected String fileHeaders;
    
    public APFoodDao() {
        
    }

    public APFoodDao(String filePath, String fileHeaders) {
        this.filePath = BASE_PATH + filePath;
        this.fileHelper = new FileHelper();
        this.fileHeaders = fileHeaders;
    }
    
    public void add(T entity) {
        int newId = fileHelper.generateID(filePath, new File(filePath));
        entity.setId(newId);

        String serializedData = serialize(entity);
        fileHelper.writeFile(filePath, new File(filePath), fileHeaders, serializedData);
    }
    
    public List<String[]> getAll() {
        return fileHelper.readFile(filePath);
    }
    
    protected String getFullPath(String relativePath) {
        return BASE_PATH + relativePath;
    }
    
    protected abstract String serialize(T entity);
    
    protected abstract T deserialize(String[] data);
    
    public abstract void update(T entity);
}
