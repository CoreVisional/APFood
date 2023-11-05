/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apu.apfood.services;

import com.apu.apfood.db.dao.RunnerDao;
import com.apu.apfood.db.dao.RunnerAvailabilityDao;
import com.apu.apfood.db.dao.RunnerRevenueDao;
import com.apu.apfood.db.dao.RunnerTaskDao;
import com.apu.apfood.db.dao.UserDao;
import com.apu.apfood.db.models.User;
import java.util.Map;
import javax.swing.JRadioButton;

/**
 *
 * @author Bryan
 */
public class RunnerService {

    private User runner;
    private RunnerAvailabilityDao runnerAvailabilityDao = new RunnerAvailabilityDao();
    private RunnerRevenueDao runnerRevenueDao = new RunnerRevenueDao();
    private RunnerTaskDao runnerTaskDao = new RunnerTaskDao();

    public RunnerService(User runner) {
        this.runner = runner;
    }

    public Object[][] getDeliveryHistory() {
        RunnerDao rd = new RunnerDao();
        return rd.getDeliveryHistory(this.runner);
    }

    public void setAvailabilityRadioButton(User user, JRadioButton availableBtn, JRadioButton unavailableBtn) {
        String status = runnerAvailabilityDao.getAvailability(user);
        if (status == null) {
            runnerAvailabilityDao.addNewRunnerAvailability(user);
            unavailableBtn.setSelected(true);
        } else if (status.equals("unavailable")) {
            unavailableBtn.setSelected(true);
        } else {
            availableBtn.setSelected(true);
        }
    }

    public void modifyAvailability(String status) {
        String availability;
        if (status.equals("Yes")) {
            availability = "available";
        } else {
            availability = "unavailable";
        }
        runnerAvailabilityDao.updateAvailability(runner, availability);
    }

    public String getTotalRevenue(User user) {
        return runnerRevenueDao.checkTotalRevenue(user);
    }

    public String getMonthsRevenue(User user, int months) {
        return runnerRevenueDao.checkPastMonthRevenue(user, months);
    }

    public String getDailyRevenue(User user) {
        return runnerRevenueDao.checkDailyRevenue(user);
    }

    public Map<String, RunnerTaskDao.OrderDetails> getDeliveryTask(User user) {
        Map<String, RunnerTaskDao.OrderDetails> orderMap = runnerTaskDao.getOrderList(user);
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

    public void displayTask(String[] orderKeys, int orderListPanelIndex, Map<String, RunnerTaskDao.OrderDetails> deliveryTasks, javax.swing.JLabel taskCustomerNameJLabel, javax.swing.JLabel taskVendorNameJLabel, javax.swing.JLabel taskOrderIdJLabel, javax.swing.JTextArea taskOrderListJTextArea) {
        UserDao ud = new UserDao();
        String chosenKey = orderKeys[orderListPanelIndex];
        RunnerTaskDao.OrderDetails orderDetails = deliveryTasks.get(chosenKey);

        String customerName = ud.getCustomerName(orderDetails.getAccountId());
        taskCustomerNameJLabel.setText(customerName);
        taskVendorNameJLabel.setText(orderDetails.getVendorName());
        taskOrderIdJLabel.setText("Order ID: #" + orderDetails.getOrderId());

        StringBuilder itemsStringBuilder = new StringBuilder();

        for (RunnerTaskDao.FoodDetails foodDetails : orderDetails.getFoodDetailsList()) {
            String item = "- " + foodDetails.getFoodName() + " x" + foodDetails.getQuantity() + "\n";
            itemsStringBuilder.append(item);
        }
        String allItems = itemsStringBuilder.toString();
        taskOrderListJTextArea.setText(allItems);
    }

    public String[] getOrderKeys(User user) {
        return runnerTaskDao.getOrderKeys(user);
    }

}
