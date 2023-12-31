package com.apu.apfood.services;

import com.apu.apfood.db.dao.NotificationDao;
import com.apu.apfood.db.dao.UserDao;
import com.apu.apfood.db.models.User;
import com.apu.apfood.db.dao.MenuDao;
import com.apu.apfood.db.dao.OrderDao;
import com.apu.apfood.db.dao.ReviewDao;
import com.apu.apfood.db.dao.RunnerAvailabilityDao;
import com.apu.apfood.db.dao.RunnerTaskDao;
import com.apu.apfood.db.dao.SubscriptionDao;
import com.apu.apfood.db.dao.TransactionDao;
import com.apu.apfood.db.dao.VendorDao;
import com.apu.apfood.db.enums.DeliveryFee;
import com.apu.apfood.db.enums.NotificationStatus;
import com.apu.apfood.db.enums.NotificationType;
import com.apu.apfood.db.enums.OrderStatus;
import com.apu.apfood.db.models.FoodDetails;
import com.apu.apfood.db.models.Menu;
import com.apu.apfood.db.models.Notification;
import com.apu.apfood.db.models.Order;
import com.apu.apfood.db.models.OrderDetails;
import com.apu.apfood.db.models.Review;
import com.apu.apfood.db.models.Vendor;
import com.apu.apfood.helpers.TableHelper;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author Maxwell
 */
public class VendorService {

    private User vendor;
    private UserDao userDao = new UserDao();
    private NotificationDao notificationDao = new NotificationDao();
    private VendorDao vendorDao = new VendorDao();
    private MenuDao menuDao = new MenuDao();
    private OrderDao orderDao = new OrderDao();
    private ReviewDao reviewDao = new ReviewDao();
    private TransactionDao transactionDao = new TransactionDao();
    private RunnerTaskDao runnerTaskDao = new RunnerTaskDao();
    private RunnerAvailabilityDao runnerAvailabilityDao = new RunnerAvailabilityDao();
    private String vendorName;
    private SubscriptionService subscriptionService = new SubscriptionService(new SubscriptionDao(), new TransactionDao());
    private final Map<String, Integer> menuItemIdMap = new HashMap<>();
    private Map<Integer, OrderDetails> ordersMap = new HashMap<>();

    // Instantiate helpers classes
    TableHelper tableHelper = new TableHelper();

    public VendorService(User vendor) {
        this.vendor = vendor;
        this.vendorName = vendorDao.getVendorName(vendor.getId());
        this.reviewDao = new ReviewDao(vendorName);
    }

    public VendorService() {

    }

    public VendorService(VendorDao vendorDao, MenuDao menuDao) {
        this.vendorDao = vendorDao;
        this.menuDao = menuDao;
    }

    public List<String> getDistinctVendorNames() {
        List<Vendor> allVendors = vendorDao.getAllVendors();

        Set<String> vendorNames = allVendors.stream()
                .map(Vendor::getVendorName)
                .collect(Collectors.toSet());

        return new ArrayList<>(vendorNames);
    }

    public String getVendorName() {
        String vendorName = vendorDao.getVendorName(vendor.getId());
        return vendorName;
    }

    public List<Menu> getVendorMenuItems(String vendorName) {
        return menuDao.getAllMenuItems(vendorName);
    }

    public List<Order> getVendorOrderList(String vendorName) {
        return orderDao.getOrderListfromVendor(vendorName);
    }

    public List<Menu> convertJTableToMenuList(JTable table) {
        List<Menu> menuList = new ArrayList<>();

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int rowCount = model.getRowCount();
        int idColumnIndex = 0;

        for (int i = 0; i < rowCount; i++) {
            int id = Integer.parseInt(model.getValueAt(i, idColumnIndex).toString());
            String menuName = model.getValueAt(i, 1).toString(); // Assuming 1 is the index of the menuName column
            String menuType = model.getValueAt(i, 2).toString(); // Assuming 2 is the index of the menuType column
            double price = 0.0;
            try 
            {
                price = Double.parseDouble(model.getValueAt(i, 3).toString()); // Assuming 3 is the index of the price column
            } 
            catch (NumberFormatException e) 
            {
                // Throw the error
                throw new NumberFormatException("Price value (" + model.getValueAt(i, 3).toString() + ") for the menu:" + menuName + " contains letters or isn't a number");
            }

            Menu menu = new Menu(id, menuName, menuType, price);
            menuList.add(menu);
        }

        return menuList;
    }

