package com.apu.apfood.services;

import com.apu.apfood.db.dao.NotificationDao;
import com.apu.apfood.db.dao.UserDao;
import com.apu.apfood.db.dao.VendorDao;
import com.apu.apfood.db.models.Menu;
import com.apu.apfood.db.models.User;
import com.apu.apfood.db.dao.MenuDao;
import com.apu.apfood.db.dao.OrderDao;
import com.apu.apfood.db.dao.VendorDao;
import com.apu.apfood.db.enums.NotificationStatus;
import com.apu.apfood.db.enums.NotificationType;
import com.apu.apfood.db.enums.OrderStatus;
import com.apu.apfood.db.models.Menu;
import com.apu.apfood.db.models.Order;
import com.apu.apfood.db.models.OrderDetails;
import com.apu.apfood.db.models.Vendor;
import com.apu.apfood.helpers.GUIHelper;
import com.apu.apfood.helpers.ImageHelper;
import com.apu.apfood.helpers.TableHelper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
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
        int idColumnIndex = 0; // Adjust this index based on the actual column order

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
        List<Order> orderList = getVendorOrderList(vendorName);
        // Filter orders with status PENDING
        orderList = orderList.stream()
                .filter(order -> order.getOrderStatus() == orderStatus)
                .collect(Collectors.toList());
        ordersMap.clear(); // Clear previous entries
        ordersMap = cleanOrderList(orderList);
        
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
    
    public void updateOrderStatus(JTable table, OrderStatus orderStatus)
    {
        boolean success = false;
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int orderId = Integer.parseInt(String.valueOf(table.getValueAt(selectedRow, 0)));
                success = orderDao.updateOrderStatus(orderId, orderStatus, vendorName);
                if (success == true)
                {
                    String userId = userDao.getUserId(String.valueOf(table.getValueAt(selectedRow, 1)));
                    String content = "Your order is " + orderStatus.toString();
                    notificationDao.writeNotification(userId, content, NotificationStatus.UNNOTIFIED.toString(), NotificationType.INFORMATIONAL.toString());
                }
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
