package com.apu.apfood.services;

import com.apu.apfood.db.dao.NotificationDao;
import com.apu.apfood.db.dao.RunnerAvailabilityDao;
import com.apu.apfood.db.dao.TransactionDao;
import com.apu.apfood.db.dao.UserDao;
import com.apu.apfood.db.models.User;
import com.apu.apfood.exceptions.CustomValidationException;

/**
 *
 * @author Alex
 */
public class UserService {

    private final UserDao userDao = new UserDao();
    private final RunnerAvailabilityDao runnerAvailabilityDao = new RunnerAvailabilityDao();
    private final TransactionDao transactionDao = new TransactionDao();
    private final NotificationDao notificationDao = new NotificationDao();

    public UserService() {
    }

    public void addUser(User user) {
        userDao.add(user);
    }

    public Object[][] getAllRegisteredUsers() {
        return userDao.getAllUsers();
    }

    public Object[][] getAllRunnerAvailability() {
        return runnerAvailabilityDao.getAllRunnerAvailability();
    }
    
    public Object[][] getAllVendorNames() {
        return userDao.getAllVendor();
    }

    public void checkCredentials(String name, String password, String email, String role) throws CustomValidationException {
        try {
            if (role.equals("No role selected")) {
                throw new CustomValidationException("Select a user role");
            }
            userDao.validateCredentials(name, password, email, role);
            // Other operations after successful validation
        } catch (CustomValidationException e) {
            String errorMessage = "Validation failed: " + e.getMessage();
            throw new CustomValidationException(errorMessage, e);
        }
    }

    // For vendor
    public boolean checkCredentials(String name, String password, String email, String role, String vendorName) throws CustomValidationException {
        try {
            if (role.equals("No role selected")) {
                throw new CustomValidationException("Select a user role");
            }
            userDao.validateCredentials(name, password, email, role);

            // Check if the vendor name is a duplicate or not
            if (userDao.checkVendorExists(vendorName)) {
                return true;
            }

        } catch (CustomValidationException e) {
            String errorMessage = "Validation failed: " + e.getMessage();
            throw new CustomValidationException(errorMessage, e);
        }
        return false;
    }

    public void registerUserToVendor(String customerName, String inputVendorName) {
        userDao.addVendorUser(customerName, inputVendorName);
    }

    public void createVendorFolder(String vendorName) {
        userDao.createNewVendorFolder(vendorName);
    }
    
    public void addNewRunnerAvailability(String name) {
        String userId = userDao.getUserId(name);
        this.runnerAvailabilityDao.addNewRunnerAvailability(userId);
    }

    public Object[][] getCustomerBalance() {
        return userDao.getCustomerCreditDetails();
    }

    public void addTopUpTransaction(String userId, String amount, String remark) {
        transactionDao.writeTransaction(userId, amount, remark);
        notificationDao.writeNotification(userId, "Credit top up [user id: " + userId + "]", "Unnotified", "Transactional");
    }

    public static String sanitizeEmail(String email) {
        // Remove leading and trailing white spaces
        return email.trim().toLowerCase();
    }

    public static void main(String[] args) {
        UserService us = new UserService();
        us.addUser(new User(5, "Jane Doe", "asd@asd.com", "qweqweqwe".toCharArray(), "admin"));
        for (Object[] value: us.getAllVendorNames()) {
            for(Object row: value) {
                System.out.println(row);
            }
        }
    }
}
