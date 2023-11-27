package com.apu.apfood.db.dao;

import com.apu.apfood.db.enums.OrderStatus;
import com.apu.apfood.db.models.Order;
import com.apu.apfood.db.models.OrderDetails;
import com.apu.apfood.helpers.FileHelper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Alex
 */
public class OrderDao extends APFoodDao<Order> {
    
    private static final String ORDER_FILEPATH = "/src/main/java/com/apu/apfood/db/datafiles/vendors/";   
    private static final String HEADERS = "id| orderId| userId| menuId| quantity| orderDate| orderTime| remarks| mode| orderStatus| hasDiscount| deliveryLocation\n";
    private FileHelper fileHelper = new FileHelper();

    public OrderDao() {
        super(ORDER_FILEPATH, HEADERS);
    }
    
    public OrderDao(String vendorName) {
        super(ORDER_FILEPATH, HEADERS);
    }
    
    public void updateFilePath(String vendorName) {
        this.filePath = getFullPath(ORDER_FILEPATH + vendorName + "/Orders.txt");
    }

    public void addOrders(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            return;
        }

        // Generate unique orderId once for the entire batch
        int uniqueOrderId = generateUniqueOrderId();

        for (Order order : orders) {
            order.setOrderId(uniqueOrderId); // Set the shared unique orderId for each order
            super.add(order); // Call the base class add method for each order
        }
    }
    
    @Override
    protected String serialize(Order order) {
        return order.getOrderId() + "| " + 
               order.getUserId() + "| " + 
               order.getMenuId() + "| " + 
               order.getQuantity() + "| " + 
               order.getOrderDate() + "| " + 
               order.getOrderTime() + "| " + 
               order.getRemarks() + "| " + 
               order.getMode() + "| " + 
               order.getOrderStatus() + "| " +
               order.isDiscountAvailable() + "| " +
               order.getDeliveryLocation() + "\n";
    }
    
    @Override
    protected Order deserialize(String[] data) {
        int orderId = Integer.parseInt(data[1].trim());
        int userId = Integer.parseInt(data[2].trim());
        int menuId = Integer.parseInt(data[3].trim());
        int quantity = Integer.parseInt(data[4].trim());
        LocalDate orderDate = LocalDate.parse(data[5].trim());
        LocalTime orderTime = LocalTime.parse(data[6].trim());
        String remarks = data[7].trim();
        String mode = data[8].trim();
        OrderStatus orderStatus = OrderStatus.valueOf(data[9].trim().toUpperCase());
        boolean hasDiscount = Boolean.parseBoolean(data[10]);
        String deliveryLocation = data[11].trim();

        return new Order(orderId, userId, menuId, quantity, orderDate, orderTime, remarks, mode, orderStatus, hasDiscount, deliveryLocation);
    }
    
    @Override
    public void update(Order order) {
        
    }
    
    private int generateUniqueOrderId() {
        int maxOrderId = 0;
        File parentDir = new File(filePath).getParentFile().getParentFile();

        File[] vendorDirs = parentDir.listFiles();
        
        if (vendorDirs == null) return 1; // If no directories are found, start with 1

        for (File vendorDir : vendorDirs) {
            if (!vendorDir.isDirectory()) continue;

            File orderFile = new File(vendorDir, "OrdersTest.txt");
            if (!orderFile.exists()) continue;

            try {
                List<String[]> orders = fileHelper.readFile(orderFile.getPath());
                
                for (String[] order : orders) {
                    if (order.length > 1) {
                        try {
                            int orderId = Integer.parseInt(order[1].trim());
                            maxOrderId = Math.max(maxOrderId, orderId);
                        } catch (NumberFormatException e) {
                            System.err.println(e.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        return maxOrderId + 1;
    }
    
    public List<Order> getOrderListfromVendor (String vendorName)
    {
        this.filePath = getFullPath(ORDER_FILEPATH + vendorName + "/Orders.txt");
        List<String[]> rawData = super.getAll();
        List<Order> orders = rawData.stream()
                                  .map(this::deserialize)
                                  .collect(Collectors.toList());

        return orders;
        
    }
    
    public boolean updateOrderStatus(int orderId, OrderStatus orderStatus, String vendorName)
    {
        boolean success = false;
        this.filePath = getFullPath(ORDER_FILEPATH + vendorName + "/Orders.txt");
        List<String[]> rawData = super.getAll();
        List<Order> orders = rawData.stream()
                                  .map(this::deserialize)
                                  .collect(Collectors.toList());
        try {
            // Open the file with WRITE mode, which truncates the file to size 0
            Files.newBufferedWriter(Path.of(filePath), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        String[] lines = new String[orders.size()];
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            if(order.getOrderId() == orderId)
            {
                order.setOrderStatus(orderStatus);
                success = true;
            }
            String serializedData = serialize(order);
            fileHelper.writeFile(filePath, new File(filePath),HEADERS, serializedData);
        }
        return success;
    }
    
}