    public void updateMenuItems(String vendorName, List<Menu> menus) {
        menuDao.updateMenuItems(vendorName, menus);
    }

    public Map<Integer, OrderDetails> cleanOrderList(List<Order> orderList) {
        Map<Integer, OrderDetails> orderMap = new HashMap<>();

        for (Order order : orderList) {
            int orderId = order.getOrderId();
            if (orderMap.containsKey(orderId)) {
                // If orderMap already contains an entry for this orderId, update the existing entry
                OrderDetails existingOrderDetails = orderMap.get(orderId);
                String foodName = menuDao.getFoodName(vendorName, order.getMenuId());
                double price = menuDao.getFoodPrice(vendorName, order.getMenuId());
                existingOrderDetails.addFoodDetails(foodName, String.valueOf(order.getMenuId()), String.valueOf(order.getQuantity()), order.getRemarks(), price);
            } else {
                // If orderMap doesn't contain an entry for this orderId, add a new entry
                OrderDetails orderDetails = new OrderDetails();
                orderDetails.setAccountId(String.valueOf(order.getUserId()));
                orderDetails.setOrderId(String.valueOf(orderId));
                orderDetails.setCustomerName(userDao.getUserName(String.valueOf(order.getUserId())));
                orderDetails.setMode(order.getMode());
                orderDetails.setOrderDate(order.getOrderDate().toString());
                orderDetails.setOrderTime(order.getOrderTime().toString());
                orderDetails.setDeliveryLocation(order.getDeliveryLocation());
                orderDetails.setOrderStatus(order.getOrderStatus().toString());
                String foodName = menuDao.getFoodName(vendorName, order.getMenuId());
                double price = menuDao.getFoodPrice(vendorName, order.getMenuId());
                orderDetails.addFoodDetails(foodName, String.valueOf(order.getMenuId()), String.valueOf(order.getQuantity()), order.getRemarks(), price);
                orderMap.put(orderId, orderDetails);

            }
        }

        return orderMap;
    }

    public void populateMenuTable(JTable menuTable) {
        List<Menu> menuItems = getVendorMenuItems(vendorName);
        menuItemIdMap.clear(); // Clear previous entries

        Function<Menu, Object[]> rowMapper = menu -> {
            menuItemIdMap.put(menu.getMenuName(), menu.getId());
            return new Object[]{menu.getMenuName(), menu.getMenuType(), menu.getPrice()};
        };

        tableHelper.populateTable(menuItems, menuTable, rowMapper, true);
        tableHelper.SetupTableSorter(menuTable);
        String[] types = {"food", "drink"};
        JComboBox combo = new JComboBox<String>(types);
        TableColumn col = menuTable.getColumnModel().getColumn(2);
        col.setCellEditor(new DefaultCellEditor(combo));

    }

    public void populateOrderTable(JTable ordersTable, OrderStatus orderStatus) {
        RefreshOrderMap(orderStatus);
        Function<OrderDetails, Object[]> rowMapper = OrderDetails -> {
            return new Object[]{
                OrderDetails.getOrderId(),
                OrderDetails.getCustomerName(),
                OrderDetails.getFoodNameList(),
                OrderDetails.getFoodQuantityList(),
                OrderDetails.getFoodRemarkList(),
                OrderDetails.getOrderDate(),
                OrderDetails.getOrderTime().toString().split("\\.")[0],
                OrderDetails.getMode()
            };
        };

        tableHelper.populateTable(new ArrayList<>(ordersMap.values()), ordersTable, rowMapper, false);
        tableHelper.SetupTableSorter(ordersTable);
        int[] scalableColumns = {2, 3, 4};
        tableHelper.adjustColumnsToScalable(scalableColumns, ordersTable);

    }

