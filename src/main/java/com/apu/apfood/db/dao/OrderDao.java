package com.apu.apfood.db.dao;

import com.apu.apfood.db.models.Order;
import java.io.File;
import java.util.List;

/**
 *
 * @author Alex
 */
public class OrderDao extends APFoodDao<Order> {
    
    private static final String ORDER_FILEPATH = "/src/main/java/com/apu/apfood/db/datafiles/vendors/";   
    private static final String HEADERS = "id| orderId| userId| menuId| quantity| orderDate| orderTime| remarks| mode| orderStatus| hasDiscount| deliveryLocation\n";
    
    public OrderDao() {
        
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
        return null;
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
}
