package com.apu.apfood.services;

import com.apu.apfood.db.dao.UserDao;
import com.apu.apfood.db.models.User;

/**
 *
 * @author Alex
 */
public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void addUser(User user) {
        userDao.add(user);
    }

    public static String sanitizeEmail(String email) {
        // Remove leading and trailing white spaces
        return email.trim().toLowerCase();
    }
    
    public boolean isCustomer(int userId) {
        User user = userDao.getUserById(userId);
        return user != null && "customer".equalsIgnoreCase(user.getRole());
    }
}
