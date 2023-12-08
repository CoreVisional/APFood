package com.apu.apfood.services;

import com.apu.apfood.db.dao.UserDao;
import com.apu.apfood.db.models.User;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Alex
 */
public class UserService {

    private final UserDao userDao;
    private static int lastAssignedAdminId = 0;

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
    
    public List<User> getAllAdmins() {
        return userDao.getAllUsers().stream()
                      .filter(user -> "admin".equalsIgnoreCase(user.getRole()))
                      .collect(Collectors.toList());
    }
    
    public User getNextAdmin() {
        List<User> admins = getAllAdmins();
        if (admins.isEmpty()) {
            return null;
        }

        // Round-robin assignment
        int nextAdminIndex = 0;
        if (lastAssignedAdminId != 0) {
            for (int i = 0; i < admins.size(); i++) {
                if (admins.get(i).getId() == lastAssignedAdminId) {
                    nextAdminIndex = (i + 1) % admins.size();
                    break;
                }
            }
        }

        lastAssignedAdminId = admins.get(nextAdminIndex).getId();
        return admins.get(nextAdminIndex);
    }
}