    public void populateOrderHistoryTable(JTable orderHistoryTable) {
        //Get orders from txt file and filter by ACCEPTED or DECLINE orderstatuses
        List<Order> orderList = orderDao.getOrderListfromVendor(vendorName);
        orderList = orderList.stream()
                .filter(order -> order.getOrderStatus() == OrderStatus.READY || order.getOrderStatus() == OrderStatus.DECLINED)
                .collect(Collectors.toList());
        RefreshOrderMap(orderList);
        Function<OrderDetails, Object[]> rowMapper = OrderDetails -> {
            int orderId = Integer.parseInt(OrderDetails.getOrderId());
            Review review = reviewDao.getReviewsFromOrderId(orderId, vendorName);
            if (review == null) {
                review = new Review("No Feedback yet", 0, orderId);
            }
            return new Object[]{
                OrderDetails.getOrderId(),
                OrderDetails.getCustomerName(),
                OrderDetails.getFoodNameList(),
                OrderDetails.getFoodQuantityList(),
                OrderDetails.getFoodRemarkList(),
                OrderDetails.getOrderDate(),
                OrderDetails.getOrderTime().toString().split("\\.")[0],
                OrderDetails.getMode(),
                review.getRating(),
                review.getFeedback(),
                OrderDetails.getOrderStatus()
            };
        };
        tableHelper.populateTable(new ArrayList<>(ordersMap.values()), orderHistoryTable, rowMapper, false);
        tableHelper.SetupTableSorter(orderHistoryTable);
        int[] scalableColumns = {2, 3, 4};
        tableHelper.adjustColumnsToScalable(scalableColumns, orderHistoryTable);

    }

    public void populateRevenueDashboard(JLabel year, JLabel month, JLabel day, JTable revenueTable) {
        RefreshOrderMap(OrderStatus.READY);
        Double yearRevenue = 0.0;
        Double monthRevenue = 0.0;
        Double dayRevenue = 0.0;
        // Define the date format expected in the string
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (OrderDetails orderDetail : ordersMap.values()) {
            LocalDate orderDate = LocalDate.parse(orderDetail.getOrderDate(), dateFormatter);
            LocalDate currentDate = LocalDate.now();

            // Check if the date and time is under a year ago
            if (orderDate.getYear() == currentDate.getYear()) {
                yearRevenue += getRevenueFromOrderDetails(orderDetail);
            }

            // Check if the date and time is under a month ago
            if (orderDate.getMonth() == currentDate.getMonth()) {
                monthRevenue += getRevenueFromOrderDetails(orderDetail);
            }

            // Check if the date and time is under today
            if (orderDate.isEqual(LocalDate.now())) {
                dayRevenue += getRevenueFromOrderDetails(orderDetail);
            }
        }

        //Set the revenue labels' text accordingly 
        year.setText(String.format("%.2f", yearRevenue));
        month.setText(String.format("%.2f", monthRevenue));
        day.setText(String.format("%.2f", dayRevenue));

    }

    public void populateRevenueOrdersTable(JTable revenueOrdersTable, LocalDate beforeDate, LocalDate afterDate) {
        List<Order> orderList = orderDao.getOrderListfromVendor(vendorName);
        orderList = orderList.stream()
                .filter(order -> order.getOrderDate().isAfter(beforeDate) && order.getOrderDate().isBefore(afterDate) && order.getOrderStatus() == OrderStatus.READY)
                .collect(Collectors.toList());
        RefreshOrderMap(orderList);
        Function<OrderDetails, Object[]> rowMapper = orderDetails -> {
            return new Object[]{
                orderDetails.getOrderId(),
                orderDetails.getCustomerName(),
                orderDetails.getFoodNameList(),
                orderDetails.getFoodQuantityList(),
                orderDetails.getFoodPriceList(),
                orderDetails.getOrderDate(),
                orderDetails.getOrderTime().toString().split("\\.")[0],
                getRevenueFromOrderDetails(orderDetails)
            };
        };
        tableHelper.populateTable(new ArrayList<>(ordersMap.values()), revenueOrdersTable, rowMapper, false);
        tableHelper.SetupTableSorter(revenueOrdersTable);
        int[] scalableColumns = {2, 3, 4};
        tableHelper.adjustColumnsToScalable(scalableColumns, revenueOrdersTable);

    }

