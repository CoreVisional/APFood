/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apu.apfood.services;

import com.apu.apfood.db.dao.NotificationDao;
import com.apu.apfood.db.dao.RunnerAvailabilityDao;
import com.apu.apfood.db.dao.RunnerRevenueDao;
import com.apu.apfood.db.dao.RunnerTaskDao;
import com.apu.apfood.db.dao.UserDao;
import com.apu.apfood.db.models.FoodDetails;
import com.apu.apfood.db.models.OrderDetails;
import com.apu.apfood.db.models.User;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Bryan
 */
public class RunnerService {

    private User runner;
    private RunnerAvailabilityDao runnerAvailabilityDao = new RunnerAvailabilityDao();
    private RunnerRevenueDao runnerRevenueDao = new RunnerRevenueDao();
    private RunnerTaskDao runnerTaskDao = new RunnerTaskDao();
    private NotificationDao notificationDao = new NotificationDao();
    private UserDao userDao = new UserDao();

    public RunnerService(User runner) {
        this.runner = runner;
    }

    public Object[][] getDeliveryHistory() {
        return runnerTaskDao.getDeliveryHistory(this.runner);
    }

    public void setRevenueValues(User user, javax.swing.JLabel totalRevenueJLabel, javax.swing.JLabel monthlyRevenueJLabel, javax.swing.JLabel yearlyRevenueJLabel, javax.swing.JLabel todayRevenueJLabel) {
        totalRevenueJLabel.setText("RM " + runnerRevenueDao.checkTotalRevenue(user));
        monthlyRevenueJLabel.setText("RM " + runnerRevenueDao.checkPastMonthRevenue(user, 1));
        yearlyRevenueJLabel.setText("RM " + runnerRevenueDao.checkPastMonthRevenue(user, 12));
        todayRevenueJLabel.setText("RM " + runnerRevenueDao.checkDailyRevenue(user));
    }

    public Map<String, OrderDetails> getDeliveryTask(User user) {
        Map<String, OrderDetails> orderMap = runnerTaskDao.getOrderList(user);
        // Print the orderMap
//        for (Map.Entry<String, RunnerTaskDao.OrderDetails> entry : orderMap.entrySet()) {
//            String key = entry.getKey();
//            RunnerTaskDao.OrderDetails orderDetails = entry.getValue();
//
//            System.out.println("Key: " + key);
//            System.out.println("AccountID: " + orderDetails.getAccountId());
//            System.out.println("OrderID: " + orderDetails.getOrderId());
//            System.out.println("VendorName: " + orderDetails.getVendorName());
//
//            for (RunnerTaskDao.FoodDetails foodDetails : orderDetails.getFoodDetailsList()) {
//                System.out.println("FoodName: " + foodDetails.getFoodName());
//                System.out.println("FoodID: " + foodDetails.getFoodId());
//                System.out.println("Quantity: " + foodDetails.getQuantity());
//            }
//
//            System.out.println(); // Add a separator between entries
//        }
        return orderMap;
    }

    public void displayTask(String[] orderKeys, int orderListPanelIndex, Map<String, OrderDetails> deliveryTasks, javax.swing.JLabel taskCustomerNameJLabel, javax.swing.JLabel taskVendorNameJLabel, javax.swing.JLabel taskOrderIdJLabel, javax.swing.JTextArea taskOrderListJTextArea) {
        UserDao ud = new UserDao();

        // Get order details based on chosen key
        String chosenKey = orderKeys[orderListPanelIndex];
        OrderDetails orderDetails = deliveryTasks.get(chosenKey);
        String customerName = ud.getCustomerName(orderDetails.getAccountId());

        // Display information
        taskCustomerNameJLabel.setText(customerName);
        taskVendorNameJLabel.setText(orderDetails.getVendorName());
        taskOrderIdJLabel.setText("#" + orderDetails.getOrderId());

        StringBuilder foodItemsStringBuilder = new StringBuilder();

        for (FoodDetails foodDetails : orderDetails.getFoodDetailsList()) {
            String item = "- " + foodDetails.getFoodName() + " x" + foodDetails.getQuantity() + "\n";
            foodItemsStringBuilder.append(item);
        }
        String allFoodItems = foodItemsStringBuilder.toString();
        taskOrderListJTextArea.setText(allFoodItems);
    }

    public String[] getOrderKeys(User user) {
        return runnerTaskDao.getOrderKeys(user);
    }

    public void changeTaskAssignmentStatus(User user, String state, String inputOrderId, String vendorName) {
        // Remove # from order id
        String orderId = inputOrderId.replace("#", "");
        runnerTaskDao.changeTaskAssignmentStatus(user, state, orderId);

        if (state.equals("Accepted")) {
            // Set availability to unavailable
            runnerAvailabilityDao.updateAvailability(user, "Unavailable");
            // Add "Ongoing" delivery status RunnerDelivery.txt
            runnerTaskDao.addRunnerDeliveryRecord(orderId, vendorName, String.valueOf(user.getId()));
        }
    }

    public void notifyIfNoVendor(String inputOrderId, String vendorName) {
        // Remove # from order id
        String orderId = inputOrderId.replace("#", "");

        String userId = userDao.getCustomerId(orderId, vendorName);
        runnerTaskDao.notifyNoRunner(orderId, vendorName, userId);
    }

    public void notifyDeliveryOngoing(String inputOrderId, String vendorName) {
        // Remove # from order id
        String orderId = inputOrderId.replace("#", "");
        String userId = userDao.getCustomerId(orderId, vendorName);

        notificationDao.writeNotification(userId, "Delivery ongoing [order id: " + orderId + ", vendor name: " + vendorName + "]", "Unnotified", "Informational");
    }

    public boolean checkOngoingTask(User user) {
        return runnerTaskDao.checkRunnerHandlingTask(String.valueOf(user.getId()));
    }

    public void displayOngoingTaskDetails(User user, javax.swing.JLabel ongCustomerNameJLabel, javax.swing.JLabel ongLocationJLabel, javax.swing.JLabel ongOrderIdJLabel, javax.swing.JLabel ongVendorNameJLabel) {
        List<String> taskDetails = runnerTaskDao.getTaskDetails(String.valueOf(user.getId()));
        ongLocationJLabel.setText(taskDetails.get(0));
        ongOrderIdJLabel.setText("#" + taskDetails.get(1));
        ongVendorNameJLabel.setText(taskDetails.get(2));
        ongCustomerNameJLabel.setText(taskDetails.get(3));
    }

    public void finishTask(User user, String inputOrderId, String vendorName) {
        String orderId = inputOrderId.replace("#", "");
        String userId = userDao.getCustomerId(orderId, vendorName);

        runnerAvailabilityDao.updateAvailability(user, "Available");
        runnerTaskDao.updateDeliveryStatus(orderId, vendorName);
        notificationDao.writeNotification(userId, "Delivery completed [order id: " + orderId + ", vendor name: " + vendorName + "]", "Unnotified", "Informational");
    }

    public static void main(String[] args) {
        RunnerService rs = new RunnerService(new User(5, "Alice Johnson", "123@123.com", "qweqweqwe".toCharArray(), "Runner"));
    }
}
