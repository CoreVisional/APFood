package com.apu.apfood.gui;

import com.apu.apfood.db.dao.MenuDao;
import com.apu.apfood.db.dao.NotificationDao;
import com.apu.apfood.db.dao.OrderDao;
import com.apu.apfood.db.dao.ReviewDao;
import com.apu.apfood.db.dao.RunnerTaskDao;
import com.apu.apfood.db.dao.SubscriptionDao;
import com.apu.apfood.db.dao.TransactionDao;
import com.apu.apfood.db.dao.UserDao;
import com.apu.apfood.helpers.GUIHelper;
import com.apu.apfood.helpers.ImageHelper;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.apu.apfood.db.dao.VendorDao;
import com.apu.apfood.db.enums.DeliveryFee;
import com.apu.apfood.db.enums.NotificationStatus;
import com.apu.apfood.db.enums.NotificationType;
import static com.apu.apfood.db.enums.NotificationType.INFORMATIONAL;
import static com.apu.apfood.db.enums.NotificationType.PUSH;
import static com.apu.apfood.db.enums.NotificationType.TRANSACTIONAL;
import com.apu.apfood.db.enums.OrderStatus;
import com.apu.apfood.db.models.Menu;
import com.apu.apfood.db.models.Notification;
import com.apu.apfood.db.models.Order;
import com.apu.apfood.db.models.Review;
import com.apu.apfood.db.models.Subscription;
import com.apu.apfood.db.models.Transaction;
import com.apu.apfood.db.models.User;
import com.apu.apfood.helpers.TableHelper;
import com.apu.apfood.services.NotificationService;
import com.apu.apfood.services.OrderService;
import com.apu.apfood.services.ReviewService;
import com.apu.apfood.services.SubscriptionService;
import com.apu.apfood.services.TransactionService;
import com.apu.apfood.services.UserService;
import com.apu.apfood.services.VendorService;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class CustomerForm extends javax.swing.JFrame {
    
    private String selectedVendorName = null;
    private final Map<String, Integer> menuItemIdMap = new HashMap<>();
    
    // Instantiate DAOs
    UserDao userDao = new UserDao();
    VendorDao vendorDao = new VendorDao();
    MenuDao menuDao = new MenuDao();
    OrderDao orderDao = new OrderDao(selectedVendorName);
    NotificationDao notificationDao = new NotificationDao();
    RunnerTaskDao runnerTaskDao = new RunnerTaskDao();
    TransactionDao transactionDao = new TransactionDao();
    SubscriptionDao subscriptionDao = new SubscriptionDao();
    ReviewDao reviewDao = new ReviewDao(selectedVendorName);
    
    // Instantiate services
    UserService userService = new UserService(userDao);
    VendorService vendorService = new VendorService(vendorDao, menuDao);
    NotificationService notificationService = new NotificationService(notificationDao);
    TransactionService transactionService = new TransactionService(transactionDao);
    SubscriptionService subscriptionService = new SubscriptionService(subscriptionDao, transactionDao);
    ReviewService reviewService = new ReviewService(reviewDao, userDao, userService);
    OrderService orderService = new OrderService(orderDao, vendorService, subscriptionService, notificationService);
    
    // Instantiate helpers classes
    ImageHelper imageHelper = new ImageHelper();
    TableHelper tableHelper = new TableHelper();
    GUIHelper guiHelper = new GUIHelper();
    
    // Menu Item count
    private int menuItemQuantity = 0;
    
    private final List<Object[]> cartItems = new ArrayList<>();

    private final User loggedInUser;
    private boolean isUserSubscribed;
    
    /**
     * Creates new form VendorFrame
     * @param user The logged-in user object
     */
    public CustomerForm(User user) {
        this.loggedInUser = user;
        initComponents();
        initCustomComponents();
    }

    private void initCustomComponents () {
        isUserSubscribed = subscriptionService.isUserSubscribed(loggedInUser.getId());
        userFullNameLabel.setText(loggedInUser.getName());
        userEmailLabel.setText(loggedInUser.getEmail());
        
        imageHelper.setFrameIcon(this, "/icons/apu-logo.png");
        GUIHelper.JFrameSetup(this);
        
        // Calling methods to populate tables
        populateOngoingOrderDeliveryTable();
        populateVendorsTable();
        
        updateCreditBalanceDisplay();
    }
    
    private void populateVendorsTable() {
        List<String> vendorNames = vendorService.getDistinctVendorNames();
        
        // Using the overloaded populateTable method with a rowMapper
        Function<String, Object[]> rowMapper = name -> new Object[]{ name };
        
        tableHelper.populateTable(vendorNames, vendorsTable, rowMapper, true);
        tableHelper.centerTableValues(vendorsTable);
    }
    
    private void populateOngoingOrderDeliveryTable() {
        Map<String, Set<String>> ongoingOrdersMap = runnerTaskDao.getOngoingDeliveriesForUser(loggedInUser.getId());

        // Create a list for table data
        List<Object[]> tableData = new ArrayList<>();

        for (Map.Entry<String, Set<String>> entry : ongoingOrdersMap.entrySet()) {
            String orderId = entry.getKey();
            for (String vendorName : entry.getValue()) {
                List<Order> orders = orderDao.getByOrderIdAndVendorName(Integer.parseInt(orderId), vendorName);
                orders.removeIf(order -> order.getUserId() != loggedInUser.getId()); // Filter orders not belonging to the logged-in user

                if (!orders.isEmpty()) {
                    Order representativeOrder = orders.get(0); // Use any order to represent common attributes

                    // Format date and time for display
                    String orderDate = representativeOrder.getOrderDate().toString();
                    String orderTime = representativeOrder.getOrderTime().truncatedTo(ChronoUnit.MINUTES).toString();

                    // Calculate total amount including delivery fee
                    double totalAmount = orderService.calculateTotalAmountForGroupedOrders(orders, vendorName, loggedInUser.getId());

                    // Add a row to the table data
                    tableData.add(new Object[] {
                        vendorName,
                        orderDate,
                        orderTime,
                        String.format("RM %.2f", totalAmount)
                    });
                }
            }
        }

        tableHelper.populateTable(tableData, ongoingOrderDeliveryTbl, row -> row);
        tableHelper.centerTableValues(ongoingOrderDeliveryTbl);
    }

    private void updateCreditBalanceDisplay() {
        String balance = transactionService.getTotalBalance(String.valueOf(loggedInUser.getId()));
        userCreditBalanceLabel1.setText("RM " + balance);
        userCreditBalanceLabel2.setText("RM " + balance);
    }
    
    private void updateSubscriptionStatusDisplay() {

        if (isUserSubscribed) {
            subscriptionValidityPanel.setVisible(true);
            subscriptionStatusLabel.setText("Active");
            subscriptionStatusLabel.setForeground(new Color(0, 128, 0)); // Set text color to green

            Subscription latestSubscription = subscriptionService.getLatestActiveSubscription(loggedInUser.getId());
            if (latestSubscription != null) {
                subscriptionValidityLabel.setText(latestSubscription.getSubscriptionEndDate().toString());
            }
        } else {
            subscriptionValidityPanel.setVisible(false);
            subscriptionStatusLabel.setText("Not Subscribed");
            subscriptionStatusLabel.setForeground(Color.BLACK); // Set text color back to default
            subscriptionValidityLabel.setText("");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sidePanel = new javax.swing.JPanel();
        apfoodTxtLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel3 = new javax.swing.JPanel();
        homeSidebarBtn = new javax.swing.JButton();
        cafeteriaSidebarBtn = new javax.swing.JButton();
        financeSidebarBtn = new javax.swing.JButton();
        notificationsSidebarBtn = new javax.swing.JButton();
        subscriptionsSidebarBtn = new javax.swing.JButton();
        jPanel46 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        topBarPanel = new javax.swing.JPanel();
        userFullNameLabel = new javax.swing.JLabel();
        userEmailLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        contentPanel = new javax.swing.JPanel();
        homePanel = new javax.swing.JPanel();
        homeContentPanel = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        quickVendorSelecctionBtn = new javax.swing.JButton();
        jPanel22 = new javax.swing.JPanel();
        jLabel43 = new javax.swing.JLabel();
        userCreditBalanceLabel1 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        historyBtn = new javax.swing.JButton();
        jPanel21 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        ongoingOrderDeliveryTbl = new javax.swing.JTable();
        jPanel47 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        ongoingOrderDeliveryTbl1 = new javax.swing.JTable();
        historyPanel = new javax.swing.JPanel();
        historyContentPanel = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        orderHistoryTbl = new javax.swing.JTable();
        jPanel24 = new javax.swing.JPanel();
        reorderBtn = new javax.swing.JButton();
        viewPastOrderDetailsBtn = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        orderHistoryBackBtn = new javax.swing.JButton();
        vendorsPanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        vendorsTable = new javax.swing.JTable();
        jPanel20 = new javax.swing.JPanel();
        viewReviewsBtnFromVendorPanel = new javax.swing.JButton();
        browseMenuBtn = new javax.swing.JButton();
        vendorMenuPanel = new javax.swing.JPanel();
        jPanel25 = new javax.swing.JPanel();
        jPanel26 = new javax.swing.JPanel();
        jPanel28 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        vendorMenuTbl = new javax.swing.JTable();
        jPanel31 = new javax.swing.JPanel();
        jPanel30 = new javax.swing.JPanel();
        decreaseItemQtyBtn = new javax.swing.JButton();
        increaseItemQtyBtn = new javax.swing.JButton();
        itemQtyLabel = new javax.swing.JLabel();
        jPanel32 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        itemRemarksTxtArea = new javax.swing.JTextArea();
        addToCartBtn = new javax.swing.JButton();
        updateCartBtn = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        searchVendorMenuTxtField = new javax.swing.JTextField();
        jPanel40 = new javax.swing.JPanel();
        vendorNameLabel = new javax.swing.JLabel();
        jPanel27 = new javax.swing.JPanel();
        orderSummaryPanel = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        orderCartTbl = new javax.swing.JTable();
        jPanel33 = new javax.swing.JPanel();
        clearCartBtn = new javax.swing.JButton();
        placeOrderBtn = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel37 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel34 = new javax.swing.JPanel();
        costSummaryParentPanel = new javax.swing.JPanel();
        costSummaryWithDeliveryPanel = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        costSummaryPanel = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        costSummaryWithDiscountPanel = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        costSummaryWithDeliveryAndDiscountPanel = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        deliveryLocationsComboBox = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        orderModesComboBox = new javax.swing.JComboBox<>();
        viewReviewsFromMenuBtn = new javax.swing.JButton();
        vendorReviewsPanel = new javax.swing.JPanel();
        jPanel42 = new javax.swing.JPanel();
        jPanel44 = new javax.swing.JPanel();
        jPanel45 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        reviewsTbl = new javax.swing.JTable();
        financePanel = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        topUpPanel = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jPanel36 = new javax.swing.JPanel();
        jLabel45 = new javax.swing.JLabel();
        userCreditBalanceLabel2 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        transactionsTbl = new javax.swing.JTable();
        notificationsPanel = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        notificationsTbl = new javax.swing.JTable();
        jPanel17 = new javax.swing.JPanel();
        markAllAsReadBtn = new javax.swing.JButton();
        jPanel18 = new javax.swing.JPanel();
        jLabel41 = new javax.swing.JLabel();
        searchNotificationTxtField = new javax.swing.JTextField();
        jPanel19 = new javax.swing.JPanel();
        refreshNotificationBtn = new javax.swing.JButton();
        subscriptionsPanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel29 = new javax.swing.JPanel();
        jPanel38 = new javax.swing.JPanel();
        jPanel41 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        subscriptionStatusLabel = new javax.swing.JLabel();
        subscriptionValidityPanel = new javax.swing.JPanel();
        jLabel39 = new javax.swing.JLabel();
        subscriptionValidityLabel = new javax.swing.JLabel();
        jPanel39 = new javax.swing.JPanel();
        jPanel35 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel43 = new javax.swing.JPanel();
        subscribeBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Home - APFood");
        setResizable(false);

        sidePanel.setBackground(new java.awt.Color(0, 89, 100));
        sidePanel.setPreferredSize(new java.awt.Dimension(250, 900));

        apfoodTxtLabel.setBackground(new java.awt.Color(255, 255, 255));
        apfoodTxtLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        apfoodTxtLabel.setForeground(new java.awt.Color(255, 255, 255));
        apfoodTxtLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        apfoodTxtLabel.setText("APFood");
        apfoodTxtLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jSeparator1.setBackground(new java.awt.Color(204, 204, 204));
        jSeparator1.setForeground(new java.awt.Color(255, 255, 255));

        jPanel3.setForeground(new java.awt.Color(30, 30, 30));
        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridLayout(5, 1, 0, 30));

        homeSidebarBtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        homeSidebarBtn.setForeground(new java.awt.Color(255, 255, 255));
        homeSidebarBtn.setText("Home");
        homeSidebarBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        homeSidebarBtn.setFocusPainted(false);
        homeSidebarBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                homeSidebarBtnMousePressed(evt);
            }
        });
        jPanel3.add(homeSidebarBtn);

        cafeteriaSidebarBtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        cafeteriaSidebarBtn.setForeground(new java.awt.Color(255, 255, 255));
        cafeteriaSidebarBtn.setText("Cafeterias");
        cafeteriaSidebarBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cafeteriaSidebarBtn.setFocusPainted(false);
        cafeteriaSidebarBtn.setPreferredSize(new java.awt.Dimension(50, 30));
        cafeteriaSidebarBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                cafeteriaSidebarBtnMousePressed(evt);
            }
        });
        jPanel3.add(cafeteriaSidebarBtn);

        financeSidebarBtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        financeSidebarBtn.setForeground(new java.awt.Color(255, 255, 255));
        financeSidebarBtn.setText("Finance");
        financeSidebarBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        financeSidebarBtn.setFocusPainted(false);
        financeSidebarBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                financeSidebarBtnMousePressed(evt);
            }
        });
        jPanel3.add(financeSidebarBtn);

        notificationsSidebarBtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        notificationsSidebarBtn.setForeground(new java.awt.Color(255, 255, 255));
        notificationsSidebarBtn.setText("Notifications");
        notificationsSidebarBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        notificationsSidebarBtn.setFocusPainted(false);
        notificationsSidebarBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                notificationsSidebarBtnMousePressed(evt);
            }
        });
        jPanel3.add(notificationsSidebarBtn);

        subscriptionsSidebarBtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        subscriptionsSidebarBtn.setForeground(new java.awt.Color(255, 255, 255));
        subscriptionsSidebarBtn.setText("Subscriptions");
        subscriptionsSidebarBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        subscriptionsSidebarBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                subscriptionsSidebarBtnMousePressed(evt);
            }
        });
        jPanel3.add(subscriptionsSidebarBtn);

        jPanel46.setOpaque(false);
        jPanel46.setLayout(new java.awt.BorderLayout());

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Logout");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.setFocusPainted(false);
        jPanel46.add(jButton1, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout sidePanelLayout = new javax.swing.GroupLayout(sidePanel);
        sidePanel.setLayout(sidePanelLayout);
        sidePanelLayout.setHorizontalGroup(
            sidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sidePanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(sidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel46, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(apfoodTxtLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(30, 30, 30))
        );
        sidePanelLayout.setVerticalGroup(
            sidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidePanelLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(apfoodTxtLabel)
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 362, Short.MAX_VALUE)
                .addComponent(jPanel46, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );

        getContentPane().add(sidePanel, java.awt.BorderLayout.LINE_START);

        mainPanel.setLayout(new java.awt.BorderLayout());

        topBarPanel.setBackground(new java.awt.Color(255, 255, 255));
        topBarPanel.setPreferredSize(new java.awt.Dimension(1350, 80));

        userFullNameLabel.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        userFullNameLabel.setForeground(new java.awt.Color(0, 0, 0));
        userFullNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        userFullNameLabel.setText("Full Name");

        userEmailLabel.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        userEmailLabel.setForeground(new java.awt.Color(102, 102, 102));
        userEmailLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        userEmailLabel.setText("TP0xxxxxx@mail.apu.edu.my");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/profile-icon.png"))); // NOI18N
        jLabel5.setName(""); // NOI18N

        javax.swing.GroupLayout topBarPanelLayout = new javax.swing.GroupLayout(topBarPanel);
        topBarPanel.setLayout(topBarPanelLayout);
        topBarPanelLayout.setHorizontalGroup(
            topBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, topBarPanelLayout.createSequentialGroup()
                .addContainerGap(1084, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(32, 32, 32)
                .addGroup(topBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(userEmailLabel)
                    .addComponent(userFullNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );
        topBarPanelLayout.setVerticalGroup(
            topBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topBarPanelLayout.createSequentialGroup()
                .addGroup(topBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(topBarPanelLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(topBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(topBarPanelLayout.createSequentialGroup()
                                .addComponent(userFullNameLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(userEmailLabel)
                                .addGap(2, 2, 2))))
                    .addGroup(topBarPanelLayout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jLabel4)))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        mainPanel.add(topBarPanel, java.awt.BorderLayout.PAGE_START);

        contentPanel.setLayout(new java.awt.CardLayout());

        homePanel.setLayout(new java.awt.BorderLayout());

        jPanel23.setLayout(new java.awt.BorderLayout());

        quickVendorSelecctionBtn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        quickVendorSelecctionBtn.setForeground(new java.awt.Color(255, 255, 255));
        quickVendorSelecctionBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/food-icon.png"))); // NOI18N
        quickVendorSelecctionBtn.setText(" Food");
        quickVendorSelecctionBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        quickVendorSelecctionBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                quickVendorSelecctionBtnMousePressed(evt);
            }
        });
        jPanel23.add(quickVendorSelecctionBtn, java.awt.BorderLayout.CENTER);

        jPanel22.setLayout(new java.awt.BorderLayout());

        jLabel43.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel43.setForeground(new java.awt.Color(255, 255, 255));
        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel43.setText("Balance:");
        jLabel43.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jLabel43.setName(""); // NOI18N
        jLabel43.setPreferredSize(new java.awt.Dimension(120, 20));
        jPanel22.add(jLabel43, java.awt.BorderLayout.LINE_START);

        userCreditBalanceLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        userCreditBalanceLabel1.setForeground(new java.awt.Color(255, 255, 255));
        userCreditBalanceLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        userCreditBalanceLabel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel22.add(userCreditBalanceLabel1, java.awt.BorderLayout.CENTER);

        jPanel10.setLayout(new java.awt.BorderLayout());

        historyBtn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        historyBtn.setForeground(new java.awt.Color(255, 255, 255));
        historyBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/history-icon.png"))); // NOI18N
        historyBtn.setText(" Order History");
        historyBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        historyBtn.setPreferredSize(new java.awt.Dimension(100, 35));
        historyBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                historyBtnMousePressed(evt);
            }
        });
        jPanel10.add(historyBtn, java.awt.BorderLayout.CENTER);

        jPanel21.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Ongoing Deliveries", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14), new java.awt.Color(255, 255, 255))); // NOI18N

        ongoingOrderDeliveryTbl.setForeground(new java.awt.Color(255, 255, 255));
        ongoingOrderDeliveryTbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Vendor", "Order Date", "Order Time", "Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ongoingOrderDeliveryTbl.setRowHeight(30);
        ongoingOrderDeliveryTbl.setShowGrid(true);
        jScrollPane2.setViewportView(ongoingOrderDeliveryTbl);

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel47.setBackground(new java.awt.Color(255, 204, 0));

        ongoingOrderDeliveryTbl1.setForeground(new java.awt.Color(255, 255, 255));
        ongoingOrderDeliveryTbl1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Vendor", "Order Date", "Order Time", "Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ongoingOrderDeliveryTbl1.setRowHeight(30);
        ongoingOrderDeliveryTbl1.setShowGrid(true);
        jScrollPane7.setViewportView(ongoingOrderDeliveryTbl1);

        javax.swing.GroupLayout jPanel47Layout = new javax.swing.GroupLayout(jPanel47);
        jPanel47.setLayout(jPanel47Layout);
        jPanel47Layout.setHorizontalGroup(
            jPanel47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel47Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane7)
                .addContainerGap())
        );
        jPanel47Layout.setVerticalGroup(
            jPanel47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel47Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(400, 400, 400)
                        .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 336, Short.MAX_VALUE)
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel47, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel47, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout homeContentPanelLayout = new javax.swing.GroupLayout(homeContentPanel);
        homeContentPanel.setLayout(homeContentPanelLayout);
        homeContentPanelLayout.setHorizontalGroup(
            homeContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(homeContentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        homeContentPanelLayout.setVerticalGroup(
            homeContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, homeContentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        homePanel.add(homeContentPanel, java.awt.BorderLayout.CENTER);

        contentPanel.add(homePanel, "homePanel");

        historyPanel.setLayout(new java.awt.BorderLayout());

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Past Orders", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14), new java.awt.Color(255, 255, 255))); // NOI18N
        jPanel8.setForeground(new java.awt.Color(255, 255, 255));

        orderHistoryTbl.setForeground(new java.awt.Color(255, 255, 255));
        orderHistoryTbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "OrderId", "Vendor", "Order Date", "Order Time", "Total Amount", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        orderHistoryTbl.setShowGrid(true);
        jScrollPane4.setViewportView(orderHistoryTbl);

        jPanel24.setLayout(new java.awt.BorderLayout());

        reorderBtn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        reorderBtn.setForeground(new java.awt.Color(255, 255, 255));
        reorderBtn.setText("Reorder");
        reorderBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        reorderBtn.setPreferredSize(new java.awt.Dimension(125, 31));
        reorderBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                reorderBtnMousePressed(evt);
            }
        });
        jPanel24.add(reorderBtn, java.awt.BorderLayout.LINE_END);

        viewPastOrderDetailsBtn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        viewPastOrderDetailsBtn.setForeground(new java.awt.Color(255, 255, 255));
        viewPastOrderDetailsBtn.setText(" View Details");
        viewPastOrderDetailsBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        viewPastOrderDetailsBtn.setPreferredSize(new java.awt.Dimension(125, 27));
        viewPastOrderDetailsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewPastOrderDetailsBtnActionPerformed(evt);
            }
        });
        jPanel24.add(viewPastOrderDetailsBtn, java.awt.BorderLayout.LINE_START);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(467, Short.MAX_VALUE)
                .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(549, 549, 549))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 633, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel14.setLayout(new java.awt.BorderLayout());

        orderHistoryBackBtn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        orderHistoryBackBtn.setForeground(new java.awt.Color(255, 255, 255));
        orderHistoryBackBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/back-icon.png"))); // NOI18N
        orderHistoryBackBtn.setText(" Order History");
        orderHistoryBackBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        orderHistoryBackBtn.setPreferredSize(new java.awt.Dimension(76, 31));
        orderHistoryBackBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                orderHistoryBackBtnMousePressed(evt);
            }
        });
        jPanel14.add(orderHistoryBackBtn, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout historyContentPanelLayout = new javax.swing.GroupLayout(historyContentPanel);
        historyContentPanel.setLayout(historyContentPanelLayout);
        historyContentPanelLayout.setHorizontalGroup(
            historyContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(historyContentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        historyContentPanelLayout.setVerticalGroup(
            historyContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(historyContentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        historyPanel.add(historyContentPanel, java.awt.BorderLayout.CENTER);

        contentPanel.add(historyPanel, "historyPanel");

        vendorsPanel.setLayout(new java.awt.BorderLayout());

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "APFood Cafeterias", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14), new java.awt.Color(255, 255, 255))); // NOI18N
        jPanel6.setForeground(new java.awt.Color(255, 255, 255));

        vendorsTable.setForeground(new java.awt.Color(255, 255, 255));
        vendorsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Cafeteria Name"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        vendorsTable.setRowHeight(40);
        vendorsTable.setShowGrid(true);
        jScrollPane1.setViewportView(vendorsTable);

        jPanel20.setLayout(new java.awt.BorderLayout(50, 0));

        viewReviewsBtnFromVendorPanel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        viewReviewsBtnFromVendorPanel.setForeground(new java.awt.Color(255, 255, 255));
        viewReviewsBtnFromVendorPanel.setText("View Reviews");
        viewReviewsBtnFromVendorPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        viewReviewsBtnFromVendorPanel.setPreferredSize(new java.awt.Dimension(150, 31));
        viewReviewsBtnFromVendorPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                viewReviewsBtnFromVendorPanelMousePressed(evt);
            }
        });
        jPanel20.add(viewReviewsBtnFromVendorPanel, java.awt.BorderLayout.LINE_START);

        browseMenuBtn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        browseMenuBtn.setForeground(new java.awt.Color(255, 255, 255));
        browseMenuBtn.setText("Browse Menu");
        browseMenuBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        browseMenuBtn.setPreferredSize(new java.awt.Dimension(140, 31));
        browseMenuBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                browseMenuBtnMousePressed(evt);
            }
        });
        jPanel20.add(browseMenuBtn, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1316, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(469, 469, 469)
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 706, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        vendorsPanel.add(jPanel5, java.awt.BorderLayout.CENTER);

        contentPanel.add(vendorsPanel, "vendorsPanel");

        vendorMenuPanel.setLayout(new java.awt.BorderLayout());

        jPanel25.setLayout(new java.awt.BorderLayout());

        jPanel26.setPreferredSize(new java.awt.Dimension(650, 820));

        jPanel28.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Vendor Menu", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14), new java.awt.Color(255, 255, 255))); // NOI18N

        vendorMenuTbl.setForeground(new java.awt.Color(255, 255, 255));
        vendorMenuTbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Menu Name", "Menu Type", "Price"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        vendorMenuTbl.setRowHeight(30);
        vendorMenuTbl.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        vendorMenuTbl.setShowGrid(true);
        vendorMenuTbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                vendorMenuTblMousePressed(evt);
            }
        });
        jScrollPane8.setViewportView(vendorMenuTbl);

        jPanel30.setLayout(new java.awt.BorderLayout());

        decreaseItemQtyBtn.setForeground(new java.awt.Color(255, 255, 255));
        decreaseItemQtyBtn.setText("-");
        decreaseItemQtyBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        decreaseItemQtyBtn.setEnabled(false);
        decreaseItemQtyBtn.setPreferredSize(new java.awt.Dimension(80, 27));
        decreaseItemQtyBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                decreaseItemQtyBtnActionPerformed(evt);
            }
        });
        jPanel30.add(decreaseItemQtyBtn, java.awt.BorderLayout.LINE_START);

        increaseItemQtyBtn.setForeground(new java.awt.Color(255, 255, 255));
        increaseItemQtyBtn.setText("+");
        increaseItemQtyBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        increaseItemQtyBtn.setPreferredSize(new java.awt.Dimension(80, 27));
        increaseItemQtyBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                increaseItemQtyBtnActionPerformed(evt);
            }
        });
        jPanel30.add(increaseItemQtyBtn, java.awt.BorderLayout.LINE_END);

        itemQtyLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        itemQtyLabel.setForeground(new java.awt.Color(255, 255, 255));
        itemQtyLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        itemQtyLabel.setText("0");
        itemQtyLabel.setEnabled(false);
        jPanel30.add(itemQtyLabel, java.awt.BorderLayout.CENTER);

        jPanel32.setLayout(new java.awt.BorderLayout(0, 10));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setText("Remarks");
        jPanel32.add(jLabel13, java.awt.BorderLayout.PAGE_START);

        itemRemarksTxtArea.setColumns(20);
        itemRemarksTxtArea.setForeground(new java.awt.Color(255, 255, 255));
        itemRemarksTxtArea.setRows(5);
        itemRemarksTxtArea.setEnabled(false);
        jScrollPane9.setViewportView(itemRemarksTxtArea);

        jPanel32.add(jScrollPane9, java.awt.BorderLayout.CENTER);

        addToCartBtn.setForeground(new java.awt.Color(255, 255, 255));
        addToCartBtn.setText("Add to Cart");
        addToCartBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        addToCartBtn.setEnabled(false);
        addToCartBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addToCartBtnActionPerformed(evt);
            }
        });

        updateCartBtn.setForeground(new java.awt.Color(255, 255, 255));
        updateCartBtn.setText("Update Cart");
        updateCartBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        updateCartBtn.setEnabled(false);
        updateCartBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateCartBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel31Layout = new javax.swing.GroupLayout(jPanel31);
        jPanel31.setLayout(jPanel31Layout);
        jPanel31Layout.setHorizontalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel32, javax.swing.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
            .addGroup(jPanel31Layout.createSequentialGroup()
                .addComponent(addToCartBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(updateCartBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel31Layout.setVerticalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel31Layout.createSequentialGroup()
                .addComponent(jPanel30, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addToCartBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(updateCartBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel7.setLayout(new java.awt.BorderLayout());

        jLabel35.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(255, 255, 255));
        jLabel35.setText("Search:");
        jLabel35.setPreferredSize(new java.awt.Dimension(60, 20));
        jPanel7.add(jLabel35, java.awt.BorderLayout.LINE_START);

        searchVendorMenuTxtField.setForeground(new java.awt.Color(255, 255, 255));
        searchVendorMenuTxtField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchVendorMenuTxtFieldKeyReleased(evt);
            }
        });
        jPanel7.add(searchVendorMenuTxtField, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout jPanel28Layout = new javax.swing.GroupLayout(jPanel28);
        jPanel28.setLayout(jPanel28Layout);
        jPanel28Layout.setHorizontalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
                    .addComponent(jPanel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel28Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel28Layout.setVerticalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel40.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel40.setLayout(new java.awt.BorderLayout());

        vendorNameLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        vendorNameLabel.setForeground(new java.awt.Color(255, 255, 255));
        vendorNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel40.add(vendorNameLabel, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel26Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jPanel40, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel25.add(jPanel26, java.awt.BorderLayout.LINE_START);

        jPanel27.setPreferredSize(new java.awt.Dimension(650, 820));

        orderSummaryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Order Summary", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14), new java.awt.Color(255, 255, 255))); // NOI18N

        orderCartTbl.setForeground(new java.awt.Color(255, 255, 255));
        orderCartTbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Menu Name", "Quantity", "Remarks"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        orderCartTbl.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        orderCartTbl.setShowGrid(true);
        orderCartTbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                orderCartTblMousePressed(evt);
            }
        });
        jScrollPane10.setViewportView(orderCartTbl);

        jPanel33.setLayout(new java.awt.GridLayout(3, 3, 20, 20));

        clearCartBtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        clearCartBtn.setForeground(new java.awt.Color(255, 255, 255));
        clearCartBtn.setText("Clear Cart");
        clearCartBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        clearCartBtn.setEnabled(false);
        clearCartBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearCartBtnActionPerformed(evt);
            }
        });
        jPanel33.add(clearCartBtn);

        placeOrderBtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        placeOrderBtn.setForeground(new java.awt.Color(255, 255, 255));
        placeOrderBtn.setText("Place Order");
        placeOrderBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        placeOrderBtn.setEnabled(false);
        placeOrderBtn.setFocusPainted(false);
        placeOrderBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                placeOrderBtnActionPerformed(evt);
            }
        });
        jPanel33.add(placeOrderBtn);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 323, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        jPanel33.add(jPanel1);

        javax.swing.GroupLayout jPanel37Layout = new javax.swing.GroupLayout(jPanel37);
        jPanel37.setLayout(jPanel37Layout);
        jPanel37Layout.setHorizontalGroup(
            jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 323, Short.MAX_VALUE)
        );
        jPanel37Layout.setVerticalGroup(
            jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        jPanel33.add(jPanel37);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 323, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        jPanel33.add(jPanel2);

        javax.swing.GroupLayout jPanel34Layout = new javax.swing.GroupLayout(jPanel34);
        jPanel34.setLayout(jPanel34Layout);
        jPanel34Layout.setHorizontalGroup(
            jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 323, Short.MAX_VALUE)
        );
        jPanel34Layout.setVerticalGroup(
            jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        jPanel33.add(jPanel34);

        costSummaryParentPanel.setBackground(new java.awt.Color(204, 204, 204));
        costSummaryParentPanel.setLayout(new java.awt.CardLayout());

        costSummaryWithDeliveryPanel.setBackground(new java.awt.Color(204, 204, 204));
        costSummaryWithDeliveryPanel.setLayout(new java.awt.GridLayout(3, 1));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 0, 0));
        jLabel15.setText("Subtotal");
        jLabel15.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1)));
        costSummaryWithDeliveryPanel.add(jLabel15);

        jLabel16.setForeground(new java.awt.Color(0, 0, 0));
        jLabel16.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1)));
        costSummaryWithDeliveryPanel.add(jLabel16);

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 0, 0));
        jLabel17.setText("Delivery Fee");
        jLabel17.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1)));
        costSummaryWithDeliveryPanel.add(jLabel17);

        jLabel18.setForeground(new java.awt.Color(0, 0, 0));
        jLabel18.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1)));
        costSummaryWithDeliveryPanel.add(jLabel18);

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(0, 0, 0));
        jLabel19.setText("Total");
        jLabel19.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1)));
        costSummaryWithDeliveryPanel.add(jLabel19);

        jLabel20.setForeground(new java.awt.Color(0, 0, 0));
        jLabel20.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1)));
        costSummaryWithDeliveryPanel.add(jLabel20);

        costSummaryParentPanel.add(costSummaryWithDeliveryPanel, "costSummaryWithDeliveryPanel");

        costSummaryPanel.setBackground(new java.awt.Color(204, 204, 204));
        costSummaryPanel.setLayout(new java.awt.GridLayout(2, 1));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setText("Subtotal");
        jLabel10.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1)));
        costSummaryPanel.add(jLabel10);

        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1)));
        costSummaryPanel.add(jLabel11);

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 0, 0));
        jLabel12.setText("Total");
        jLabel12.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1)));
        costSummaryPanel.add(jLabel12);

        jLabel14.setForeground(new java.awt.Color(0, 0, 0));
        jLabel14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        costSummaryPanel.add(jLabel14);

        costSummaryParentPanel.add(costSummaryPanel, "costSummaryPanel");

        costSummaryWithDiscountPanel.setBackground(new java.awt.Color(204, 204, 204));
        costSummaryWithDiscountPanel.setLayout(new java.awt.GridLayout(3, 1));

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(0, 0, 0));
        jLabel21.setText("Subtotal");
        jLabel21.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1)));
        costSummaryWithDiscountPanel.add(jLabel21);

        jLabel22.setForeground(new java.awt.Color(0, 0, 0));
        jLabel22.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1)));
        costSummaryWithDiscountPanel.add(jLabel22);

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(0, 0, 0));
        jLabel23.setText("Discount");
        jLabel23.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1)));
        costSummaryWithDiscountPanel.add(jLabel23);

        jLabel24.setForeground(new java.awt.Color(0, 0, 0));
        jLabel24.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1)));
        costSummaryWithDiscountPanel.add(jLabel24);

        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(0, 0, 0));
        jLabel25.setText("Total");
        jLabel25.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1)));
        costSummaryWithDiscountPanel.add(jLabel25);

        jLabel26.setForeground(new java.awt.Color(0, 0, 0));
        jLabel26.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1)));
        costSummaryWithDiscountPanel.add(jLabel26);

        costSummaryParentPanel.add(costSummaryWithDiscountPanel, "costSummaryWithDiscountPanel");

        costSummaryWithDeliveryAndDiscountPanel.setBackground(new java.awt.Color(204, 204, 204));
        costSummaryWithDeliveryAndDiscountPanel.setLayout(new java.awt.GridLayout(4, 1));

        jLabel27.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(0, 0, 0));
        jLabel27.setText("Subtotal");
        jLabel27.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1)));
        costSummaryWithDeliveryAndDiscountPanel.add(jLabel27);

        jLabel28.setForeground(new java.awt.Color(0, 0, 0));
        jLabel28.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1)));
        costSummaryWithDeliveryAndDiscountPanel.add(jLabel28);

        jLabel29.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(0, 0, 0));
        jLabel29.setText("Delivery Fee");
        jLabel29.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1)));
        costSummaryWithDeliveryAndDiscountPanel.add(jLabel29);

        jLabel30.setForeground(new java.awt.Color(0, 0, 0));
        jLabel30.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1)));
        costSummaryWithDeliveryAndDiscountPanel.add(jLabel30);

        jLabel31.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(0, 0, 0));
        jLabel31.setText("Discount");
        jLabel31.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1)));
        costSummaryWithDeliveryAndDiscountPanel.add(jLabel31);

        jLabel32.setForeground(new java.awt.Color(0, 0, 0));
        jLabel32.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1)));
        costSummaryWithDeliveryAndDiscountPanel.add(jLabel32);

        jLabel33.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(0, 0, 0));
        jLabel33.setText("Total");
        jLabel33.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1)));
        costSummaryWithDeliveryAndDiscountPanel.add(jLabel33);

        jLabel34.setForeground(new java.awt.Color(0, 0, 0));
        jLabel34.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)), javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1)));
        costSummaryWithDeliveryAndDiscountPanel.add(jLabel34);

        costSummaryParentPanel.add(costSummaryWithDeliveryAndDiscountPanel, "costSummaryWithDeliveryAndDiscountPanel");

        deliveryLocationsComboBox.setForeground(new java.awt.Color(255, 255, 255));
        deliveryLocationsComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Block A", "Block B", "Block D", "Block E" }));
        deliveryLocationsComboBox.setSelectedIndex(-1);
        deliveryLocationsComboBox.setEnabled(false);
        deliveryLocationsComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deliveryLocationsComboBoxActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Select Order Mode");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Select Delivery Location");

        orderModesComboBox.setForeground(new java.awt.Color(255, 255, 255));
        orderModesComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Delivery", "Pickup", "Dine-in" }));
        orderModesComboBox.setEnabled(false);
        orderModesComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orderModesComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout orderSummaryPanelLayout = new javax.swing.GroupLayout(orderSummaryPanel);
        orderSummaryPanel.setLayout(orderSummaryPanelLayout);
        orderSummaryPanelLayout.setHorizontalGroup(
            orderSummaryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(orderSummaryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(orderSummaryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane10)
                    .addGroup(orderSummaryPanelLayout.createSequentialGroup()
                        .addGroup(orderSummaryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(orderSummaryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(orderModesComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(deliveryLocationsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(costSummaryParentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        orderSummaryPanelLayout.setVerticalGroup(
            orderSummaryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(orderSummaryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(orderSummaryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(orderSummaryPanelLayout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(orderModesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deliveryLocationsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(costSummaryParentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
                .addComponent(jPanel33, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        viewReviewsFromMenuBtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        viewReviewsFromMenuBtn.setForeground(new java.awt.Color(255, 255, 255));
        viewReviewsFromMenuBtn.setText(">> See Reviews");
        viewReviewsFromMenuBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        viewReviewsFromMenuBtn.setFocusPainted(false);
        viewReviewsFromMenuBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                viewReviewsFromMenuBtnMousePressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(orderSummaryPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel27Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(viewReviewsFromMenuBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(250, 250, 250))
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(orderSummaryPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(viewReviewsFromMenuBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );

        jPanel25.add(jPanel27, java.awt.BorderLayout.CENTER);

        vendorMenuPanel.add(jPanel25, java.awt.BorderLayout.CENTER);

        contentPanel.add(vendorMenuPanel, "vendorMenuPanel");

        vendorReviewsPanel.setLayout(new java.awt.BorderLayout());

        jPanel45.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Reviews", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14), new java.awt.Color(255, 255, 255))); // NOI18N

        reviewsTbl.setForeground(new java.awt.Color(255, 255, 255));
        reviewsTbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Customers", "What They Say", "Ratings"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        reviewsTbl.setRowHeight(40);
        reviewsTbl.setShowGrid(true);
        jScrollPane6.setViewportView(reviewsTbl);

        javax.swing.GroupLayout jPanel45Layout = new javax.swing.GroupLayout(jPanel45);
        jPanel45.setLayout(jPanel45Layout);
        jPanel45Layout.setHorizontalGroup(
            jPanel45Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel45Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 1304, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel45Layout.setVerticalGroup(
            jPanel45Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel45Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 757, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel44Layout = new javax.swing.GroupLayout(jPanel44);
        jPanel44.setLayout(jPanel44Layout);
        jPanel44Layout.setHorizontalGroup(
            jPanel44Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel44Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel45, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel44Layout.setVerticalGroup(
            jPanel44Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel44Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel45, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel42Layout = new javax.swing.GroupLayout(jPanel42);
        jPanel42.setLayout(jPanel42Layout);
        jPanel42Layout.setHorizontalGroup(
            jPanel42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel42Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel44, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel42Layout.setVerticalGroup(
            jPanel42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel42Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel44, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        vendorReviewsPanel.add(jPanel42, java.awt.BorderLayout.CENTER);

        contentPanel.add(vendorReviewsPanel, "vendorReviewsPanel");

        financePanel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        financePanel.setLayout(new java.awt.BorderLayout());

        topUpPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true), null));
        topUpPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        topUpPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                topUpPanelMousePressed(evt);
            }
        });
        topUpPanel.setLayout(new java.awt.BorderLayout(-20, 0));

        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/coin-wallet.png"))); // NOI18N
        jLabel36.setPreferredSize(new java.awt.Dimension(60, 24));
        topUpPanel.add(jLabel36, java.awt.BorderLayout.LINE_START);

        jLabel37.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(255, 255, 255));
        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setText("Top Up");
        topUpPanel.add(jLabel37, java.awt.BorderLayout.CENTER);

        jPanel36.setLayout(new java.awt.BorderLayout());

        jLabel45.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel45.setForeground(new java.awt.Color(255, 255, 255));
        jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel45.setText("Balance:");
        jLabel45.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jLabel45.setName(""); // NOI18N
        jLabel45.setPreferredSize(new java.awt.Dimension(120, 20));
        jPanel36.add(jLabel45, java.awt.BorderLayout.LINE_START);

        userCreditBalanceLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        userCreditBalanceLabel2.setForeground(new java.awt.Color(255, 255, 255));
        userCreditBalanceLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        userCreditBalanceLabel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel36.add(userCreditBalanceLabel2, java.awt.BorderLayout.CENTER);

        transactionsTbl.setForeground(new java.awt.Color(255, 255, 255));
        transactionsTbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Amount", "Transaction Date", "Transaction Time", "Remarks"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Double.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        transactionsTbl.setRowHeight(30);
        transactionsTbl.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        transactionsTbl.setShowGrid(true);
        jScrollPane5.setViewportView(transactionsTbl);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 1326, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 713, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jPanel36, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(topUpPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(topUpPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(jPanel36, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        financePanel.add(jPanel9, java.awt.BorderLayout.CENTER);

        contentPanel.add(financePanel, "financePanel");

        notificationsPanel.setLayout(new java.awt.BorderLayout());

        jPanel16.setPreferredSize(new java.awt.Dimension(1349, 780));

        notificationsTbl.setForeground(new java.awt.Color(255, 255, 255));
        notificationsTbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Notification ID", "Name", "Content", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        notificationsTbl.setRowHeight(30);
        notificationsTbl.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        notificationsTbl.setShowGrid(true);
        notificationsTbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                notificationsTblMousePressed(evt);
            }
        });
        jScrollPane3.setViewportView(notificationsTbl);

        jPanel17.setLayout(new java.awt.BorderLayout());

        markAllAsReadBtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        markAllAsReadBtn.setForeground(new java.awt.Color(255, 255, 255));
        markAllAsReadBtn.setText("Mark All as Read");
        markAllAsReadBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        markAllAsReadBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                markAllAsReadBtnActionPerformed(evt);
            }
        });
        jPanel17.add(markAllAsReadBtn, java.awt.BorderLayout.CENTER);

        jPanel18.setLayout(new java.awt.BorderLayout());

        jLabel41.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(255, 255, 255));
        jLabel41.setText("Search:");
        jLabel41.setPreferredSize(new java.awt.Dimension(60, 20));
        jPanel18.add(jLabel41, java.awt.BorderLayout.LINE_START);

        searchNotificationTxtField.setForeground(new java.awt.Color(255, 255, 255));
        searchNotificationTxtField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchNotificationTxtFieldKeyReleased(evt);
            }
        });
        jPanel18.add(searchNotificationTxtField, java.awt.BorderLayout.CENTER);

        jPanel19.setPreferredSize(new java.awt.Dimension(44, 19));
        jPanel19.setLayout(new java.awt.BorderLayout());

        refreshNotificationBtn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        refreshNotificationBtn.setForeground(new java.awt.Color(255, 255, 255));
        refreshNotificationBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/refresh-icon.png"))); // NOI18N
        refreshNotificationBtn.setText(" Refresh");
        refreshNotificationBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel19.add(refreshNotificationBtn, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1326, Short.MAX_VALUE)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(600, 600, 600))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, 1338, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        notificationsPanel.add(jPanel15, java.awt.BorderLayout.CENTER);

        contentPanel.add(notificationsPanel, "notificationsPanel");

        subscriptionsPanel.setLayout(new java.awt.BorderLayout());

        jPanel29.setBackground(new java.awt.Color(41, 173, 178));

        jPanel38.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Current Plan", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14), new java.awt.Color(255, 255, 255))); // NOI18N
        jPanel38.setPreferredSize(new java.awt.Dimension(600, 796));

        jPanel41.setBackground(new java.awt.Color(255, 255, 255));
        jPanel41.setLayout(new java.awt.BorderLayout());

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Subscription Status:");
        jLabel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel7.setPreferredSize(new java.awt.Dimension(160, 16));
        jPanel41.add(jLabel7, java.awt.BorderLayout.LINE_START);

        subscriptionStatusLabel.setForeground(new java.awt.Color(0, 0, 0));
        subscriptionStatusLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        subscriptionStatusLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel41.add(subscriptionStatusLabel, java.awt.BorderLayout.CENTER);

        subscriptionValidityPanel.setBackground(new java.awt.Color(227, 161, 119));
        subscriptionValidityPanel.setLayout(new java.awt.BorderLayout());

        jLabel39.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(0, 0, 0));
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel39.setText("Valid Until:");
        jLabel39.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jLabel39.setPreferredSize(new java.awt.Dimension(160, 16));
        subscriptionValidityPanel.add(jLabel39, java.awt.BorderLayout.LINE_START);

        subscriptionValidityLabel.setForeground(new java.awt.Color(0, 0, 0));
        subscriptionValidityLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        subscriptionValidityLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        subscriptionValidityPanel.add(subscriptionValidityLabel, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout jPanel38Layout = new javax.swing.GroupLayout(jPanel38);
        jPanel38.setLayout(jPanel38Layout);
        jPanel38Layout.setHorizontalGroup(
            jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel38Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel41, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(subscriptionValidityPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(270, Short.MAX_VALUE))
        );
        jPanel38Layout.setVerticalGroup(
            jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel38Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jPanel41, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(subscriptionValidityPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel39.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Subscription", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14), new java.awt.Color(255, 255, 255))); // NOI18N

        jPanel35.setBackground(new java.awt.Color(0, 102, 102));
        jPanel35.setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Get 10% Discount on ALL ORDERS when subscribed ! Min. Spend Applies");
        jLabel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jLabel1.setPreferredSize(new java.awt.Dimension(339, 40));
        jPanel35.add(jLabel1, java.awt.BorderLayout.PAGE_START);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("ONLY RM 4.00 / month");
        jLabel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel35.add(jLabel6, java.awt.BorderLayout.CENTER);

        jPanel43.setLayout(new java.awt.BorderLayout());

        subscribeBtn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        subscribeBtn.setForeground(new java.awt.Color(255, 255, 255));
        subscribeBtn.setText("Subscribe");
        subscribeBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        subscribeBtn.setFocusPainted(false);
        subscribeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subscribeBtnActionPerformed(evt);
            }
        });
        jPanel43.add(subscribeBtn, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout jPanel39Layout = new javax.swing.GroupLayout(jPanel39);
        jPanel39.setLayout(jPanel39Layout);
        jPanel39Layout.setHorizontalGroup(
            jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel39Layout.createSequentialGroup()
                .addContainerGap(79, Short.MAX_VALUE)
                .addGroup(jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel39Layout.createSequentialGroup()
                        .addComponent(jPanel43, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(255, 255, 255))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel39Layout.createSequentialGroup()
                        .addComponent(jPanel35, javax.swing.GroupLayout.PREFERRED_SIZE, 550, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(75, 75, 75))))
        );
        jPanel39Layout.setVerticalGroup(
            jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel39Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jPanel35, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(jPanel43, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(583, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel29Layout = new javax.swing.GroupLayout(jPanel29);
        jPanel29.setLayout(jPanel29Layout);
        jPanel29Layout.setHorizontalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel38, javax.swing.GroupLayout.PREFERRED_SIZE, 606, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel29Layout.setVerticalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        subscriptionsPanel.add(jPanel4, java.awt.BorderLayout.CENTER);

        contentPanel.add(subscriptionsPanel, "subscriptionsPanel");

        mainPanel.add(contentPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void homeSidebarBtnMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeSidebarBtnMousePressed
        guiHelper.panelSwitcher(homeSidebarBtn, contentPanel, "homePanel");
        setTitle("Home - APFood");
        
        updateCreditBalanceDisplay();
    }//GEN-LAST:event_homeSidebarBtnMousePressed

    private void cafeteriaSidebarBtnMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cafeteriaSidebarBtnMousePressed
        guiHelper.panelSwitcher(cafeteriaSidebarBtn, contentPanel, "vendorsPanel");
        setTitle("All Vendors - APFood");
    }//GEN-LAST:event_cafeteriaSidebarBtnMousePressed

    private void decreaseItemQtyBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_decreaseItemQtyBtnActionPerformed
        int selectedCartIndex = orderCartTbl.getSelectedRow();
        int selectedMenuIndex = vendorMenuTbl.getSelectedRow();

        // Check if a row is selected in the vendorMenuTbl and not in the cart
        if (selectedMenuIndex != -1 && selectedCartIndex == -1) {
            if (menuItemQuantity > 1) {
                menuItemQuantity--;
            } else {
                menuItemQuantity = 1; // Prevent it from going below 1
            }
            itemQtyLabel.setText(String.valueOf(menuItemQuantity));
        }
        // Check if a row is selected in the orderCartTbl
        else if (selectedCartIndex != -1) {
            if (menuItemQuantity > 1) {
                menuItemQuantity--;
                updateCartBtn.setEnabled(true); // Keep the Update Cart button enabled
            } else {
                // If quantity reaches 1, prompt the user before removing the item
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Do you want to remove this item from the cart?",
                        "Remove Item", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    cartItems.remove(selectedCartIndex);
                    updateCartUIElements();
                    updateCartBtn.setEnabled(false);
                }
            }
            itemQtyLabel.setText(String.valueOf(menuItemQuantity));
            updateOrderCartTable();

            // Reselect the previously selected row if it still exists
            if (selectedCartIndex < orderCartTbl.getRowCount()) {
                orderCartTbl.setRowSelectionInterval(selectedCartIndex, selectedCartIndex);
            } else {
                // Clear selection if the row no longer exists
                orderCartTbl.clearSelection();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                                          "Please select an item to adjust its quantity.",
                                          "No Selection",
                                          JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_decreaseItemQtyBtnActionPerformed

    private void increaseItemQtyBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_increaseItemQtyBtnActionPerformed
        // Check if a row is selected in vendorMenuTbl for adding a new item to the cart
        if (vendorMenuTbl.getSelectedRow() != -1) {
            menuItemQuantity++;
            updateVendorMenuUIElements();
        } 
        // Check if a row is selected in orderCartTbl for adjusting the quantity of an existing item
        else if (orderCartTbl.getSelectedRow() != -1) {
            menuItemQuantity++;
            itemQtyLabel.setText(String.valueOf(menuItemQuantity));
            updateCartBtn.setEnabled(true); // Enable the Update Cart button
        } else {
            // No row is selected in either table, show a warning message
            JOptionPane.showMessageDialog(this,
                                          "Please select an item before increasing the quantity.",
                                          "No Selection",
                                          JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_increaseItemQtyBtnActionPerformed
    
    private Map<String, Double> createTablePriceMap() {
        return tableHelper.createTableMap(vendorMenuTbl, 1, 3, 
                                              Object::toString, 
                                              value -> Double.valueOf(value.toString()));
    }
    
    private double[] getTotalOrderCostDetails(String selectedOrderMode) {
        double subtotal = 0.0;
        double deliveryFee = 0.0;
        Map<String, Double> priceMap = createTablePriceMap();

        for (Object[] cartItem : cartItems) {
            String itemName = (String) cartItem[1];
            int quantity = (Integer) cartItem[2];
            double price = priceMap.getOrDefault(itemName, 0.0);
            subtotal += price * quantity;
        }

        if ("Delivery".equals(selectedOrderMode)) {
            String deliveryLocation = (String) deliveryLocationsComboBox.getSelectedItem();
            deliveryFee = OrderService.getDeliveryFee(deliveryLocation);
        }

        double discountAmount = orderService.calculateDiscountAmount(subtotal, loggedInUser.getId());
        double grandTotal = subtotal - discountAmount + deliveryFee;

        return new double[] { subtotal, discountAmount, deliveryFee, grandTotal };
    }
    
    private void updateCostSummary() {
        String selectedOrderMode = (String) orderModesComboBox.getSelectedItem();
        double[] costDetails = getTotalOrderCostDetails(selectedOrderMode);
        double subtotal = costDetails[0];
        double discountAmount = costDetails[1];
        double deliveryFee = costDetails[2];
        double totalWithDiscount = costDetails[3];

        if (isUserSubscribed && "Delivery".equals(selectedOrderMode)) {
            guiHelper.panelSwitcher(costSummaryParentPanel, "costSummaryWithDeliveryAndDiscountPanel");
            jLabel28.setText(String.format("RM %.2f", subtotal));
            jLabel30.setText(String.format("RM %.2f", deliveryFee));
            jLabel32.setText(String.format("RM %.2f", discountAmount));
            jLabel34.setText(String.format("RM %.2f", totalWithDiscount));
        } else if (isUserSubscribed) {
            guiHelper.panelSwitcher(costSummaryParentPanel, "costSummaryWithDiscountPanel");
            jLabel22.setText(String.format("RM %.2f", subtotal));
            jLabel24.setText(String.format("RM %.2f", discountAmount));
            jLabel26.setText(String.format("RM %.2f", totalWithDiscount));
        } else if ("Delivery".equals(selectedOrderMode)) {
            guiHelper.panelSwitcher(costSummaryParentPanel, "costSummaryWithDeliveryPanel");
            jLabel16.setText(String.format("RM %.2f", subtotal));
            jLabel18.setText(String.format("RM %.2f", deliveryFee));
            jLabel20.setText(String.format("RM %.2f", subtotal + deliveryFee));
        } else {
            guiHelper.panelSwitcher(costSummaryParentPanel, "costSummaryPanel");
            jLabel11.setText(String.format("RM %.2f", subtotal));
            jLabel14.setText(String.format("RM %.2f", subtotal));
        }
    }
    
    private void addToCartBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addToCartBtnActionPerformed
        int selectedRowIndex = vendorMenuTbl.getSelectedRow();
        int selectedCartIndex = orderCartTbl.getSelectedRow();

        if (selectedRowIndex != -1) {
            String itemName = vendorMenuTbl.getValueAt(selectedRowIndex, 1).toString();
            Integer menuId = menuItemIdMap.get(itemName);
            String remarks = itemRemarksTxtArea.getText();

            // Check if item already exists in the cart
            for (int i = 0; i < cartItems.size(); i++) {
                Object[] cartItem = cartItems.get(i);
                
                if (cartItem[1].equals(itemName)) {
                    // Update the quantity of the existing item
                    int currentQuantity = (int) cartItem[2];
                    cartItem[2] = (selectedCartIndex == i) ? menuItemQuantity : currentQuantity + menuItemQuantity;
                    updateOrderCartTable();
                    updateCartUIElements();
                    return;
                }
            }

            // Add new item to the cart if not found
            cartItems.add(new Object[] { menuId, itemName, menuItemQuantity, remarks });
            updateOrderCartTable();
            updateCartUIElements();
            
            itemRemarksTxtArea.setText("");
            
            updateCostSummary();
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to add to the cart.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_addToCartBtnActionPerformed

    private void browseMenuBtnMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_browseMenuBtnMousePressed
        int selectedRow = vendorsTable.getSelectedRow();
        if (selectedRow >= 0) {

            selectedVendorName = vendorsTable.getValueAt(selectedRow, 1).toString();
            orderDao.updateFilePath(selectedVendorName);

            // Load the vendor's menu
            loadVendorMenu(selectedVendorName);

            // Switch to the vendor's menu panel using panelSwitcher method
            guiHelper.panelSwitcher(browseMenuBtn, contentPanel, "vendorMenuPanel");
            vendorNameLabel.setText("Welcome to " + selectedVendorName);
            setTitle(selectedVendorName + " Menu - APFood");

            if (isUserSubscribed) {
                String defaultOrderMode = (String) orderModesComboBox.getSelectedItem();
                if ("Delivery".equals(defaultOrderMode)) {
                    guiHelper.panelSwitcher(costSummaryParentPanel, "costSummaryWithDeliveryAndDiscountPanel");
                } else {
                    guiHelper.panelSwitcher(costSummaryParentPanel, "costSummaryWithDiscountPanel");
                }
            } else {
                guiHelper.panelSwitcher(costSummaryParentPanel, "costSummaryPanel");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a vendor to view their menu.");
        }
    }//GEN-LAST:event_browseMenuBtnMousePressed

    private void vendorMenuTblMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_vendorMenuTblMousePressed
        
        orderCartTbl.clearSelection();

        // When a new row is selected, set the quantity to 1
        menuItemQuantity = 1;
        updateVendorMenuUIElements();
    }//GEN-LAST:event_vendorMenuTblMousePressed

    private void orderCartTblMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_orderCartTblMousePressed
        
        vendorMenuTbl.clearSelection();
            
        int selectedRow = orderCartTbl.getSelectedRow();

        if (selectedRow != -1) {

            // Get the selected item's quantity
            menuItemQuantity = (int) orderCartTbl.getValueAt(selectedRow, 1);
            itemQtyLabel.setText(String.valueOf(menuItemQuantity));

            // Get the selected item's remarks
            String menuItemRemarks = (String) orderCartTbl.getValueAt(selectedRow, 2);
            itemRemarksTxtArea.setText(menuItemRemarks);
            
            // Disable addToCartBtn when a row is selected
            addToCartBtn.setEnabled(false);
            
            updateCartBtn.setEnabled(true);
        } else {
            // Enable addToCartBtn when no row is selected
            addToCartBtn.setEnabled(true);
        }
    }//GEN-LAST:event_orderCartTblMousePressed

    private void updateCartBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateCartBtnActionPerformed
        int selectedCartIndex = orderCartTbl.getSelectedRow();
        if (selectedCartIndex != -1) {
            Object[] cartItem = cartItems.get(selectedCartIndex);
            cartItem[2] = menuItemQuantity;
            String updatedRemarks = itemRemarksTxtArea.getText();
            cartItem[3] = updatedRemarks;

            cartItems.set(selectedCartIndex, cartItem); // Update the cartItems list

            updateOrderCartTable();
            updateCartUIElements();
            itemQtyLabel.setText("1");
            itemRemarksTxtArea.setText("");
            menuItemQuantity = 1;
        } else {
            JOptionPane.showMessageDialog(this,
                                          "Please select an item in the cart to update.",
                                          "No Selection",
                                          JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_updateCartBtnActionPerformed

    private void clearCartBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearCartBtnActionPerformed
        // Clear the cartItems list
        cartItems.clear();

        // Update the order cart table to reflect the empty cart
        updateOrderCartTable();

        // Update the UI elements
        updateCartUIElements();

        // Clear any selected rows in the order cart table
        orderCartTbl.clearSelection();

        // Reset the menu item quantity and update the vendor menu UI elements
        menuItemQuantity = 0;
        updateVendorMenuUIElements();

        // Clear the item remarks text area
        itemRemarksTxtArea.setText("");

        // Disable buttons that should not be active when the cart is empty
        updateCartBtn.setEnabled(false);
    }//GEN-LAST:event_clearCartBtnActionPerformed
    
    private void updatePlaceOrderButtonState() {
        String selectedOrderMode = (String) orderModesComboBox.getSelectedItem();
        boolean canPlaceOrder;

        if ("Delivery".equals(selectedOrderMode)) {
            String deliveryLocation = (String) deliveryLocationsComboBox.getSelectedItem();
            canPlaceOrder = deliveryLocation != null && !deliveryLocation.isEmpty();
        } else {
            // Enable the Place Order button for Pickup or Dine-in
            canPlaceOrder = true;
        }

        placeOrderBtn.setEnabled(canPlaceOrder);
    }
    
    private void placeOrderBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_placeOrderBtnActionPerformed

        String selectedOrderMode = (String) orderModesComboBox.getSelectedItem();
        String deliveryLocation = (String) deliveryLocationsComboBox.getSelectedItem();

        if (selectedOrderMode == null || selectedOrderMode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an order mode.", "Order Mode Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if ("Delivery".equals(selectedOrderMode) && (deliveryLocation == null || deliveryLocation.isEmpty())) {
            JOptionPane.showMessageDialog(this, "Please select a delivery location.", "Delivery Location Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Order> ordersToAdd = new ArrayList<>();

        // Iterate over each item in the cart and create an order for each
        for (Object[] cartItem : cartItems) {
            int menuId = (Integer) cartItem[0]; // Retrieve menuId

            int quantity = (Integer) cartItem[2];
            String remarks = (String) cartItem[3];

            Order newOrder = new Order(loggedInUser.getId(), menuId, quantity, remarks, selectedOrderMode, deliveryLocation);
            ordersToAdd.add(newOrder);
        }
        
        double totalOrderCost = getTotalOrderCostDetails(selectedOrderMode)[3];

        if (!transactionService.hasSufficientBalance(loggedInUser.getId(), totalOrderCost)) {
            JOptionPane.showMessageDialog(this, "Insufficient credit balance to place order.", "Insufficient Balance", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Add all orders using OrderService and pass the selected vendor name
        orderService.addOrders(ordersToAdd, selectedVendorName, loggedInUser.getId());
        
        transactionService.addTransaction(loggedInUser.getId(), -totalOrderCost, "Food");

        // Clear the cart and update UI
        cartItems.clear();
        updateOrderCartTable();
        updateCartUIElements();    
        JOptionPane.showMessageDialog(this, "Order Placed Successfully!");
    }//GEN-LAST:event_placeOrderBtnActionPerformed

    private void orderModesComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orderModesComboBoxActionPerformed
        
        String selectedOrderMode = (String) orderModesComboBox.getSelectedItem();

        boolean isPickup = "Pickup".equals(selectedOrderMode);
        boolean isDineIn = "Dine-in".equals(selectedOrderMode);

        checkAndEnableDeliveryLocations();

        if (isPickup || isDineIn) {
            if (isUserSubscribed) {
                guiHelper.panelSwitcher(costSummaryParentPanel, "costSummaryWithDiscountPanel");
            } else {
                guiHelper.panelSwitcher(costSummaryParentPanel, "costSummaryPanel");
            }
        } else if ("Delivery".equals(selectedOrderMode)) {
            if (isUserSubscribed) {
                guiHelper.panelSwitcher(costSummaryParentPanel, "costSummaryWithDeliveryAndDiscountPanel");
            } else {
                guiHelper.panelSwitcher(costSummaryParentPanel, "costSummaryWithDeliveryPanel");
            }
        }

        updatePlaceOrderButtonState();
        updateCostSummary();
    }//GEN-LAST:event_orderModesComboBoxActionPerformed

    private void deliveryLocationsComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deliveryLocationsComboBoxActionPerformed

        updateCostSummary();
        updatePlaceOrderButtonState();
    }//GEN-LAST:event_deliveryLocationsComboBoxActionPerformed

    private void financeSidebarBtnMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_financeSidebarBtnMousePressed
        guiHelper.panelSwitcher(financeSidebarBtn, contentPanel, "financePanel");
        setTitle("Finance - APFood");
        
        List<Transaction> transactions = transactionService.getTransactions();

        // Prepare table data
        List<Object[]> tableData = transactions.stream()
            .filter(transaction -> transaction.getUserId() == loggedInUser.getId())
            .map(transaction -> new Object[]{
                String.format("RM %.2f", transaction.getAmount()),
                transaction.getTransactionOn(),
                transaction.getTransactionAt().format(DateTimeFormatter.ofPattern("HH:mm a")),
                transaction.getRemarks(),
                transaction.getRemarks().equals("Top up") ? "Top Up Receipt Available to View" : ""
            })
            .collect(Collectors.toList());

        Function<Object[], Object[]> rowMapper = row -> row;
        
        tableHelper.populateTable(tableData, transactionsTbl, rowMapper);
        tableHelper.centerTableValues(transactionsTbl);
        
        updateCreditBalanceDisplay();
    }//GEN-LAST:event_financeSidebarBtnMousePressed
    
    private void updateNotificationsTable(List<Object[]> tableData) {
        Function<Object[], Object[]> rowMapper = row -> row;
        tableHelper.populateTable(tableData, notificationsTbl, rowMapper);

        TableColumnModel columnModel = notificationsTbl.getColumnModel();
        columnModel.getColumn(0).setMinWidth(0);
        columnModel.getColumn(0).setMaxWidth(0);
        columnModel.getColumn(0).setWidth(0);

        List<Integer> centeredColumns = Arrays.asList(1, 3, 4);
        
        // Custom cell rendering with softer colors, adjusted foreground color, and center alignment
        for (int i = 0; i < notificationsTbl.getColumnCount(); i++) {
            TableColumn column = notificationsTbl.getColumnModel().getColumn(i);
            column.setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                                                               boolean isSelected, boolean hasFocus, 
                                                               int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                    // Center align if it's in the specified columns
                    if (centeredColumns.contains(column)) {
                        setHorizontalAlignment(SwingConstants.CENTER);
                    }

                    Notification notification = notificationService.getNotificationById((Integer) table.getModel().getValueAt(row, 0));
                    if (notification.getNotificationStatus() == NotificationStatus.UNNOTIFIED) {
                        c.setForeground(Color.BLACK); // Set foreground color to black for better readability
                        switch (notification.getNotificationType()) {
                            case PUSH -> c.setBackground(new Color(255, 153, 153)); // Softer red
                            case TRANSACTIONAL, INFORMATIONAL -> c.setBackground(new Color(255, 255, 153)); // Softer yellow
                            default -> c.setBackground(table.getBackground());
                        }
                    } else {
                        c.setBackground(table.getBackground()); // Default color
                        c.setForeground(table.getForeground()); // Revert to default foreground color
                    }
                    return c;
                }
            });
        }
    }
    
    private boolean isOrderAccepted(Notification notification) {
        String content = notification.getContent();
        return content.toLowerCase().contains("order has been accepted");
    }
    
    private String getNotificationActionText(Notification notification) {
        if (notification.getNotificationType() == NotificationType.PUSH) {
            return notification.getNotificationStatus() == NotificationStatus.NOTIFIED ? "" : "Action Required";
        } else if (isOrderAccepted(notification)) {
            boolean reviewExists = reviewService.hasReviewForOrder(Integer.parseInt(notification.getOrderId()), notification.getVendorName());
            return reviewExists ? "View Receipt" : "View Receipt / Give Review";
        } else if (notification.getContent().toLowerCase().contains("delivery completed")) {
            boolean feedbackGiven = runnerTaskDao.hasFeedbackForOrder(Integer.parseInt(notification.getOrderId()), notification.getVendorName());       
            return feedbackGiven ? "Feedback Given" : "Give Feedback";
        } else if (notification.getContent().toLowerCase().contains("credit top up")) {
            return "Top Up Receipt Available to View";
        }
        return "";
    }
    

    private String getUserFriendlyNotificationContent(String content) {
        content = content.replaceAll("\\[order id: \\d+, vendor name: [^\\]]+\\]", "");
        content = content.replaceAll("\\[user id: \\d+, transaction id: \\d+\\]", "");
        return content;
    }

    private void displayNotifications() {
        List<Notification> notifications = notificationService.getNotifications()
                                                               .stream()
                                                               .filter(notification -> notification.getUserId() == loggedInUser.getId())
                                                               .collect(Collectors.toList());
        List<Object[]> tableData = new ArrayList<>();

        for (Notification notification : notifications) {
            String senderName = determineSenderName(notification);
            String userFriendlyContent = getUserFriendlyNotificationContent(notification.getContent());
            String actionText = getNotificationActionText(notification);

            tableData.add(new Object[] { notification.getId(), senderName, userFriendlyContent, actionText });
        }

        updateNotificationsTable(tableData);
    }
    
    private String determineSenderName(Notification notification) {
        
        Pattern vendorOrderAcceptancePattern = Pattern.compile("order has been (accepted|declined)", Pattern.CASE_INSENSITIVE);
        
        switch (notification.getNotificationType()) {
            case PUSH -> {
                return "APFood";
            }
            case TRANSACTIONAL -> {
                if (notification.getContent().toLowerCase().contains("order has been placed")) {
                    selectedVendorName = notification.getVendorName();
                    return selectedVendorName.isEmpty() ? "APFood" : "APFood - " + selectedVendorName;
                } else if (notification.getContent().toLowerCase().contains("credit top up")) {
                    return "Administrator";
                }
            }
            case INFORMATIONAL -> {
                if (vendorOrderAcceptancePattern.matcher(notification.getContent()).find()) {
                    return notification.getVendorName();
                } else if (notification.getContent().toLowerCase().contains("delivery")) {
                    return getRunnerNameFromContent(notification);
                }
            }
        }
        return "Unknown";
    }
    
    private String getRunnerNameFromContent(Notification notification) {
        String orderId = notification.getOrderId();
        int runnerId = runnerTaskDao.getRunnerIdByOrderId(orderId);
        User runner = userDao.getUserById(runnerId);
        return runner != null ? runner.getName() : "Unknown Runner";
    }
    
    private void notificationsSidebarBtnMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_notificationsSidebarBtnMousePressed

        guiHelper.panelSwitcher(notificationsSidebarBtn, contentPanel, "notificationsPanel");
        setTitle("Notifications - APFood");

        displayNotifications();
    }//GEN-LAST:event_notificationsSidebarBtnMousePressed

    private void historyBtnMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_historyBtnMousePressed
        guiHelper.panelSwitcher(historyBtn, contentPanel, "historyPanel");
        setTitle("History - APFood");
        
        int userId = 1; // User ID for demonstration
        List<OrderStatus> statuses = Arrays.asList(OrderStatus.READY, OrderStatus.DECLINED, OrderStatus.CANCELLED);

        Map<String, List<Order>> groupedOrders = orderService.getUserOrdersGrouped(userId, statuses);
        List<Object[]> tableData = new ArrayList<>();

        for (Map.Entry<String, List<Order>> entry : groupedOrders.entrySet()) {
            List<Order> orders = entry.getValue();
            if (!orders.isEmpty()) {
                Order representativeOrder = orders.get(0); // // A representative order for common details
                double totalAmount = orderService.calculateTotalAmountForGroupedOrders(orders, representativeOrder.getVendorName(), userId);
                Object[] row = formatOrderTable(representativeOrder, totalAmount);
                tableData.add(row);
            }
        }

        updateOrderHistoryTable(tableData);
    }//GEN-LAST:event_historyBtnMousePressed

    private Object[] formatOrderTable(Order order, double totalAmount) {        

        return new Object[]{
            order.getOrderId(),
            order.getVendorName(),
            order.getOrderDate().toString(), 
            order.getOrderTime().format(DateTimeFormatter.ofPattern("HH:mm")),
            String.format("RM %.2f", totalAmount),
            order.getOrderStatus().toString(),
        };
    }

    private void updateOrderHistoryTable(List<Object[]> tableData) {
        Function<Object[], Object[]> rowMapper = row -> row;

        tableHelper.populateTable(tableData, orderHistoryTbl, rowMapper);
        tableHelper.centerTableValues(orderHistoryTbl);

        TableColumnModel columnModel = orderHistoryTbl.getColumnModel();
        if (columnModel.getColumnCount() > 5) { // Check if the orderId column is still present
            columnModel.removeColumn(columnModel.getColumn(0));
        }
    }
    
    private void orderHistoryBackBtnMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_orderHistoryBackBtnMousePressed
        guiHelper.panelSwitcher(orderHistoryBackBtn, contentPanel, "homePanel");
        setTitle("Home - APFood");
    }//GEN-LAST:event_orderHistoryBackBtnMousePressed

    private void showOrderDetailsPopup(List<Order> orderDetails, String vendorName) {
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Order order : orderDetails) {
            Menu menuItem = vendorService.getVendorMenuItems(vendorName).stream()
                                         .filter(menu -> menu.getId() == order.getMenuId())
                                         .findFirst()
                                         .orElse(new Menu(0, "Unknown", "Unknown", 0.0));
            String itemDetail = menuItem.getMenuName() + " - Qty: " + order.getQuantity();
            if (!order.getRemarks().isEmpty()) {
                itemDetail += " (Remarks: " + order.getRemarks() + ")";
            }
            listModel.addElement(itemDetail);
        }


        JList<String> orderItemList = new JList<>(listModel);

        orderItemList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Padding for each cell
                if (isSelected) {
                    setBackground(Color.LIGHT_GRAY); // Background for selected item
                    setForeground(Color.BLACK);
                } else {
                    setBackground(Color.WHITE); // Background for non-selected items
                    setForeground(Color.BLACK);
                }
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(orderItemList);
        scrollPane.setPreferredSize(new Dimension(350, 200)); // Set preferred size

        JOptionPane.showMessageDialog(this, scrollPane, "Order Details for " + vendorName, JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void viewPastOrderDetailsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewPastOrderDetailsBtnActionPerformed

        int selectedRow = orderHistoryTbl.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to view details.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int orderId = (Integer) orderHistoryTbl.getModel().getValueAt(selectedRow, 0);
        
        String vendorName = (String) orderHistoryTbl.getValueAt(selectedRow, 0);
        LocalDate orderDate = LocalDate.parse((String) orderHistoryTbl.getValueAt(selectedRow, 1));

        // Parsing the time string with the appropriate format
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm a");
        LocalTime orderTime = LocalTime.parse((String) orderHistoryTbl.getValueAt(selectedRow, 2), timeFormatter);
        
        List<Order> orderDetails = orderService.getOrderDetails(orderId, vendorName, orderDate, orderTime);
        showOrderDetailsPopup(orderDetails, vendorName);
    }//GEN-LAST:event_viewPastOrderDetailsBtnActionPerformed

    private void reorderBtnMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reorderBtnMousePressed

        int selectedRow = orderHistoryTbl.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to reorder.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderId = (Integer) orderHistoryTbl.getModel().getValueAt(selectedRow, 0);

        selectedVendorName = (String) orderHistoryTbl.getValueAt(selectedRow, 0);
        LocalDate orderDate = LocalDate.parse((String) orderHistoryTbl.getValueAt(selectedRow, 1));

        // Adjusted DateTimeFormatter for 24-hour format without AM/PM
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm a");
        LocalTime orderTime = LocalTime.parse((String) orderHistoryTbl.getValueAt(selectedRow, 2), timeFormatter);

        List<Order> orderDetails = orderService.getOrderDetails(orderId, selectedVendorName, orderDate, orderTime);

        loadVendorMenu(selectedVendorName);
        guiHelper.panelSwitcher(reorderBtn, contentPanel, "vendorMenuPanel");
        vendorNameLabel.setText("Welcome to " + selectedVendorName);
        setTitle(selectedVendorName + " Menu - APFood");

        cartItems.clear(); // Clear existing cart items
        for (Order order : orderDetails) {
            Menu menuItem = vendorService.getVendorMenuItems(selectedVendorName).stream()
                                         .filter(menu -> menu.getId() == order.getMenuId())
                                         .findFirst()
                                         .orElse(null);

            if (menuItem != null) {
                cartItems.add(new Object[]{ menuItem.getId(), menuItem.getMenuName(), order.getQuantity(), order.getRemarks() });
            }
        }

        updateOrderCartTable();
        updateCartUIElements();
    }//GEN-LAST:event_reorderBtnMousePressed

    private void generateAndShowOrderReceipt(int orderId, String vendorName) {
        // Fetch order details
        List<Order> orders = orderService.getByOrderIdAndVendorName(orderId, vendorName);

        if (orders.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Order details not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get customer's name
        int userId = orders.get(0).getUserId();
        String customerName = userDao.getUserById(userId).getName();

        StringBuilder receiptContent = new StringBuilder();
        double subtotal = 0.0;
        receiptContent.append("Customer Name: ").append(customerName).append("\n")
                       .append("Order ID: ").append(orderId).append("\n")
                       .append("Vendor: ").append(vendorName).append("\n")
                       .append("Items:\n");

        for (Order order : orders) {
            Menu menuItem = menuDao.getMenuById(order.getMenuId(), vendorName);
            double itemTotal = menuItem.getPrice() * order.getQuantity();
            subtotal += itemTotal;

            receiptContent.append(" - ").append(menuItem.getMenuName())
                           .append(" x ").append(order.getQuantity())
                           .append(" @ RM ").append(String.format("%.2f", menuItem.getPrice()))
                           .append(" = RM ").append(String.format("%.2f", itemTotal))
                           .append("\n");
        }

        // Calculate delivery fee if applicable
        double deliveryFee = 0.0;
        if ("Delivery".equals(orders.get(0).getMode())) {
            String deliveryLocation = orders.get(0).getDeliveryLocation();
            deliveryFee = DeliveryFee.valueOf(deliveryLocation.toUpperCase().replace(" ", "_")).getFee();
        }

        double grandTotal = subtotal + deliveryFee;

        // Append delivery and total information
        receiptContent.append("Subtotal: RM ").append(String.format("%.2f", subtotal)).append("\n")
                       .append("Delivery Fee: RM ").append(String.format("%.2f", deliveryFee)).append("\n")
                       .append("Grand Total: RM ").append(String.format("%.2f", grandTotal));

        // Display the receipt
        JTextArea textArea = new JTextArea(receiptContent.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(this, scrollPane, "Order Receipt", JOptionPane.INFORMATION_MESSAGE);
    }

    private void displayVendorReviewDialog(int orderId, String vendorName) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Create and add a label for the feedback text area
        JLabel feedbackLabel = new JLabel("Feedback:");
        panel.add(feedbackLabel);

        // Create a text area for feedback
        JTextArea feedbackTextArea = new JTextArea(5, 20); // Adjust rows and columns as needed
        feedbackTextArea.setLineWrap(true);
        feedbackTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(feedbackTextArea);
        panel.add(scrollPane);

        // Create and add a label for the rating dropdown
        JLabel ratingLabel = new JLabel("Rating:");
        panel.add(ratingLabel);

        // Create a combobox for rating
        JComboBox<String> ratingComboBox = new JComboBox<>(new String[]{ "1", "2", "3", "4", "5" });
        panel.add(ratingComboBox);

        Object[] options = { "Submit", "Cancel" };

        // Show JOptionPane with the panel and custom buttons
        int result = JOptionPane.showOptionDialog(this, panel, "Write Review",
                                                  JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                                                  null, options, options[0]);

        if (result == JOptionPane.OK_OPTION) {
            String feedback = feedbackTextArea.getText();
            int rating = Integer.parseInt((String) ratingComboBox.getSelectedItem());
            Review review = new Review(feedback, rating, orderId);
            reviewService.addReview(review, vendorName);
        }
    }
    
    private void displayRunnerFeedbackDialog(int orderId, String vendorName) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Create and add a label for the feedback text area
        JLabel feedbackLabel = new JLabel("Feedback:");
        panel.add(feedbackLabel);

        // Create a text area for feedback
        JTextArea feedbackTextArea = new JTextArea(5, 20); // Adjust rows and columns as needed
        feedbackTextArea.setLineWrap(true);
        feedbackTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(feedbackTextArea);
        panel.add(scrollPane);

        Object[] options = { "Submit", "Cancel" };

        // Show JOptionPane with the panel and custom buttons
        int result = JOptionPane.showOptionDialog(this, panel, "Give Runner Feedback",
                                                  JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                                                  null, options, options[0]);

        if (result == JOptionPane.OK_OPTION) {
            String feedback = feedbackTextArea.getText();
            int runnerId = runnerTaskDao.getRunnerIdByOrderId(String.valueOf(orderId));
            runnerTaskDao.addRunnerFeedback(runnerId, orderId, vendorName, feedback);
            JOptionPane.showMessageDialog(this, "Feedback submitted successfully!", "Feedback", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showCreditTopUpReceipt(Transaction transaction, int adminUserId) {
        String userName = userDao.getUserById(transaction.getUserId()).getName();
        String adminName = userDao.getUserById(adminUserId).getName();

        // Create the receipt content
        String receiptContent = String.format(
            "Top-up for: %s\nTop-up by: %s\nAmount: RM %.2f\nDate: %s\nTime: %s\nRemarks: %s",
            userName, adminName, transaction.getAmount(), 
            transaction.getTransactionOn(), 
            transaction.getTransactionAt().format(DateTimeFormatter.ofPattern("HH:mm a")),
            transaction.getRemarks()
        );

        // Show the receipt in a popup
        JTextArea textArea = new JTextArea(receiptContent);
        textArea.setEditable(false);

        // Set a monospaced font and increase the font size
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        // Set the text area background and foreground colors
        textArea.setBackground(new Color(240, 240, 240)); // Light gray background
        textArea.setForeground(Color.BLACK); // Black text

        // Add padding to the text area
        textArea.setBorder(BorderFactory.createCompoundBorder(
            textArea.getBorder(), 
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Enable line wrapping and wrap by words
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(350, 150));
        JOptionPane.showMessageDialog(this, scrollPane, "Payment Receipt", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void notificationsTblMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_notificationsTblMousePressed
        int selectedRow = notificationsTbl.getSelectedRow();
        if (selectedRow != -1) {
            int notificationId = (Integer) notificationsTbl.getValueAt(selectedRow, 0);
            Notification notification = notificationService.getNotificationById(notificationId);

            if (notification != null) {
                if (notification.getNotificationType() == NotificationType.PUSH && notification.getNotificationStatus() == NotificationStatus.UNNOTIFIED) {
                    String[] pushOptions = { "Takeaway", "Dine-in", "Cancel Order" };
                    int selection = JOptionPane.showOptionDialog(this, 
                            "Select an option for your order:", 
                            "Order Option", 
                            JOptionPane.DEFAULT_OPTION, 
                            JOptionPane.INFORMATION_MESSAGE, 
                            null, pushOptions, pushOptions[0]);

                    if (selection == 0 || selection == 1) { // Takeaway or Dine-in
                        String mode = pushOptions[selection];
                        int orderId = Integer.parseInt(notification.getOrderId());
                        selectedVendorName = notification.getVendorName();

                        // Fetch order details
                        List<Order> orders = orderService.getByOrderIdAndVendorName(orderId, selectedVendorName);

                        // Check if the original order mode was Delivery and refund the delivery fee
                        if (!orders.isEmpty() && "Delivery".equals(orders.get(0).getMode())) {
                            double deliveryFeeToRefund = orderService.calculateOrderDeliveryFee(orderId, selectedVendorName);
                            transactionService.processRefund(loggedInUser.getId(), deliveryFeeToRefund, "Delivery Fee Refund");
                        }

                        // Update order mode to Takeaway or Dine-in
                        orderService.updateOrderMode(orderId, mode, selectedVendorName);
                    } else if (selection == 2) { // "Cancel Order" selected
                        int orderId = Integer.parseInt(notification.getOrderId());
                        selectedVendorName = notification.getVendorName();

                        // Fetch order details for refund calculation
                        List<Order> orders = orderService.getByOrderIdAndVendorName(orderId, selectedVendorName);
                        double amountToRefund = orderService.calculateTotalAmountForGroupedOrders(orders, selectedVendorName, loggedInUser.getId());

                        // Cancel the order and process the refund
                        orderService.cancelOrder(orderId, selectedVendorName);
                        transactionService.processRefund(loggedInUser.getId(), amountToRefund, "Order Refund");

                        JOptionPane.showMessageDialog(this, "Order cancelled. Refund processed.", "Order Cancelled", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                
                if (notification.getContent().contains("Credit top up")) {
                    int adminUserId = notification.getExtractedUserId();
                    int transactionId = notification.getTransactionId();
                    
                    // Retrieve the transaction based on the extracted IDs
                    Transaction transaction = transactionService.getTransactions().stream()
                        .filter(t -> t.getId() == transactionId && t.getUserId() == loggedInUser.getId())
                        .findFirst()
                        .orElse(null);

                    if (transaction != null) {
                        showCreditTopUpReceipt(transaction, adminUserId);
                    } else {
                        JOptionPane.showMessageDialog(this, "Transaction not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                
                if (isOrderAccepted(notification)) {
                    boolean reviewExists = reviewService.hasReviewForOrder(Integer.parseInt(notification.getOrderId()), notification.getVendorName());
                    int orderId = Integer.parseInt(notification.getOrderId());
                    selectedVendorName = notification.getVendorName();

                    if (!reviewExists) {
                        // Show option dialog only if review does not exist
                        String[] notificationOptions = {"View Receipt", "Give Review"};
                        int selection = JOptionPane.showOptionDialog(this,
                                "Select an option:",
                                "Notification Action",
                                JOptionPane.DEFAULT_OPTION,
                                JOptionPane.INFORMATION_MESSAGE,
                                null, notificationOptions, notificationOptions[0]);

                        try {
                            if (selection == 0) {
                                generateAndShowOrderReceipt(orderId, selectedVendorName);
                            } else if (selection == 1) {
                                displayVendorReviewDialog(orderId, selectedVendorName);
                            }
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(this, "Invalid order ID.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        generateAndShowOrderReceipt(orderId, selectedVendorName);
                    }
                }
                
                if ("Give Feedback".equals(notificationsTbl.getValueAt(selectedRow, 3))) {
                    int orderId = Integer.parseInt(notification.getOrderId());
                    selectedVendorName = notification.getVendorName();
                    try {
                        if (!runnerTaskDao.hasFeedbackForOrder(orderId, selectedVendorName)) {
                            displayRunnerFeedbackDialog(orderId, selectedVendorName);
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Invalid order ID.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

                // Update notification status as 'Notified' regardless of the notification type
                if (notification.getNotificationStatus() == NotificationStatus.UNNOTIFIED) {
                    notification.setNotificationStatus(NotificationStatus.NOTIFIED);
                    notificationService.updateNotification(notification);

                    // Save the current scroll position
                    JScrollBar scrollBar = jScrollPane3.getVerticalScrollBar();
                    int scrollPosition = scrollBar.getValue();

                    displayNotifications(); // Refresh the table

                    // Restore the scroll position
                    SwingUtilities.invokeLater(() -> scrollBar.setValue(scrollPosition));

                    // Restore the row selection
                    if (selectedRow < notificationsTbl.getRowCount()) {
                        notificationsTbl.setRowSelectionInterval(selectedRow, selectedRow);
                    }
                }
            }
        }
    }//GEN-LAST:event_notificationsTblMousePressed

    private void subscriptionsSidebarBtnMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_subscriptionsSidebarBtnMousePressed
        guiHelper.panelSwitcher(subscriptionsSidebarBtn, contentPanel, "subscriptionsPanel");
        setTitle("My Subscriptions - APFood");
        
        updateSubscriptionStatusDisplay();
    }//GEN-LAST:event_subscriptionsSidebarBtnMousePressed

    private void quickVendorSelecctionBtnMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_quickVendorSelecctionBtnMousePressed
        guiHelper.panelSwitcher(quickVendorSelecctionBtn, contentPanel, "vendorsPanel");
        setTitle("All Vendors - APFood");
    }//GEN-LAST:event_quickVendorSelecctionBtnMousePressed

    private void searchVendorMenuTxtFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchVendorMenuTxtFieldKeyReleased
        tableHelper.searchTable(vendorMenuTbl, searchVendorMenuTxtField.getText(), new int[] {1});
    }//GEN-LAST:event_searchVendorMenuTxtFieldKeyReleased
        
    private void markAllAsReadBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_markAllAsReadBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_markAllAsReadBtnActionPerformed

    private void searchNotificationTxtFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchNotificationTxtFieldKeyReleased
        tableHelper.searchTable(notificationsTbl, searchNotificationTxtField.getText(), new int[] { 1, 2 });
    }//GEN-LAST:event_searchNotificationTxtFieldKeyReleased

    private void subscribeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subscribeBtnActionPerformed

        // Use the service layer to check for active subscription
        if (isUserSubscribed) {
            JOptionPane.showMessageDialog(this, "You already have an active subscription.", "Subscription", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Add the subscription for the user
            subscriptionService.addSubscription(loggedInUser.getId());

            // Update user's credit balance display
            updateCreditBalanceDisplay();

            JOptionPane.showMessageDialog(this, "Subscription successful. Enjoy your benefits!", "Subscription", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_subscribeBtnActionPerformed

    private List<Map<String, String>> fetchVendorReviews(String vendorName) {
        List<Review> reviews = reviewService.getCustomerReviewsForVendor(vendorName);
        List<Map<String, String>> processedReviews = new ArrayList<>();

        for (Review review : reviews) {
            String customerId = userDao.getCustomerId(String.valueOf(review.getOrderId()), vendorName);
            String customerName = userDao.getUserById(Integer.parseInt(customerId)).getName();

            Map<String, String> reviewData = new HashMap<>();
            reviewData.put("customerName", customerName);
            reviewData.put("feedback", review.getFeedback());
            reviewData.put("rating", review.getRating() + " Stars");

            processedReviews.add(reviewData);
        }

        return processedReviews;
    }
    
    private void populateVendorReviewsTable(String vendorName) {
        List<Map<String, String>> reviews = fetchVendorReviews(vendorName);

        Function<Map<String, String>, Object[]> rowMapper = review -> 
            new Object[]{ review.get("customerName"), review.get("feedback"), review.get("rating") };

        tableHelper.populateTable(reviews, reviewsTbl, rowMapper);
        tableHelper.centerTableValues(reviewsTbl);
    }
    
    private String getFormattedVendorReviews(String vendorName) {
        List<Map<String, String>> reviews = fetchVendorReviews(vendorName);
        StringBuilder formattedReviews = new StringBuilder();

        for (Map<String, String> review : reviews) {
            formattedReviews.append(review.get("customerName"))
                            .append(" - ")
                            .append(review.get("rating"))
                            .append(": ")
                            .append(review.get("feedback"))
                            .append("\n\n");
        }

        return formattedReviews.toString();
    }
    
    private void viewReviewsBtnFromVendorPanelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_viewReviewsBtnFromVendorPanelMousePressed
        
        int selectedRow = vendorsTable.getSelectedRow();
        if (selectedRow >= 0) {
            selectedVendorName = vendorsTable.getValueAt(selectedRow, 1).toString();
            populateVendorReviewsTable(selectedVendorName);
            guiHelper.panelSwitcher(viewReviewsBtnFromVendorPanel, contentPanel, "vendorReviewsPanel");
            setTitle(selectedVendorName + " Reviews - APFood");
        } else {
            JOptionPane.showMessageDialog(this, "Please select a vendor to view customer reviews of this vendor.");
        }
    }//GEN-LAST:event_viewReviewsBtnFromVendorPanelMousePressed

    private void viewReviewsFromMenuBtnMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_viewReviewsFromMenuBtnMousePressed
        String reviews = getFormattedVendorReviews(selectedVendorName);
        JTextArea textArea = new JTextArea(reviews);
        textArea.setEditable(false);

        // Set a soft background color and customize font
        textArea.setBackground(new Color(245, 245, 220)); // Soft cream color
        textArea.setFont(new Font("Serif", Font.PLAIN, 14));
        textArea.setForeground(Color.DARK_GRAY); // Dark gray text for readability
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setMargin(new Insets(5, 5, 5, 5)); // Add some padding

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(350, 150));
        scrollPane.getViewport().setBackground(textArea.getBackground()); // Match the color of the viewport to the JTextArea

        JOptionPane.showMessageDialog(this, scrollPane, selectedVendorName + " Reviews", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_viewReviewsFromMenuBtnMousePressed

    private void topUpPanelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_topUpPanelMousePressed
        String topUpAmountStr = JOptionPane.showInputDialog(this, "Enter the amount to top up:", "Credit Top-Up Request", JOptionPane.PLAIN_MESSAGE);

        if (topUpAmountStr != null && !topUpAmountStr.isEmpty()) {
            try {
                double topUpAmount = Double.parseDouble(topUpAmountStr);
                if (topUpAmount <= 0) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid amount.", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                User nextAdmin = userService.getNextAdmin();
                
                System.out.println(nextAdmin);

                if (nextAdmin != null) {
                    String notificationContent = String.format("%s requests for a credit top-up [user id: %d, amount: RM %.2f]",
                                                               loggedInUser.getName(), loggedInUser.getId(), topUpAmount);
                    Notification topUpRequestNotification = new Notification(nextAdmin.getId(), notificationContent, NotificationType.PUSH);
                    notificationDao.add(topUpRequestNotification);

                    JOptionPane.showMessageDialog(this, "Top-up request sent successfully.", "Request Sent", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "No admin available for top-up request.", "Admin Unavailable", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount.", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_topUpPanelMousePressed
    
    private void updateVendorMenuUIElements() {
        itemQtyLabel.setText(String.valueOf(menuItemQuantity));
        decreaseItemQtyBtn.setEnabled(menuItemQuantity > 1);
        itemQtyLabel.setEnabled(menuItemQuantity >= 1);
        itemRemarksTxtArea.setEnabled(menuItemQuantity >= 1);
        addToCartBtn.setEnabled(menuItemQuantity >= 1);
        updateCartBtn.setEnabled(false);
    }
    
    private void updateOrderCartTable() {
    
        Function<Object[], Object[]> rowMapper = cartItem -> new Object[] { cartItem[1], cartItem[2], cartItem[3] }; 

        tableHelper.populateTable(cartItems, orderCartTbl, rowMapper);
        tableHelper.centerTableValues(orderCartTbl);
    }
    
    private void checkAndEnableDeliveryLocations() {
        boolean hasCartItems = !cartItems.isEmpty();
        String selectedOrderMode = (String) orderModesComboBox.getSelectedItem();
        boolean isDeliveryMode = "Delivery".equals(selectedOrderMode);

        deliveryLocationsComboBox.setEnabled(hasCartItems && isDeliveryMode);
    }

    private void updateCartUIElements() {
        boolean hasCartItems = !cartItems.isEmpty();
        
        clearCartBtn.setEnabled(hasCartItems);
        orderModesComboBox.setEnabled(hasCartItems);
        placeOrderBtn.setEnabled(hasCartItems);
        deliveryLocationsComboBox.setSelectedIndex(-1);
        checkAndEnableDeliveryLocations();
    }
    
    private void loadVendorMenu(String vendorName) {
        List<Menu> menuItems = vendorService.getVendorMenuItems(vendorName);
        menuItemIdMap.clear(); // Clear previous entries

        Function<Menu, Object[]> rowMapper = menu -> {
            menuItemIdMap.put(menu.getMenuName(), menu.getId());
            return new Object[] { menu.getMenuName(), menu.getMenuType(), menu.getPrice() };
        };

        tableHelper.populateTable(menuItems, vendorMenuTbl, rowMapper, true);
        tableHelper.centerTableValues(vendorMenuTbl);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the FlatLaf look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        try {
            javax.swing.UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CustomerForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                User mockUser = new User(1, "Adam Smith", "qwe@qwe.com", "qweqweqwe".toCharArray(), "customer");
                new CustomerForm(mockUser).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addToCartBtn;
    private javax.swing.JLabel apfoodTxtLabel;
    private javax.swing.JButton browseMenuBtn;
    private javax.swing.JButton cafeteriaSidebarBtn;
    private javax.swing.JButton clearCartBtn;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JPanel costSummaryPanel;
    private javax.swing.JPanel costSummaryParentPanel;
    private javax.swing.JPanel costSummaryWithDeliveryAndDiscountPanel;
    private javax.swing.JPanel costSummaryWithDeliveryPanel;
    private javax.swing.JPanel costSummaryWithDiscountPanel;
    private javax.swing.JButton decreaseItemQtyBtn;
    private javax.swing.JComboBox<String> deliveryLocationsComboBox;
    private javax.swing.JPanel financePanel;
    private javax.swing.JButton financeSidebarBtn;
    private javax.swing.JButton historyBtn;
    private javax.swing.JPanel historyContentPanel;
    private javax.swing.JPanel historyPanel;
    private javax.swing.JPanel homeContentPanel;
    private javax.swing.JPanel homePanel;
    private javax.swing.JButton homeSidebarBtn;
    private javax.swing.JButton increaseItemQtyBtn;
    private javax.swing.JLabel itemQtyLabel;
    private javax.swing.JTextArea itemRemarksTxtArea;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel32;
    private javax.swing.JPanel jPanel33;
    private javax.swing.JPanel jPanel34;
    private javax.swing.JPanel jPanel35;
    private javax.swing.JPanel jPanel36;
    private javax.swing.JPanel jPanel37;
    private javax.swing.JPanel jPanel38;
    private javax.swing.JPanel jPanel39;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel40;
    private javax.swing.JPanel jPanel41;
    private javax.swing.JPanel jPanel42;
    private javax.swing.JPanel jPanel43;
    private javax.swing.JPanel jPanel44;
    private javax.swing.JPanel jPanel45;
    private javax.swing.JPanel jPanel46;
    private javax.swing.JPanel jPanel47;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton markAllAsReadBtn;
    private javax.swing.JPanel notificationsPanel;
    private javax.swing.JButton notificationsSidebarBtn;
    private javax.swing.JTable notificationsTbl;
    private javax.swing.JTable ongoingOrderDeliveryTbl;
    private javax.swing.JTable ongoingOrderDeliveryTbl1;
    private javax.swing.JTable orderCartTbl;
    private javax.swing.JButton orderHistoryBackBtn;
    private javax.swing.JTable orderHistoryTbl;
    private javax.swing.JComboBox<String> orderModesComboBox;
    private javax.swing.JPanel orderSummaryPanel;
    private javax.swing.JButton placeOrderBtn;
    private javax.swing.JButton quickVendorSelecctionBtn;
    private javax.swing.JButton refreshNotificationBtn;
    private javax.swing.JButton reorderBtn;
    private javax.swing.JTable reviewsTbl;
    private javax.swing.JTextField searchNotificationTxtField;
    private javax.swing.JTextField searchVendorMenuTxtField;
    private javax.swing.JPanel sidePanel;
    private javax.swing.JButton subscribeBtn;
    private javax.swing.JLabel subscriptionStatusLabel;
    private javax.swing.JLabel subscriptionValidityLabel;
    private javax.swing.JPanel subscriptionValidityPanel;
    private javax.swing.JPanel subscriptionsPanel;
    private javax.swing.JButton subscriptionsSidebarBtn;
    private javax.swing.JPanel topBarPanel;
    private javax.swing.JPanel topUpPanel;
    private javax.swing.JTable transactionsTbl;
    private javax.swing.JButton updateCartBtn;
    private javax.swing.JLabel userCreditBalanceLabel1;
    private javax.swing.JLabel userCreditBalanceLabel2;
    private javax.swing.JLabel userEmailLabel;
    private javax.swing.JLabel userFullNameLabel;
    private javax.swing.JPanel vendorMenuPanel;
    private javax.swing.JTable vendorMenuTbl;
    private javax.swing.JLabel vendorNameLabel;
    private javax.swing.JPanel vendorReviewsPanel;
    private javax.swing.JPanel vendorsPanel;
    private javax.swing.JTable vendorsTable;
    private javax.swing.JButton viewPastOrderDetailsBtn;
    private javax.swing.JButton viewReviewsBtnFromVendorPanel;
    private javax.swing.JButton viewReviewsFromMenuBtn;
    // End of variables declaration//GEN-END:variables
}