    public void populateNotificationsTable(JTable notificationsTable, int userId) {
        List<Notification> notificationList = notificationDao.getNotificationList();
        // Filter notification with the vendorName and if it is unnotified
        notificationList = notificationList.stream()
                .filter(notification -> notification.getContent().contains(vendorName) && vendorDao.getVendorName(notification.getUserId()).equals(vendorName) && notification.getNotificationStatus() == NotificationStatus.UNNOTIFIED)
                .collect(Collectors.toList());
        Function<Notification, Object[]> rowMapper = notification -> {
            return new Object[]{
                notification.getId(),
                formatNotificationContent(notification.getContent()),
                notification.getNotificationType(),
                notification.getNotificationStatus()
            };
        };

        tableHelper.populateTable(notificationList, notificationsTable, rowMapper, false);
        tableHelper.SetupTableSorter(notificationsTable);
    }

    public String formatNotificationContent(String content)
    {
        // Find the index of "[order id:" in the content
        int startIndex = content.indexOf("[order id:");

        // Check if the substring is found
        if (startIndex != -1) {
            // Find the index of the corresponding "]"
            int endIndex = content.indexOf("]", startIndex);

            // Check if the closing "]" is found
            if (endIndex != -1) {
                // Extract the substring from the content
                content = content.substring(0, startIndex) + content.substring(startIndex, content.indexOf(",", startIndex)) + "]";
            } 
        }
        return content;
    }
    
    public double getRevenueFromOrderDetails(OrderDetails orderDetail) {
        double revenue = 0.0;
        for (FoodDetails foodDetail : orderDetail.getFoodDetailsList()) {
            Menu menu = menuDao.getMenuItem(vendorName, Integer.parseInt(foodDetail.getFoodId()));
            revenue += Integer.parseInt(foodDetail.getQuantity()) * menu.getPrice();
        }
        return revenue;
    }

    public void RefreshOrderMap(OrderStatus orderStatus) {
        List<Order> orderList = getVendorOrderList(vendorName);
        // Filter orders with status
        orderList = orderList.stream()
                .filter(order -> order.getOrderStatus() == orderStatus)
                .collect(Collectors.toList());
        ordersMap.clear(); // Clear previous entries
        ordersMap = cleanOrderList(orderList);
    }

    public void RefreshOrderMap() {
        List<Order> orderList = getVendorOrderList(vendorName);
        orderList = orderList.stream()
                .collect(Collectors.toList());
        ordersMap.clear(); // Clear previous entries
        ordersMap = cleanOrderList(orderList);
    }

    public void RefreshOrderMap(List<Order> orderList) {
        orderList = orderList.stream()
                .collect(Collectors.toList());
        ordersMap.clear(); // Clear previous entries
        ordersMap = cleanOrderList(orderList);
    }

