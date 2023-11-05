/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apu.apfood.db.dao;

import com.apu.apfood.db.models.User;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Bryan
 */
public class RunnerTaskDao extends APFoodDao<User> {

    private static final String BASE_PATH = System.getProperty("user.dir");
    private static final String USER_FILEPATH = "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\RunnerTaskAssignment.txt";
    private static final String HEADERS = "id| orderId| deliveryRunnerId| status| vendorName\n";

    public RunnerTaskDao() {
        super(USER_FILEPATH, HEADERS);
    }

    @Override
    protected String serialize(User user) {
        return user.getName() + "| " + user.getEmail() + "| " + new String(user.getPassword()) + user.getRole() + "\n";
    }

    @Override
    public void update(User user) {

    }

//Check for orderId
//Go to vendor's menu.txt
//Check for Food Id
//Retrieve food name. Put food id, food name, quantity into array.
//Next iteration of orderHistory, until no more of that order id.
//Use array, display as list of ordered items.
//Read user.txt
//Check for accountId.
//get full name.
    public Map<String, OrderDetails> getOrderList(User user) {
        Map<String, OrderDetails> orderMap = new HashMap<>();
        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            String row;

            br.readLine(); // Skip header row
            while ((row = br.readLine()) != null) {
                String[] rowArray = row.split("\\| ");

                if (rowArray[2].equals(String.valueOf(user.getId())) && rowArray[3].equals("Pending")) {
                    //Retrieve orderId and vendor name
                    String orderId = rowArray[1];
                    String vendorName = rowArray[4];

                    //Read vendor's order history
                    FileReader fr2 = new FileReader(BASE_PATH + "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\vendors\\" + vendorName + "\\OrderHistory.txt");
                    BufferedReader br2 = new BufferedReader(fr2);
                    String row2;

                    br2.readLine();
                    while ((row2 = br2.readLine()) != null) {
                        String[] rowArray2 = row2.split("\\| ");
                        if (rowArray2[1].equals(orderId)) {
                            // Retrieve food id and quantity, and account Id.
                            String accountId = rowArray2[2];
                            String foodId = rowArray2[3];
                            String quantity = rowArray2[4];

                            FileReader fr3 = new FileReader(BASE_PATH + "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\vendors\\" + vendorName + "\\Menu.txt");
                            BufferedReader br3 = new BufferedReader(fr3);
                            String row3;

                            br3.readLine();
                            while ((row3 = br3.readLine()) != null) {

                                String[] rowArray3 = row3.split("\\| ");
                                if (rowArray3[0].equals(foodId)) {
                                    // Retrieve food name
                                    String foodName = rowArray3[1];

                                    // Create key
                                    String key = vendorName + "-" + orderId;

                                    // Check if key exists already
                                    if (!orderMap.containsKey(key)) {
                                        OrderDetails orderDetails = new OrderDetails();
                                        orderDetails.setAccountId(accountId);
                                        orderDetails.setOrderId(orderId);
                                        orderDetails.setVendorName(vendorName);

                                        orderMap.put(key, orderDetails);
                                    }

                                    OrderDetails orderDetails = orderMap.get(key);

                                    // Add food list to the OrderDetails object
                                    orderDetails.addFoodDetails(foodName, foodId, quantity);
                                }
                            }
                        }
                    }
                }

            }
            /*            
            {
                key: Modesto-1
                Orderdetails : {
                    AccountID: 1
                    OrderID: 1
                    VendorName: Modesto
                    FoodDetails: [
                        {
                            FoodName: Nasi
                            FoodID: 1
                            Quantity: 1
                        },
                        {
                            FoodName: Orange Chicken
                            FoodID: 2
                            Quantity: 3
                        }
                    ]
                },
                key: Modesto-2
                Orderdetails : {
                    AccountID: 1
                    OrderID: 1
                    VendorName: Modesto
                    FoodDetails: [
                        {
                            FoodName: Nasi
                            FoodID: 1
                            Quantity: 1
                        }
                    ]
                },
            }
             */

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return orderMap;
    }
    
    public void getCustomerName(String accountId) {
        
    }

    public String[] getOrderKeys(User user) {
        List<String> orderKeys = new ArrayList<>();
        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);

            String row;
            br.readLine(); // Skip header row

            while ((row = br.readLine()) != null) {
                String[] rowArray = row.split("\\| ");
                String vendorName = rowArray[4];
                String orderId = rowArray[1];

                if (rowArray[2].equals(String.valueOf(user.getId())) && rowArray[3].equals("Pending")) {
                    String key = vendorName + "-" + orderId;
                    orderKeys.add(key);
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Convert the ArrayList to a String array
        return orderKeys.toArray(new String[0]);
    }

    public class OrderDetails {

        private String accountId;
        private String orderId;
        private String vendorName;

        private List<FoodDetails> foodDetailsList = new ArrayList<>();

        public void addFoodDetails(String foodName, String foodId, String quantity) {
            FoodDetails foodDetails = new FoodDetails(foodName, foodId, quantity);
            foodDetailsList.add(foodDetails);
        }

        public String getAccountId() {
            return accountId;
        }

        public String getOrderId() {
            return orderId;
        }

        public String getVendorName() {
            return vendorName;
        }

        public List<FoodDetails> getFoodDetailsList() {
            return foodDetailsList;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public void setVendorName(String vendorName) {
            this.vendorName = vendorName;
        }

    }

    public class FoodDetails {

        private String foodName;
        private String foodId;
        private String quantity;

        public FoodDetails(String foodName, String foodId, String quantity) {
            this.foodName = foodName;
            this.foodId = foodId;
            this.quantity = quantity;
        }

        public String getFoodName() {
            return foodName;
        }

        public String getFoodId() {
            return foodId;
        }

        public String getQuantity() {
            return quantity;
        }
    }

    public static void main(String[] args) {
        RunnerTaskDao runnerTaskDao = new RunnerTaskDao();
        runnerTaskDao.getOrderList(new User(5, "Alice Johnson", "123@123.com", "qweqweqwe".toCharArray(), "Runner"));
        runnerTaskDao.getOrderKeys(new User(5, "Alice Johnson", "123@123.com", "qweqweqwe".toCharArray(), "Runner"));
    }

}
