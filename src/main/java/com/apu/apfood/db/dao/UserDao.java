package com.apu.apfood.db.dao;

import com.apu.apfood.db.models.User;

/**
 *
 * @author Alex
 */
public class UserDao extends APFoodDao<User> {

    private static final String USER_FILEPATH = "/src/main/java/com/apu/apfood/db/datafiles/user.txt";
    private static final String HEADERS = "id| name| email| password| role\n";
    
    public UserDao() {
        super(USER_FILEPATH, HEADERS);
    }
    
    @Override
    protected String serialize(User user) {
        return user.getName() + "| " + user.getEmail() + "| " + new String(user.getPassword()) + user.getRole() + "\n";
    }
    
    @Override
    protected User deserialize(String[] data) {
        return null;
    }
    
    @Override
    public void update(User user) {
        
    }
}