    public void updateOrderStatus(JTable table, OrderStatus orderStatus) {
        RefreshOrderMap();
        boolean success = false;
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int orderId = Integer.parseInt(String.valueOf(table.getValueAt(selectedRow, 0)));
            OrderDetails orderDetail = ordersMap.get(orderId);
            success = orderDao.updateOrderStatus(orderId, orderStatus, vendorName);
            if (success == true) {
                String userId = userDao.getUserId(String.valueOf(table.getValueAt(selectedRow, 1)));
                if (orderStatus == OrderStatus.ACCEPTED) {
                    String content = "Order has been accepted" + " [order id: " + String.valueOf(orderId + ", vendor name: " + vendorName + "]");
                    notificationDao.writeNotification(userId, content, NotificationStatus.UNNOTIFIED.toString(), NotificationType.INFORMATIONAL.toString());
                } else if (orderStatus == OrderStatus.READY) {
                    String content = "Order is ready for " + orderDetail.getMode();
                    if (orderDetail.getMode().equals("Delivery")) {
                        Object[][] result = runnerAvailabilityDao.getAllRunnerAvailability();
                        for (Object[] row : result) {
                            int runnerId = Integer.parseInt(String.valueOf(row[1]));
                            runnerTaskDao.writeVendorTaskAssignment(orderId, runnerId, vendorName, orderDetail.getDeliveryLocation());
                        }
                        content = "Order has been sent for delivery" + " [order id: " + String.valueOf(orderId + ", vendor name: " + vendorName + "]");
                    }
                    notificationDao.writeNotification(userId, content, NotificationStatus.UNNOTIFIED.toString(), NotificationType.INFORMATIONAL.toString());
                } else if (orderStatus == OrderStatus.DECLINED) {
                    String content = "Order has been cancelled" + " [order id: " + String.valueOf(orderId + ", vendor name: " + vendorName + "]");
                    notificationDao.writeNotification(userId, content, NotificationStatus.UNNOTIFIED.toString(), NotificationType.TRANSACTIONAL.toString());
                    //If order is cancelled refund customer
                    double totalPrice = getRevenueFromOrderDetails(orderDetail);
                    // Check if the user is subscribed and the total is at least RM 12.00 for the 10% discount
                    boolean isSubscribed = subscriptionService.isUserSubscribed(Integer.parseInt(orderDetail.getAccountId()));
                    double discountRate = (isSubscribed && totalPrice >= 12.00) ? 0.9 : 1.0; // Apply 10% discount if conditions are met
                    // Apply discount on items' total cost
                    totalPrice *= discountRate;
                    //If order mode is delivery refund the customer with the added deliveryfee
                    if (orderDetail.getMode().equals("Delivery")) {
                        double fee = 0.0;
                        try {
                            fee = DeliveryFee.getFeeForBlock(orderDetail.getDeliveryLocation());

                        } catch (IllegalArgumentException ex) {
                            JOptionPane.showMessageDialog(null, ex.getMessage(), "Delivery Location invalid", JOptionPane.WARNING_MESSAGE);
                        }

                        totalPrice += fee;
                    }

                    transactionDao.writeTransaction(userId, String.valueOf(totalPrice), "Order Declined");
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a row in the table", "No Row Selected", JOptionPane.WARNING_MESSAGE);
        }

    }

    public void updateNotificationStatus(JTable table, NotificationStatus notificationStatus) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int id = Integer.parseInt(String.valueOf(table.getValueAt(selectedRow, 0)));
            notificationDao.updateNotificationStatus(id, notificationStatus);
        } else {
            JOptionPane.showMessageDialog(null, "Please select a row in the table", "No Row Selected", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void populateDateComboBoxes(JComboBox<Integer> yearComboBox,
            JComboBox<Integer> monthComboBox,
            JComboBox<Integer> dayComboBox) {
        // Populate year combo box with a range of years
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = currentYear; year >= 1970; year--) {
            yearComboBox.addItem(year);
        }

        // Populate month combo box
        for (int month = 1; month <= 12; month++) {
            monthComboBox.addItem(month);
        }
        // Populate day combo box
        for (int day = 1; day <= 31; day++) {
            dayComboBox.addItem(day);
        }
    }

    public int getVendorUserId(String vendorName) {
        List<Vendor> allVendors = vendorDao.getAllVendors();

        for (Vendor vendor : allVendors) {
            if (vendor.getVendorName().equalsIgnoreCase(vendorName)) {
                return vendor.getUserId();
            }
        }
        return -1;
    }
    
    public Map<Integer, Double> getMenuPriceMap(String vendorName) {
        List<Menu> menuItems = menuDao.getAllMenuItems(vendorName);
        return menuItems.stream().collect(Collectors.toMap(Menu::getId, Menu::getPrice));
    }
}
