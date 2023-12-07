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
import java.util.ArrayList;
import java.util.Arrays;
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

    public void addOrders(List<Order> orders, String vendorName) {
        int uniqueOrderId = generateUniqueOrderId(vendorName);

        for (Order order : orders) {
            order.setOrderId(uniqueOrderId);
            super.add(order);
        }
    }

    public List<Order> getUserOrders(int userId, List<OrderStatus> statuses) {
        File vendorsDir = new File(BASE_PATH + "/src/main/java/com/apu/apfood/db/datafiles/vendors/");
        return Arrays.stream(vendorsDir.listFiles())
                .filter(File::isDirectory)
                .flatMap(vendorFolder -> processVendorFolder(vendorFolder, userId, statuses).stream())
                .collect(Collectors.toList());
    }

    private List<Order> processVendorFolder(File vendorFolder, int userId, List<OrderStatus> statuses) {
        updateFilePath(vendorFolder.getName());
        return super.getAll().stream()
                .map(this::deserialize)
                .filter(order -> order != null && order.getUserId() == userId && statuses.contains(order.getOrderStatus()))
                .peek(order -> order.setVendorName(vendorFolder.getName()))
                .collect(Collectors.toList());
    }

    public void updateOrderMode(int orderId, String newMode, String vendorName) {
        // Update the file path to point to the specific vendor's Orders.txt
        updateFilePath(vendorName);

        List<Order> orders = getAllOrders();

        for (Order order : orders) {
            if (order.getOrderId() == orderId) {
                order.setMode(newMode);
                update(order);
                break;
            }
        }
    }

    @Override
    protected String serialize(Order order) {
        return order.getOrderId() + "| "
                + order.getUserId() + "| "
                + order.getMenuId() + "| "
                + order.getQuantity() + "| "
                + order.getOrderDate() + "| "
                + order.getOrderTime() + "| "
                + order.getRemarks() + "| "
                + order.getMode() + "| "
                + order.getOrderStatus() + "| "
                + order.isDiscountAvailable() + "| "
                + order.getDeliveryLocation() + "\n";
    }

    @Override
    protected Order deserialize(String[] data) {
        int id = Integer.parseInt(data[0].trim());
        int orderId = Integer.parseInt(data[1].trim());
        int userId = Integer.parseInt(data[2].trim());
        int menuId = Integer.parseInt(data[3].trim());
        int quantity = Integer.parseInt(data[4].trim());
        LocalDate orderDate = LocalDate.parse(data[5].trim());
        LocalTime orderTime = LocalTime.parse(data[6].trim());
        String remarks = data[7].trim();
        String mode = data[8].trim();
        OrderStatus orderStatus = OrderStatus.valueOf(data[9].trim().toUpperCase());
        String hasDiscount = data[10].trim();
        String deliveryLocation = data[11].trim();

        return new Order(id, orderId, userId, menuId, quantity, orderDate, orderTime, remarks, mode, orderStatus, deliveryLocation, hasDiscount);
    }

    @Override
    public void update(Order orderToUpdate) {
        List<Order> orders = getAllOrders();
        List<String> serializedOrders = new ArrayList<>();
        String serializedUpdateOrder = serialize(orderToUpdate);

        for (Order order : orders) {
            StringBuilder serializedOrderBuilder = new StringBuilder();
            serializedOrderBuilder.append(order.getId()).append("| ");

            if (order.getOrderId() == orderToUpdate.getOrderId()) {
                serializedOrderBuilder.append(serializedUpdateOrder);
            } else {
                serializedOrderBuilder.append(serialize(order));
            }

            serializedOrders.add(serializedOrderBuilder.toString());
        }

        fileHelper.updateFile(filePath, HEADERS, serializedOrders);
    }

    private int generateUniqueOrderId(String vendorName) {
        int maxOrderId = 0;

        // Define the path to the specific vendor's order file
        File orderFile = new File(getFullPath(ORDER_FILEPATH + vendorName + "/Orders.txt"));

        // Check if the order file exists
        if (!orderFile.exists()) {
            return 1; // If file doesn't exist, start with 1
        }

        try {
            List<String[]> orders = fileHelper.readFile(orderFile.getPath());

            for (int i = 1; i < orders.size(); i++) {
                String[] order = orders.get(i);
                try {
                    int orderId = Integer.parseInt(order[1].trim());
                    maxOrderId = Math.max(maxOrderId, orderId);
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing order ID: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading order file for " + vendorName + ": " + e.getMessage());
        }

        return maxOrderId + 1;
    }

    public List<Order> getOrderListfromVendor(String vendorName) {
        this.filePath = getFullPath(ORDER_FILEPATH + vendorName + "/Orders.txt");
        List<String[]> rawData = super.getAll();
        List<Order> orders = rawData.stream()
                .map(this::deserialize)
                .collect(Collectors.toList());

        return orders;

    }

    public boolean updateOrderStatus(int orderId, OrderStatus orderStatus, String vendorName) {
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
            if (order.getOrderId() == orderId) {
                order.setOrderStatus(orderStatus);
                success = true;
            }
            String serializedData = serialize(order);
            fileHelper.writeFile(filePath, new File(filePath), HEADERS, serializedData);
        }
        return success;
    }

    public List<Order> getAllOrders() {
        List<Order> rawData = super.getAll().stream()
                .map(this::deserialize)
                .collect(Collectors.toList());

        return rawData;
    }

    public List<Order> getByOrderIdAndVendorName(int orderId, String vendorName) {
        updateFilePath(vendorName);
        return getAll().stream()
                .map(this::deserialize)
                .filter(order -> order.getOrderId() == orderId)
                .collect(Collectors.toList());
    }
}
