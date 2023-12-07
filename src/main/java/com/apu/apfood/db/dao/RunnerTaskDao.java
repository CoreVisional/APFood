package com.apu.apfood.db.dao;

import com.apu.apfood.db.enums.TaskStatus;
import com.apu.apfood.db.models.OrderDetails;
import com.apu.apfood.db.models.User;
import com.apu.apfood.helpers.FileHelper;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Bryan
 */
public class RunnerTaskDao extends APFoodDao<User> {

    private static final String RUNNER_FEEDBACK_FILEPATH = "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\RunnerFeedback.txt";
    private static final String FEEDBACK_HEADERS = "id| deliveryRunnerId| orderId| vendor| feedback\n";
    
    private static final String USER_FILEPATH = "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\RunnerTaskAssignment.txt";
    private static final String HEADERS = "id| orderId| deliverRunnerId| status| vendor| location\n";

    private UserDao userDao = new UserDao();
    private NotificationDao notificationDao = new NotificationDao();

    public RunnerTaskDao() {
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

    public Object[][] getDeliveryHistory(User user) {

        // Declare 2D array
        Object[][] deliveryHistory;

        // Create an empty list to store matching rows
        List<String[]> rows = new ArrayList<>();

        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\\| ");
                if (values[2].equals(String.valueOf(user.getId())) && values[3].equals("Accepted")) {

                    // Retrieve orderid, vendor name, delivery location
                    String orderId = values[1];
                    String vendorName = values[4];
                    String location = values[5];
                    String feedback = getRunnerFeedback(orderId, vendorName);

                    // Retrive customer name, date and time
                    FileReader fr2 = new FileReader(BASE_PATH + "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\vendors\\" + vendorName + "\\Orders.txt");
                    BufferedReader br2 = new BufferedReader(fr2);
                    String row2;

                    br2.readLine();
                    while ((row2 = br2.readLine()) != null) {
                        String[] rowArray2 = row2.split("\\| ");
                        if (rowArray2[1].equals(orderId)) {
                            // Retrieve account Id, date, time and customer name
                            String accountId = rowArray2[2];
                            String date = rowArray2[5];
                            String time = rowArray2[6];

                            LocalTime parsedTime = LocalTime.parse(time);
                            int hours = parsedTime.getHour();
                            int minutes = parsedTime.getMinute();
                            // Formatting minutes to have leading zeros if necessary
                            String formattedMinutes = String.format("%02d", minutes);
                            String formattedTime = hours + ":" + formattedMinutes; // Concatenating hours and formatted minutes

                            String customerName = userDao.getUserName(accountId);

                            FileReader fr3 = new FileReader(BASE_PATH + "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\RunnerDelivery.txt");
                            BufferedReader br3 = new BufferedReader(fr3);
                            String row3;

                            while ((row3 = br3.readLine()) != null) {
                                String[] rowArray3 = row3.split("\\| ");

                                if (rowArray3[1].equals(orderId) && rowArray3[3].equals(vendorName)) {
                                    String deliveryId = rowArray3[0];
                                    String deliveryStatus = rowArray3[2];

                                    // Populate table row
                                    String[] row = {deliveryId, orderId, customerName, vendorName, location, date, formattedTime, deliveryStatus, feedback};
                                    rows.add(row);
                                    break;
                                }
                            }
                            br3.close();
                            break;
                        }
                    }
                    br2.close();
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert the list of matching rows to a 2D array
        deliveryHistory = new Object[rows.size()][8];
        for (int i = 0; i < rows.size(); i++) {
            deliveryHistory[i] = rows.get(i);
        }

        return deliveryHistory;
    }

    public void addRunnerFeedback(int deliveryRunnerId, int orderId, String vendor, String feedback) {
        String feedbackEntry = deliveryRunnerId + "| " + orderId + "| " + vendor + "| " + feedback; // Add newline here
        File feedbackFile = new File(BASE_PATH + RUNNER_FEEDBACK_FILEPATH);
        fileHelper.writeFile(RUNNER_FEEDBACK_FILEPATH, feedbackFile, FEEDBACK_HEADERS, true, feedbackEntry);
    }
    
    public String getRunnerFeedback(String orderId, String vendorName) {
        String feedback = "";
        try {
            FileReader fr = new FileReader(BASE_PATH + "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\RunnerFeedback.txt");
            BufferedReader br = new BufferedReader(fr);
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\\| ");
                if (values[2].equals(orderId) && values[3].equals(vendorName)) {
                    feedback = values[4];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return feedback;
    }

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
                    FileReader fr2 = new FileReader(BASE_PATH + "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\vendors\\" + vendorName + "\\Orders.txt");
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

                            // Read vendor's menu
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

                                    // Check if key exists in hashmap
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
                    System.out.println(key);
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

    public void changeTaskAssignmentStatus(User user, String state, String inputOrderId, String vendorName) {
        List<String> updatedLines = new ArrayList<>();

        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);

            updatedLines.add(br.readLine()); // Add first row
            String line;

            if (state.equals("Declined")) {
                while ((line = br.readLine()) != null) {
                    String[] rowArray = line.split("\\| ");
                    String id = rowArray[0];
                    String orderId = rowArray[1];
                    String deliveryRunnerId = rowArray[2];
                    String status = rowArray[3];
                    String vendor = rowArray[4];
                    String location = rowArray[5];
                    if (rowArray[1].equals(inputOrderId) && rowArray[2].equals(String.valueOf(user.getId())) && rowArray[4].equals(vendorName)) {
                        status = state;
                    }
                    updatedLines.add(id + "| " + orderId + "| " + deliveryRunnerId + "| " + status + "| " + vendor + "| " + location);
                }
                br.close();

                FileWriter fw = new FileWriter(filePath, false);
                BufferedWriter bw = new BufferedWriter(fw);

                // Write the updated lines to the file
                for (String updatedLine : updatedLines) {
                    bw.write(updatedLine);
                    bw.newLine(); // Add a newline character to separate lines
                }

                // Close the BufferedWriter to save the changes
                bw.close();
            } else if (state.equals("Accepted")) {
                boolean stateUpdated = false;
                while ((line = br.readLine()) != null) {
                    String[] rowArray = line.split("\\| ");
                    String id = rowArray[0];
                    String orderId = rowArray[1];
                    String deliveryRunnerId = rowArray[2];
                    String status = rowArray[3];
                    String vendor = rowArray[4];
                    String location = rowArray[5];
                    if (!stateUpdated && rowArray[1].equals(inputOrderId) && rowArray[2].equals(String.valueOf(user.getId())) && rowArray[3].equals("Pending")) {
                        // Current runner's status for this task set to "Accepted"
                        status = state;
                        stateUpdated = true;
                    } else if (rowArray[1].equals(inputOrderId) && !rowArray[2].equals(String.valueOf(user.getId()))) {
                        // All other runners status for this task are set to "Declined".
                        status = "Declined";
                    }
                    updatedLines.add(id + "| " + orderId + "| " + deliveryRunnerId + "| " + status + "| " + vendor + "| " + location);
                }

                // Close the BufferedWriter to save the changes
                br.close();

                FileWriter fw = new FileWriter(filePath, false);
                BufferedWriter bw = new BufferedWriter(fw);

                // Write the updated lines to the file
                for (String updatedLine : updatedLines) {
                    bw.write(updatedLine);
                    bw.newLine(); // Add a newline character to separate lines
                }

                // Close the BufferedWriter to save the changes
                bw.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notifyNoRunner(String inputOrderId, String vendorName, String userId) {
        boolean isNoRunnerLeft = true;

        // Check if there are any other runners still Pending
        try (FileReader fr = new FileReader(filePath); BufferedReader br = new BufferedReader(fr)) {
            String row;
            while ((row = br.readLine()) != null) {
                String[] rowArray = row.split("\\| ");
                if (rowArray[1].equals(inputOrderId) && rowArray[3].equals("Pending")) {
                    isNoRunnerLeft = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isNoRunnerLeft) {

            String content = "Delivery runner not found [order id: " + inputOrderId + ", vendor name: " + vendorName + "]";
            String status = "Unnotified";
            String type = "Push";
            // If no runner left send notification to customer
            notificationDao.writeNotification(userId, content, status, type);
        }
    }

    public void addRunnerDeliveryRecord(String orderId, String vendorName, String userId) {
        String runnerDeliveryFilePath = BASE_PATH + "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\RunnerDelivery.txt";
        FileHelper fileHelper = new FileHelper();
        fileHelper.writeFile(runnerDeliveryFilePath, new File(runnerDeliveryFilePath), "id| orderId| status| vendor| deliveryRunnerId", true, orderId + "| Ongoing| " + vendorName + "| " + userId);
    }

    public void updateDeliveryStatus(String inputOrderId, String inputVendorName) {
        String runnerDeliveryFilePath = BASE_PATH + "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\RunnerDelivery.txt";

        List<String> updatedLines = new ArrayList<>();

        try {
            FileReader fr = new FileReader(runnerDeliveryFilePath);
            BufferedReader br = new BufferedReader(fr);

            // Write header row into file and skip it.
            updatedLines.add(br.readLine());

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\\| ");

                // Assign existing values into variables to write back into file
                String id = values[0];
                String orderId = values[1];
                String deliveryStatus = values[2];
                String vendorName = values[3];
                String deliveryRunnerID = values[4];

                // Modifications
                if (values[1].equals(inputOrderId) && values[3].equals(inputVendorName)) {
                    deliveryStatus = "Completed";
                }

                // Add to list
                updatedLines.add(id + "| " + orderId + "| " + deliveryStatus + "| " + vendorName + "| " + deliveryRunnerID);
            }
            br.close();

            FileWriter fw = new FileWriter(runnerDeliveryFilePath, false);
            BufferedWriter bw = new BufferedWriter(fw);

            // Write the updated lines to the file
            for (String updatedLine : updatedLines) {
                bw.write(updatedLine);
                bw.newLine();
            }

            // Close the BufferedWriter to save the changes
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkRunnerHandlingTask(String userId) {
        try {
            FileReader fr = new FileReader(BASE_PATH + "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\RunnerDelivery.txt");
            BufferedReader br = new BufferedReader(fr);
            String row;

            while ((row = br.readLine()) != null) {
                String[] rowArray = row.split("\\| ");

                if (rowArray[2].equals("Ongoing") && rowArray[4].equals(userId)) {
                    // If runner has ongoing task, return true
                    return true;
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // If runner no ongoing task, return false
        return false;
    }

    public List<String> getTaskDetails(String userId) {
        List<String> taskDetails = new ArrayList<>();

        try {
            FileReader fr = new FileReader(BASE_PATH + "\\src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\RunnerDelivery.txt");
            BufferedReader br = new BufferedReader(fr);
            br.readLine();
            String row;

            while ((row = br.readLine()) != null) {
                String[] rowArray = row.split("\\| ");

                if (rowArray[2].equals("Ongoing") && rowArray[4].equals(userId)) {
                    String orderId = rowArray[1];
                    String vendorName = rowArray[3];

                    //Read vendor's order history
                    FileReader fr2 = new FileReader(filePath);
                    BufferedReader br2 = new BufferedReader(fr2);
                    String row2;

                    br2.readLine();
                    while ((row2 = br2.readLine()) != null) {
                        String[] rowArray2 = row2.split("\\| ");
                        if (rowArray2[1].equals(orderId) && rowArray2[4].equals(vendorName)) {
                            String location = rowArray2[5];
                            String customerId = userDao.getUserId(orderId, vendorName);
                            String customerName = userDao.getUserName(customerId);

                            taskDetails.add(location);
                            taskDetails.add(orderId);
                            taskDetails.add(vendorName);
                            taskDetails.add(customerName);
                        }
                    }
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return taskDetails;
    }
    
    public int getRunnerIdByOrderId(String orderId) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\\| ");
                if (values[1].equals(orderId)) {
                    return Integer.parseInt(values[2].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        return -1;
    }

    public void writeVendorTaskAssignment(int orderId, int runnerId, String vendorName, String deliveryLocation)
    {
        String task  = orderId + "| " + runnerId + "| " + TaskStatus.PENDING.toString() + "| " + vendorName + "| " + deliveryLocation;
        this.fileHelper.writeFile(filePath, new File(filePath), HEADERS, true, task);
    }
    
    
    public static void main(String[] args) {
        RunnerTaskDao runnerTaskDao = new RunnerTaskDao();
        runnerTaskDao.getOrderList(new User(5, "Alice Johnson", "123@123.com", "qweqweqwe".toCharArray(), "Runner"));
        runnerTaskDao.getOrderKeys(new User(5, "Alice Johnson", "123@123.com", "qweqweqwe".toCharArray(), "Runner"));
    }
}
