package com.apu.apfood.services;

import com.apu.apfood.db.dao.FeedbackDao;
import com.apu.apfood.db.dao.NotificationDao;
import com.apu.apfood.db.dao.UserDao;
import com.apu.apfood.db.dao.VendorDao;
import com.apu.apfood.db.models.Menu;
import com.apu.apfood.db.models.User;
import com.apu.apfood.db.dao.MenuDao;
import com.apu.apfood.db.dao.OrderDao;
import com.apu.apfood.db.dao.RunnerAvailabilityDao;
import com.apu.apfood.db.dao.RunnerTaskDao;
import com.apu.apfood.db.dao.TransactionDao;
import com.apu.apfood.db.dao.VendorDao;
import com.apu.apfood.db.enums.NotificationStatus;
import com.apu.apfood.db.enums.NotificationType;
import com.apu.apfood.db.enums.OrderStatus;
import com.apu.apfood.db.models.Feedback;
import com.apu.apfood.db.models.FoodDetails;
import com.apu.apfood.db.models.Menu;
import com.apu.apfood.db.models.Notification;
import com.apu.apfood.db.models.Order;
import com.apu.apfood.db.models.OrderDetails;
import com.apu.apfood.db.models.Vendor;
import com.apu.apfood.helpers.GUIHelper;
import com.apu.apfood.helpers.ImageHelper;
import com.apu.apfood.helpers.TableHelper;
import java.awt.Label;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
 * @author Alex
 */
public class VendorService {
    private User vendor;
    private UserDao userDao = new UserDao();
    private NotificationDao notificationDao = new NotificationDao();
    private VendorDao vendorDao = new VendorDao();
    private MenuDao menuDao = new MenuDao();
    private OrderDao orderDao = new OrderDao();
    private FeedbackDao feedbackDao = new FeedbackDao();
    private TransactionDao transactionDao = new TransactionDao();
    private RunnerTaskDao runnerTaskDao = new RunnerTaskDao();
    private RunnerAvailabilityDao runnerAvailabilityDao= new RunnerAvailabilityDao();
    private String vendorName;
    private final Map<String, Integer> menuItemIdMap = new HashMap<>();
    private Map<Integer, OrderDetails> ordersMap = new HashMap<>();
    
    // Instantiate helpers classes
    ImageHelper imageHelper = new ImageHelper();
    GUIHelper guiHelper = new GUIHelper();
    TableHelper tableHelper = new TableHelper();
    
    public VendorService(User vendor) {
        this.vendor = vendor;
        this.vendorName = vendorDao.getVendorName(vendor.getId());
    }
    
    public VendorService() {
        
    }
    
    public VendorService(VendorDao vendorDao, MenuDao menuDao) {
        this.vendorDao = vendorDao;
        this.menuDao = menuDao;
        this.vendorName = vendorDao.getVendorName(vendor.getId());
    }
    
    public List<String> getDistinctVendorNames() {
        List<Vendor> allVendors = vendorDao.getAllVendors();

        Set<String> vendorNames = allVendors.stream()
                                                  .map(Vendor::getVendorName)
                                                  .collect(Collectors.toSet());

        return new ArrayList<>(vendorNames);
    }
    
    public String getVendorName()
    {
        String vendorName = vendorDao.getVendorName(vendor.getId());
        return vendorName;
    }
    
    public List<Menu> getVendorMenuItems(String vendorName) {
        return menuDao.getAllMenuItems(vendorName);
    }
    
    public List<Order> getVendorOrderList (String vendorName)
    {
        return orderDao.getOrderListfromVendor(vendorName);
    }
    
     public static List<Menu> convertJTableToMenuList(JTable table) {
        List<Menu> menuList = new ArrayList<>();

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int rowCount = model.getRowCount();
        int idColumnIndex = 0;

        for (int i = 0; i < rowCount; i++) {
            int id = Integer.parseInt(model.getValueAt(i, idColumnIndex).toString());
            String menuName = model.getValueAt(i, 1).toString(); // Assuming 1 is the index of the menuName column
            String menuType = model.getValueAt(i, 2).toString(); // Assuming 2 is the index of the menuType column
            double price = Double.parseDouble(model.getValueAt(i, 3).toString()); // Assuming 3 is the index of the price column

            Menu menu = new Menu(id, menuName, menuType, price);
            menuList.add(menu);
        }

        return menuList;
    }
    
