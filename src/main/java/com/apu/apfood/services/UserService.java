package com.apu.apfood.services;

import com.apu.apfood.db.dao.NotificationDao;
import com.apu.apfood.db.dao.RunnerAvailabilityDao;
import com.apu.apfood.db.dao.TransactionDao;
import com.apu.apfood.db.dao.UserDao;
import com.apu.apfood.db.enums.NotificationStatus;
import com.apu.apfood.db.models.Notification;
import com.apu.apfood.db.models.Transaction;
import com.apu.apfood.db.models.User;
import com.apu.apfood.exceptions.CustomValidationException;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Bryan
 */
public class UserService {

    private UserDao userDao = new UserDao();
    private final RunnerAvailabilityDao runnerAvailabilityDao = new RunnerAvailabilityDao();
    private final TransactionDao transactionDao = new TransactionDao();
    private final NotificationDao notificationDao = new NotificationDao();
    private final NotificationService notificationService = new NotificationService(notificationDao);

    public UserService() {
    }

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void addUser(User user) {
        userDao.add(user);
    }

    public Object[][] getAllRegisteredUsers(boolean manageUsers) {
        if (manageUsers) {
            return userDao.getUsers(true);
        }
        return userDao.getUsers();
    }

    public Object[][] getAllRegisteredUsers() {
        return getAllRegisteredUsers(false);
    }

    public Object[][] getAllRunnerAvailability() {
        return runnerAvailabilityDao.getAllRunnerAvailability();
    }

    public Object[][] getAllVendorNames() {
        return userDao.getAllVendor();
    }

    public Object[][] getAllRequestTopUpNotifications() {
        List<Notification> allNotifications = notificationDao.getAllNotifications();
        List<Object[]> creditTopUpNotifications = allNotifications.stream()
                .filter(notification -> notification.getContent().contains("requests for a credit top-up"))
                .filter(notification -> notification.getNotificationStatus().equals(NotificationStatus.UNNOTIFIED))
                .map(notification -> {
                    String content = notification.getContent();
                    String formattedContent = content.substring(0, content.indexOf("[")).trim();
                    formattedContent = formattedContent.substring(0, formattedContent.lastIndexOf(" "));
                    String amount = content.replaceAll(".*amount: (RM [\\d.]+).*", "$1"); // Extracts the amount part

                    return new Object[]{
                        String.valueOf(notification.getId()),
                        formattedContent + " of " + amount,};
                })
                .collect(Collectors.toList());

        // Convert to Object[][]
        Object[][] creditTopUpNotificationsArray = new Object[creditTopUpNotifications.size()][];
        for (int i = 0; i < creditTopUpNotifications.size(); i++) {
            creditTopUpNotificationsArray[i] = creditTopUpNotifications.get(i);
        }

        return creditTopUpNotificationsArray;

    }

    public String[] retrieveNotificationDetails(String notificationId) {
        List<Notification> allNotifications = notificationDao.getAllNotifications();
        Notification creditTopUpNotifications = allNotifications.stream()
                .filter(notification -> notification.getId() == Integer.parseInt(notificationId))
                .findFirst()
                .orElse(null); // Returns null if no matching notification is found
        String notificationContent = creditTopUpNotifications.getContent();
        String userId = notificationService.extractUserId(notificationContent);
        String amount = notificationService.extractAmount(notificationContent);
        String userName = userDao.getUserName(userId);
        
        
        return new String[]{userId, userName, amount};
    }

    public void removeUser(String id) {
        userDao.removeUserById(id);
    }

    public void updateUser(String inputUserId, String inputUserName, String inputUserEmail, String userInputPassword) {
        userDao.modifyUser(inputUserId, inputUserName, inputUserEmail, userInputPassword);
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

    public void addTopUpTransaction(String customerId, String adminId, String amount, String remark) {
        transactionDao.writeTransaction(customerId, amount, remark);
        List<Transaction> transactions = transactionDao.getAllTransactions();
        Transaction lastTransaction = transactions.get(transactions.size() - 1);
        String transactionId = String.valueOf(lastTransaction.getId());
        System.out.println(transactionId);
        notificationDao.writeNotification(customerId, "Credit top up [transaction id: " + transactionId + "]", "Unnotified", "Transactional");
    }

    public static String sanitizeEmail(String email) {
        // Remove leading and trailing white spaces
        return email.trim().toLowerCase();
    }

    public static void main(String[] args) {
        UserService us = new UserService();
        us.addUser(new User(5, "Jane Doe", "asd@asd.com", "qweqweqwe".toCharArray(), "admin"));
        for (Object[] value : us.getAllVendorNames()) {
            for (Object row : value) {
                System.out.println(row);
            }
        }
    }

    public boolean isCustomer(int userId) {
        User user = userDao.getUserById(userId);
        return user != null && "customer".equalsIgnoreCase(user.getRole());
    }
}