    public void updateMenuItems (String vendorName, List<Menu> menus)
    {
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
                existingOrderDetails.addFoodDetails(foodName, String.valueOf(order.getMenuId()), String.valueOf(order.getQuantity()), order.getRemarks());
            } else {
                // If orderMap doesn't contain an entry for this orderId, add a new entry
                OrderDetails orderDetails = new OrderDetails();
                orderDetails.setOrderId(String.valueOf(orderId));
                orderDetails.setCustomerName(userDao.getUserName(String.valueOf(order.getUserId())));
                orderDetails.setMode(order.getMode());
                orderDetails.setOrderDate(order.getOrderDate().toString());
                orderDetails.setOrderTime(order.getOrderTime().toString());
                orderDetails.setDeliveryLocation(order.getDeliveryLocation());
                String foodName = menuDao.getFoodName(vendorName, order.getMenuId());
                orderDetails.addFoodDetails(foodName, String.valueOf(order.getMenuId()), String.valueOf(order.getQuantity()), order.getRemarks());
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
            return new Object[] { menu.getMenuName(), menu.getMenuType(), menu.getPrice() };
        };
        
        tableHelper.populateTable(menuItems, menuTable, rowMapper, true);
        tableHelper.SetupTableSorter(menuTable);
        String[] types = {"food", "drink"};
        JComboBox combo = new JComboBox<String>(types);
        TableColumn col = menuTable.getColumnModel().getColumn(2);
        col.setCellEditor(new DefaultCellEditor(combo));
        
    }
    
    public void populateOrderTable(JTable ordersTable, OrderStatus orderStatus)
    {
        RefreshOrderMap(orderStatus);
        Function<OrderDetails, Object[]> rowMapper = OrderDetails -> {
            return new Object[]{
                OrderDetails.getOrderId(),
                OrderDetails.getCustomerName(),
                OrderDetails.getFoodNameList(),
                OrderDetails.getFoodQuantityList(),
                OrderDetails.getFoodRemarkList(),
                OrderDetails.getOrderDate(),
                OrderDetails.getOrderTime(),
                OrderDetails.getMode()
            };
        };
        

        tableHelper.populateTable(new ArrayList<>(ordersMap.values()), ordersTable, rowMapper, false);
        tableHelper.SetupTableSorter(ordersTable);
        int[] scalableColumns = {2, 3, 4};
        tableHelper.adjustColumnsToScalable(scalableColumns, ordersTable);
        
    }
    
    public void populateOrderHistoryTable(JTable orderHistoryTable)
    {
        RefreshOrderMap(OrderStatus.ACCEPTED);
        Function<OrderDetails, Object[]> rowMapper = OrderDetails -> {
            int orderId = Integer.parseInt(OrderDetails.getOrderId());
            Feedback feedback = feedbackDao.getFeedbackFromOrderId(orderId, vendorName);
            return new Object[]{
                OrderDetails.getOrderId(),
                OrderDetails.getCustomerName(),
                OrderDetails.getFoodNameList(),
                OrderDetails.getFoodQuantityList(),
                OrderDetails.getFoodRemarkList(),
                OrderDetails.getOrderDate(),
                OrderDetails.getOrderTime(),
                OrderDetails.getMode(),
                feedback.getRating(),
                feedback.getFeedback()
            };
        };
        tableHelper.populateTable(new ArrayList<>(ordersMap.values()), orderHistoryTable, rowMapper, false);
        tableHelper.SetupTableSorter(orderHistoryTable);
        int[] scalableColumns = {2, 3, 4};
        tableHelper.adjustColumnsToScalable(scalableColumns, orderHistoryTable);
        
    }
    
    public void populateRevenueDashboard(JLabel year, JLabel month, JLabel day, JTable revenueTable)
    {
        RefreshOrderMap(OrderStatus.ACCEPTED);
        Double yearRevenue = 0.0;
        Double monthRevenue = 0.0;
        Double dayRevenue = 0.0;
        // Define the date format expected in the string
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        
        for (OrderDetails orderDetail : ordersMap.values())
        {
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
        
        year.setText(String.format("%.2f",yearRevenue));
        month.setText(String.format("%.2f",monthRevenue));
        day.setText(String.format("%.2f",dayRevenue));
      
    }
    
    public void populateNotificationsTable(JTable notificationsTable, int userId)
    {
        List<Notification> notificationList = notificationDao.getNotificationList();
        // Filter notification with the correct userId and if it is unnotified
        notificationList = notificationList.stream()
                .filter(notification -> notification.getUserId() == userId && notification.getNotificationStatus() == NotificationStatus.UNNOTIFIED)
                .collect(Collectors.toList());
        Function<Notification, Object[]> rowMapper = notification -> {
            return new Object[]     
            { 
                notification.getId(),
                notification.getContent(),
                notification.getNotificationType(),
                notification.getNotificationStatus()
            };
        };
        
        tableHelper.populateTable(notificationList, notificationsTable, rowMapper, false);
        tableHelper.SetupTableSorter(notificationsTable);
    }
    
    
    public double getRevenueFromOrderDetails(OrderDetails orderDetail)
    {
        double revenue = 0.0;
        for(FoodDetails foodDetail : orderDetail.getFoodDetailsList())
        {
            Menu menu = menuDao.getMenuItem(vendorName, Integer.parseInt(foodDetail.getFoodId()));
            revenue += Integer.parseInt(foodDetail.getQuantity()) * menu.getPrice();
        }
        return revenue;
    }
    
    public void RefreshOrderMap(OrderStatus orderStatus)
    {
        List<Order> orderList = getVendorOrderList(vendorName);
        // Filter orders with status
        orderList = orderList.stream()
                .filter(order -> order.getOrderStatus() == orderStatus)
                .collect(Collectors.toList());
        ordersMap.clear(); // Clear previous entries
        ordersMap = cleanOrderList(orderList);
    }
    
    public void RefreshOrderMap()
    {
        List<Order> orderList = getVendorOrderList(vendorName);
        orderList = orderList.stream()
                .collect(Collectors.toList());
        ordersMap.clear(); // Clear previous entries
        ordersMap = cleanOrderList(orderList);
    }
    
    public void updateOrderStatus(JTable table, OrderStatus orderStatus)
    {
        RefreshOrderMap();
        boolean success = false;
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int orderId = Integer.parseInt(String.valueOf(table.getValueAt(selectedRow, 0)));
            OrderDetails orderDetail = ordersMap.get(orderId);
                success = orderDao.updateOrderStatus(orderId, orderStatus, vendorName);
                if (success == true)
                {
                    String userId = userDao.getUserId(String.valueOf(table.getValueAt(selectedRow, 1)));
                    String content = "Your order is " + orderStatus.toString() + " [order id: " + String.valueOf(orderId + ", vendor name: " + vendorName + "]");
                    notificationDao.writeNotification(userId, content, NotificationStatus.UNNOTIFIED.toString(), NotificationType.INFORMATIONAL.toString());
                    if (orderStatus == OrderStatus.ACCEPTED)
                    {
                        Object[][] result = runnerAvailabilityDao.getAllRunnerAvailability();
                        for (Object[] row : result)
                        {
                            int runnerId = Integer.parseInt(String.valueOf(row[1]));
                            runnerTaskDao.writeVendorTaskAssignment(orderId, runnerId, vendorName, orderDetail.getDeliveryLocation());
                           
                        }
                    }
                    else if (orderStatus == OrderStatus.DECLINED)
                    {
                        //If order is cancelled refund customer
                        double totalPrice = getRevenueFromOrderDetails(orderDetail);
                        transactionDao.writeTransaction(userId, String.valueOf(totalPrice), "Refund for [orderid: " + String.valueOf(orderId) + "]");
                    }
                }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a row in the table", "No Row Selected", JOptionPane.WARNING_MESSAGE);
        }
        
    }
    
    public void updateNotificationStatus(JTable table, NotificationStatus notificationStatus)
    {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int id = Integer.parseInt(String.valueOf(table.getValueAt(selectedRow, 0)));
            notificationDao.updateNotificationStatus(id, notificationStatus);
        } else {
            JOptionPane.showMessageDialog(null, "Please select a row in the table", "No Row Selected", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /*
    private Object[] mergeOrders(Object[] order1, Order order2) {
        Object orderId = order1[0];
        Object userId = order1[1];
        String menuItems = order1[2] + "\n" + menuDao.getFoodName(vendorName, order2.getMenuId());
        String quantity = order1[3] + "\n" + String.valueOf(order2.getQuantity());
        Object dateAndTime = order1[4];
        Object remarks = order1[5];
        Object mode = order[6];
        
        

        return new Object[]{orderId, userId, menuItems, quantity, dateAndTime, remarks, mode};
    }
*/
}
