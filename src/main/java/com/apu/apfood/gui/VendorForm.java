package com.apu.apfood.gui;

import com.apu.apfood.db.dao.MenuDao;
import com.apu.apfood.db.dao.UserDao;
import com.apu.apfood.db.enums.NotificationStatus;
import com.apu.apfood.db.enums.OrderStatus;
import com.apu.apfood.db.models.Menu;
import com.apu.apfood.db.models.Order;
import com.apu.apfood.db.models.OrderDetails;
import com.apu.apfood.db.models.User;
import com.apu.apfood.helpers.GUIHelper;
import com.apu.apfood.helpers.ImageHelper;
import com.apu.apfood.helpers.TableHelper;
import com.apu.apfood.services.VendorService;
import com.formdev.flatlaf.FlatDarculaLaf;
import java.awt.CardLayout;
import java.awt.Component;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class VendorForm extends javax.swing.JFrame {

    private User user;
    private VendorService vs;
    private UserDao ud;
    private MenuDao md;
    private String vendorName;
    private final Map<String, Integer> menuItemIdMap = new HashMap<>();
    private Map<Integer, OrderDetails> ordersMap = new HashMap<>();
    
    // Instantiate helpers classes
    ImageHelper imageHelper = new ImageHelper();
    GUIHelper guiHelper = new GUIHelper();
    TableHelper tableHelper = new TableHelper();
    /**
     * Creates new form VendorFrame
     */
    public VendorForm(User user) {
        this.user = user;
        this.ud = new UserDao();
        this.md = new MenuDao();
        this.vs = new VendorService(user);
        this.vendorName = vs.getVendorName();
        initComponents();
        initCustomComponents();
        
        
        // Enable side buttons for switching panels
        guiHelper.panelSwitcher(homeNavBtn, contentPanel, "homePanel");
        guiHelper.panelSwitcher(menuBtn, contentPanel, "menuPanel");
        guiHelper.panelSwitcher(ordersBtn, contentPanel, "ordersPanel");
        guiHelper.panelSwitcher(orderHistoryBtn, contentPanel, "orderHistoryPanel");
        guiHelper.panelSwitcher(revenueDashboardBtn, contentPanel, "revenueDashboardPanel");
        guiHelper.panelSwitcher(notificationsBtn, contentPanel, "notificationsPanel");
        
        nameLabel.setText(user.getName());
        emailLabel.setText(user.getEmail());
        vendorLabel.setText(vendorName + " Vendor");
        menuLabel.setText(vendorName + " Menu");
        
        vs.populateDateComboBoxes(yearBeforeCmbBox, monthBeforeCmbBox, dayBeforeCmbBox);
        vs.populateDateComboBoxes(yearAfterCmbBox, monthAfterCmbBox, dayAfterCmbBox);
        
    }

    
    
    private void initCustomComponents() {
        imageHelper.setFrameIcon(this, "/icons/apu-logo.png");
        GUIHelper.JFrameSetup(this);
        
        populateMenuTable();
        populateIncomingOrdersTable();
        populateOrdersInProgressTable();
        populateOrderHistoryTable();
        populateRevenueDashboard();
        populateNotificationsTable();
        
        setupHomePage();
    }

    public void populateMenuTable() {
        vs.populateMenuTable(menuTable);
    }
    
    public void populateIncomingOrdersTable()
    {
        vs.populateOrderTable(incomingOrdersTable, OrderStatus.PENDING);
    }
    
    public void populateOrdersInProgressTable()
    {
        vs.populateOrderTable(ordersInProgressTable, OrderStatus.IN_PROGRESS);
    }
    
    public void populateOrderHistoryTable()
    {
        vs.populateOrderHistoryTable(orderHistoryTable);
    }
    
    public void populateRevenueDashboard()
    {
        vs.populateRevenueDashboard(yearRevenueLabel, monthRevenueLabel, dayRevenueLabel, menuTable);
    }
    
    public void populateNotificationsTable()
    {
        vs.populateNotificationsTable(notificationsTable, user.getId());
    }
    
    public void setupHomePage()
    {
        ordersLabel.setText("Incoming Orders: " + incomingOrdersTable.getRowCount());
        notificationsLabel.setText("Unread Notifications: " + notificationsTable.getRowCount());
    }
    
    public void populateRevenueOrdersTable()
    {
        try {
        int yearBefore = (int) yearBeforeCmbBox.getSelectedItem();
        int monthBefore = (int) monthBeforeCmbBox.getSelectedItem();
        int dayBefore = (int) dayBeforeCmbBox.getSelectedItem();
        LocalDate beforeDate = LocalDate.of(yearBefore, monthBefore, dayBefore);
        
        int yearAfter = (int) yearAfterCmbBox.getSelectedItem();
        int monthAfter = (int) monthAfterCmbBox.getSelectedItem();
        int dayAfter = (int) dayAfterCmbBox.getSelectedItem();
        LocalDate afterDate = LocalDate.of(yearAfter, monthAfter, dayAfter);
        vs.populateRevenueOrdersTable(revenueOrdersTable, beforeDate, afterDate);
        // Loop through each row and add up the values in the price column
        double totalPrice = 0.0;
        for (int row = 0; row < revenueOrdersTable.getRowCount(); row++) {
            try {
                Object value = revenueOrdersTable.getValueAt(row, 7);
                if (value != null) {
                    totalPrice += Double.parseDouble(value.toString());
                }
            } catch (NumberFormatException e) {
                // Handle the case where the value in the column cannot be parsed as a double
                e.printStackTrace(); // Print the stack trace for now, handle it appropriately in your application
            }
        }
        totalRevenueLabel.setText("Total Revenue: " + totalPrice);
        } catch (Exception ex) {
            //Nothing
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
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel3 = new javax.swing.JPanel();
        homeNavBtn = new javax.swing.JButton();
        ordersBtn = new javax.swing.JButton();
        orderHistoryBtn = new javax.swing.JButton();
        menuBtn = new javax.swing.JButton();
        revenueDashboardBtn = new javax.swing.JButton();
        notificationsBtn = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        topBarPanel = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        emailLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        contentPanel = new javax.swing.JPanel();
        homePanel = new javax.swing.JPanel();
        notificationsLabel = new javax.swing.JLabel();
        vendorLabel = new javax.swing.JLabel();
        ordersLabel = new javax.swing.JLabel();
        menuPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        menuTable = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        saveAllBtn = new javax.swing.JButton();
        refreshBtn = new javax.swing.JButton();
        AddBtn = new javax.swing.JButton();
        removeBtn = new javax.swing.JButton();
        menuLabel = new javax.swing.JLabel();
        ordersPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        incomingOrdersTable = new javax.swing.JTable();
        declineBtn = new javax.swing.JButton();
        ordersRefreshBtn = new javax.swing.JButton();
        acceptBtn = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        ordersInProgressTable = new javax.swing.JTable();
        readyBtn = new javax.swing.JButton();
        declineBtn1 = new javax.swing.JButton();
        orderHistoryPanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        orderHistoryTable = new javax.swing.JTable();
        orderHistoryRefreshBtn1 = new javax.swing.JButton();
        revenueDashboardPanel = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        monthRevenueLabel = new javax.swing.JLabel();
        dayRevenueLabel = new javax.swing.JLabel();
        yearRevenueLabel = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        revenueOrdersTable = new javax.swing.JTable();
        monthBeforeCmbBox = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        yearBeforeCmbBox = new javax.swing.JComboBox<>();
        dayBeforeCmbBox = new javax.swing.JComboBox<>();
        yearAfterCmbBox = new javax.swing.JComboBox<>();
        monthAfterCmbBox = new javax.swing.JComboBox<>();
        dayAfterCmbBox = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        totalRevenueLabel = new javax.swing.JLabel();
        notificationsPanel = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        notificationsTable = new javax.swing.JTable();
        readNotificationBtn = new javax.swing.JButton();
        notificationsRefreshBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Home - APFood");
        setResizable(false);

        sidePanel.setBackground(new java.awt.Color(0, 89, 100));
        sidePanel.setPreferredSize(new java.awt.Dimension(250, 900));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("APFood");

        jSeparator1.setBackground(new java.awt.Color(204, 204, 204));
        jSeparator1.setForeground(new java.awt.Color(255, 255, 255));

        jPanel3.setForeground(new java.awt.Color(30, 30, 30));
        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridLayout(6, 1, 0, 30));

        homeNavBtn.setForeground(new java.awt.Color(255, 255, 255));
        homeNavBtn.setText("Home");
        homeNavBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        homeNavBtn.setFocusPainted(false);
        homeNavBtn.setName(""); // NOI18N
        homeNavBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                homeNavBtnActionPerformed(evt);
            }
        });
        jPanel3.add(homeNavBtn);

        ordersBtn.setForeground(new java.awt.Color(255, 255, 255));
        ordersBtn.setText("Orders");
        jPanel3.add(ordersBtn);

        orderHistoryBtn.setForeground(new java.awt.Color(255, 255, 255));
        orderHistoryBtn.setText("Order History");
        jPanel3.add(orderHistoryBtn);

        menuBtn.setForeground(new java.awt.Color(255, 255, 255));
        menuBtn.setText("Menu");
        jPanel3.add(menuBtn);

        revenueDashboardBtn.setForeground(new java.awt.Color(255, 255, 255));
        revenueDashboardBtn.setText("Revenue Dashboard");
        jPanel3.add(revenueDashboardBtn);

        notificationsBtn.setForeground(new java.awt.Color(255, 255, 255));
        notificationsBtn.setText("Notifications");
        jPanel3.add(notificationsBtn);

        javax.swing.GroupLayout sidePanelLayout = new javax.swing.GroupLayout(sidePanel);
        sidePanel.setLayout(sidePanelLayout);
        sidePanelLayout.setHorizontalGroup(
            sidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sidePanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(sidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(jSeparator1)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(30, 30, 30))
        );
        sidePanelLayout.setVerticalGroup(
            sidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidePanelLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 517, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(276, Short.MAX_VALUE))
        );

        getContentPane().add(sidePanel, java.awt.BorderLayout.LINE_START);

        mainPanel.setLayout(new java.awt.BorderLayout());

        topBarPanel.setBackground(new java.awt.Color(255, 255, 255));
        topBarPanel.setPreferredSize(new java.awt.Dimension(1350, 80));

        nameLabel.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        nameLabel.setForeground(new java.awt.Color(0, 0, 0));
        nameLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        nameLabel.setText("Full Name");

        emailLabel.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        emailLabel.setForeground(new java.awt.Color(102, 102, 102));
        emailLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        emailLabel.setText("TP0xxxxxx@mail.apu.edu.my");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/profile-icon.png"))); // NOI18N
        jLabel5.setName(""); // NOI18N

        jSeparator2.setForeground(new java.awt.Color(102, 102, 102));
        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/bell.png"))); // NOI18N

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 0, 0));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel7.setText("3+");

        javax.swing.GroupLayout topBarPanelLayout = new javax.swing.GroupLayout(topBarPanel);
        topBarPanel.setLayout(topBarPanelLayout);
        topBarPanelLayout.setHorizontalGroup(
            topBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, topBarPanelLayout.createSequentialGroup()
                .addContainerGap(1032, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addGap(0, 0, 0)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addGap(2, 2, 2)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(topBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(emailLabel)
                    .addComponent(nameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                                .addGroup(topBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(topBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(topBarPanelLayout.createSequentialGroup()
                                            .addComponent(nameLabel)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(emailLabel)))
                                    .addComponent(jLabel7))
                                .addGap(2, 2, 2))))
                    .addGroup(topBarPanelLayout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(topBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel4))))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        mainPanel.add(topBarPanel, java.awt.BorderLayout.PAGE_START);

        contentPanel.setLayout(new java.awt.CardLayout());

        homePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        notificationsLabel.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        notificationsLabel.setText("jLabel2");
        homePanel.add(notificationsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 300, 320, 40));

        vendorLabel.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        vendorLabel.setText("jLabel2");
        homePanel.add(vendorLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 110, 320, 40));

        ordersLabel.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        ordersLabel.setText("jLabel2");
        homePanel.add(ordersLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 230, 320, 40));

        contentPanel.add(homePanel, "homePanel");

        menuPanel.setPreferredSize(new java.awt.Dimension(648, 136));

        menuTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Name", "Type", "Price"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(menuTable);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("Double click the cells in the table to change their data values.");

        saveAllBtn.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        saveAllBtn.setText("Save All");
        saveAllBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAllBtnActionPerformed(evt);
            }
        });

        refreshBtn.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        refreshBtn.setText("Refresh");
        refreshBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshBtnActionPerformed(evt);
            }
        });

        AddBtn.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        AddBtn.setText("Add");
        AddBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddBtnActionPerformed(evt);
            }
        });

        removeBtn.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        removeBtn.setText("Remove");
        removeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeBtnActionPerformed(evt);
            }
        });

        menuLabel.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        menuLabel.setText("(vendorName) Menu");

        javax.swing.GroupLayout menuPanelLayout = new javax.swing.GroupLayout(menuPanel);
        menuPanel.setLayout(menuPanelLayout);
        menuPanelLayout.setHorizontalGroup(
            menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuPanelLayout.createSequentialGroup()
                .addGap(164, 164, 164)
                .addGroup(menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1014, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 555, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(menuPanelLayout.createSequentialGroup()
                            .addComponent(AddBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(48, 48, 48)
                            .addComponent(removeBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(refreshBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(48, 48, 48)
                            .addComponent(saveAllBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(menuLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(172, Short.MAX_VALUE))
        );
        menuPanelLayout.setVerticalGroup(
            menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuPanelLayout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addComponent(menuLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 475, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54)
                .addGroup(menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveAllBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(refreshBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(removeBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AddBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(87, Short.MAX_VALUE))
        );

        contentPanel.add(menuPanel, "menuPanel");

        ordersPanel.setMinimumSize(new java.awt.Dimension(648, 136));
        ordersPanel.setPreferredSize(new java.awt.Dimension(648, 136));

        incomingOrdersTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "OrderID", "Customer Name", "Items", "Quantity", "Remarks", "Date", "Time", "Mode"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(incomingOrdersTable);

        declineBtn.setText("Decline");
        declineBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                declineBtnActionPerformed(evt);
            }
        });

        ordersRefreshBtn.setText("Refresh");
        ordersRefreshBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ordersRefreshBtnActionPerformed(evt);
            }
        });

        acceptBtn.setText("Accept");
        acceptBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acceptBtnActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setText("Orders in Progress");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel8.setText("Incoming Orders");

        ordersInProgressTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "OrderID", "Customer Name", "Items", "Quantity", "Remarks", "Date", "Time", "Mode"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(ordersInProgressTable);

        readyBtn.setText("Ready");
        readyBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readyBtnActionPerformed(evt);
            }
        });

        declineBtn1.setText("Cancel");
        declineBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                declineBtn1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ordersPanelLayout = new javax.swing.GroupLayout(ordersPanel);
        ordersPanel.setLayout(ordersPanelLayout);
        ordersPanelLayout.setHorizontalGroup(
            ordersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ordersPanelLayout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(ordersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ordersPanelLayout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ordersRefreshBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ordersPanelLayout.createSequentialGroup()
                        .addGroup(ordersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(ordersPanelLayout.createSequentialGroup()
                                .addComponent(readyBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(declineBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(ordersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(ordersPanelLayout.createSequentialGroup()
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(acceptBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(declineBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 1144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1144, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(100, 100, 100)))
                .addGap(65, 65, 65))
        );
        ordersPanelLayout.setVerticalGroup(
            ordersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ordersPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ordersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8)
                    .addComponent(ordersRefreshBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ordersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ordersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(acceptBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(declineBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ordersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(readyBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(declineBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(75, Short.MAX_VALUE))
        );

        contentPanel.add(ordersPanel, "ordersPanel");

        orderHistoryTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "OrderId", "Customer Name", "Items", "Quantity", "Remark", "Date", "Time", "Mode", "Rating", "Feedback"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(orderHistoryTable);

        orderHistoryRefreshBtn1.setText("Refresh");
        orderHistoryRefreshBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orderHistoryRefreshBtn1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout orderHistoryPanelLayout = new javax.swing.GroupLayout(orderHistoryPanel);
        orderHistoryPanel.setLayout(orderHistoryPanelLayout);
        orderHistoryPanelLayout.setHorizontalGroup(
            orderHistoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(orderHistoryPanelLayout.createSequentialGroup()
                .addContainerGap(75, Short.MAX_VALUE)
                .addGroup(orderHistoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, orderHistoryPanelLayout.createSequentialGroup()
                        .addComponent(orderHistoryRefreshBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, orderHistoryPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 1179, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(96, 96, 96))))
        );
        orderHistoryPanelLayout.setVerticalGroup(
            orderHistoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(orderHistoryPanelLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(orderHistoryRefreshBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 543, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(189, Short.MAX_VALUE))
        );

        contentPanel.add(orderHistoryPanel, "orderHistoryPanel");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel9.setText("Revenue for this Year:");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel10.setText("Revenue for today:");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel11.setText("Revenue for this Month:");

        monthRevenueLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        monthRevenueLabel.setText("0.0");

        dayRevenueLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        dayRevenueLabel.setText("0.0");

        yearRevenueLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        yearRevenueLabel.setText("0.0");

        revenueOrdersTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "OrderId", "Customer Name", "Items", "Quantity", "Price", "Date", "Time", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane5.setViewportView(revenueOrdersTable);

        monthBeforeCmbBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                monthBeforeCmbBoxActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel12.setText("Dates in YYYY/MM/DD:");

        yearBeforeCmbBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yearBeforeCmbBoxActionPerformed(evt);
            }
        });

        dayBeforeCmbBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dayBeforeCmbBoxActionPerformed(evt);
            }
        });

        yearAfterCmbBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yearAfterCmbBoxActionPerformed(evt);
            }
        });

        monthAfterCmbBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                monthAfterCmbBoxActionPerformed(evt);
            }
        });

        dayAfterCmbBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dayAfterCmbBoxActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel13.setText("--->");

        totalRevenueLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        totalRevenueLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        totalRevenueLabel.setText("jLabel14");

        javax.swing.GroupLayout revenueDashboardPanelLayout = new javax.swing.GroupLayout(revenueDashboardPanel);
        revenueDashboardPanel.setLayout(revenueDashboardPanelLayout);
        revenueDashboardPanelLayout.setHorizontalGroup(
            revenueDashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, revenueDashboardPanelLayout.createSequentialGroup()
                .addGroup(revenueDashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(revenueDashboardPanelLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(totalRevenueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(revenueDashboardPanelLayout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addGroup(revenueDashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                            .addGroup(revenueDashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(monthRevenueLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(yearRevenueLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE))
                            .addComponent(dayRevenueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 67, Short.MAX_VALUE)
                        .addGroup(revenueDashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 930, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, revenueDashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(revenueDashboardPanelLayout.createSequentialGroup()
                                    .addComponent(yearBeforeCmbBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(monthBeforeCmbBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(dayBeforeCmbBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(jLabel13)
                                    .addGap(18, 18, 18)
                                    .addComponent(yearAfterCmbBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(monthAfterCmbBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(dayAfterCmbBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addGap(91, 91, 91))
        );
        revenueDashboardPanelLayout.setVerticalGroup(
            revenueDashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(revenueDashboardPanelLayout.createSequentialGroup()
                .addGroup(revenueDashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(revenueDashboardPanelLayout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(dayRevenueLabel)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(monthRevenueLabel)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(yearRevenueLabel))
                    .addGroup(revenueDashboardPanelLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(revenueDashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(monthBeforeCmbBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(yearBeforeCmbBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dayBeforeCmbBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(monthAfterCmbBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(yearAfterCmbBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dayAfterCmbBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 543, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(30, 30, 30)
                .addComponent(totalRevenueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(110, Short.MAX_VALUE))
        );

        jLabel12.getAccessibleContext().setAccessibleName("Dates in YYYY/MM/DD:");

        contentPanel.add(revenueDashboardPanel, "revenueDashboardPanel");

        notificationsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null}
            },
            new String [] {
                "Id", "Content", "Type", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane6.setViewportView(notificationsTable);

        readNotificationBtn.setText("Read");
        readNotificationBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readNotificationBtnActionPerformed(evt);
            }
        });

        notificationsRefreshBtn.setText("Refresh");
        notificationsRefreshBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                notificationsRefreshBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout notificationsPanelLayout = new javax.swing.GroupLayout(notificationsPanel);
        notificationsPanel.setLayout(notificationsPanelLayout);
        notificationsPanelLayout.setHorizontalGroup(
            notificationsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, notificationsPanelLayout.createSequentialGroup()
                .addContainerGap(210, Short.MAX_VALUE)
                .addGroup(notificationsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 930, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(readNotificationBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(210, 210, 210))
            .addGroup(notificationsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, notificationsPanelLayout.createSequentialGroup()
                    .addContainerGap(1173, Short.MAX_VALUE)
                    .addComponent(notificationsRefreshBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(55, 55, 55)))
        );
        notificationsPanelLayout.setVerticalGroup(
            notificationsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, notificationsPanelLayout.createSequentialGroup()
                .addContainerGap(120, Short.MAX_VALUE)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 543, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(readNotificationBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(94, 94, 94))
            .addGroup(notificationsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(notificationsPanelLayout.createSequentialGroup()
                    .addGap(63, 63, 63)
                    .addComponent(notificationsRefreshBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(712, Short.MAX_VALUE)))
        );

        contentPanel.add(notificationsPanel, "notificationsPanel");

        mainPanel.add(contentPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void homeNavBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_homeNavBtnActionPerformed
        // TODO add your handling code here:
        setupHomePage();
    }//GEN-LAST:event_homeNavBtnActionPerformed

    private void refreshBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshBtnActionPerformed
        // TODO add your handling code here:
        populateMenuTable();
    }//GEN-LAST:event_refreshBtnActionPerformed

    private void saveAllBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAllBtnActionPerformed
        // TODO add your handling code here:
        List<Menu> menus = vs.convertJTableToMenuList(menuTable);
        vs.updateMenuItems(vendorName, menus);
    }//GEN-LAST:event_saveAllBtnActionPerformed

    private void AddBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddBtnActionPerformed
        // TODO add your handling code here:
        tableHelper.addRowinTable(menuTable, Arrays.asList("foodName", "food", "5.50"));
    }//GEN-LAST:event_AddBtnActionPerformed

    private void removeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeBtnActionPerformed
        // TODO add your handling code here:
        int selectedRow = menuTable.getSelectedRow();

        if (selectedRow != -1) {
            DefaultTableModel model = (DefaultTableModel) menuTable.getModel();
            model.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(null, "Please select a row to remove.", "No Row Selected", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_removeBtnActionPerformed

    private void ordersRefreshBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ordersRefreshBtnActionPerformed
        // TODO add your handling code here:
        populateIncomingOrdersTable();
        populateOrdersInProgressTable();
    }//GEN-LAST:event_ordersRefreshBtnActionPerformed

    private void declineBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_declineBtnActionPerformed
        // TODO add your handling code here:
        vs.updateOrderStatus(incomingOrdersTable, OrderStatus.DECLINED);
        populateIncomingOrdersTable();
        populateOrdersInProgressTable();
    }//GEN-LAST:event_declineBtnActionPerformed

    private void acceptBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acceptBtnActionPerformed
        // TODO add your handling code here:
        vs.updateOrderStatus(incomingOrdersTable, OrderStatus.IN_PROGRESS);
        populateIncomingOrdersTable();
        populateOrdersInProgressTable();
    }//GEN-LAST:event_acceptBtnActionPerformed

    private void readyBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readyBtnActionPerformed
        // TODO add your handling code here:
        vs.updateOrderStatus(ordersInProgressTable, OrderStatus.ACCEPTED);
        populateIncomingOrdersTable();
        populateOrdersInProgressTable();
        populateOrderHistoryTable();
        populateRevenueDashboard();
    }//GEN-LAST:event_readyBtnActionPerformed

    private void declineBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_declineBtn1ActionPerformed
        // TODO add your handling code here:
        vs.updateOrderStatus(ordersInProgressTable, OrderStatus.DECLINED);
        populateIncomingOrdersTable();
        populateOrdersInProgressTable();
    }//GEN-LAST:event_declineBtn1ActionPerformed

    private void orderHistoryRefreshBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orderHistoryRefreshBtn1ActionPerformed
        // TODO add your handling code here:
        populateOrderHistoryTable();
    }//GEN-LAST:event_orderHistoryRefreshBtn1ActionPerformed

    private void readNotificationBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readNotificationBtnActionPerformed
        // TODO add your handling code here:
        vs.updateNotificationStatus(notificationsTable, NotificationStatus.NOTIFIED);
        populateNotificationsTable();
    }//GEN-LAST:event_readNotificationBtnActionPerformed

    private void notificationsRefreshBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_notificationsRefreshBtnActionPerformed
        // TODO add your handling code here:
        populateNotificationsTable();
    }//GEN-LAST:event_notificationsRefreshBtnActionPerformed

    private void yearBeforeCmbBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yearBeforeCmbBoxActionPerformed
        // TODO add your handling code here:
        populateRevenueOrdersTable();
    }//GEN-LAST:event_yearBeforeCmbBoxActionPerformed

    private void monthBeforeCmbBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_monthBeforeCmbBoxActionPerformed
        // TODO add your handling code here:
        populateRevenueOrdersTable();
    }//GEN-LAST:event_monthBeforeCmbBoxActionPerformed

    private void dayBeforeCmbBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dayBeforeCmbBoxActionPerformed
        // TODO add your handling code here:
        populateRevenueOrdersTable();
    }//GEN-LAST:event_dayBeforeCmbBoxActionPerformed

    private void yearAfterCmbBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yearAfterCmbBoxActionPerformed
        // TODO add your handling code here:
        populateRevenueOrdersTable();
    }//GEN-LAST:event_yearAfterCmbBoxActionPerformed

    private void monthAfterCmbBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_monthAfterCmbBoxActionPerformed
        // TODO add your handling code here:
        populateRevenueOrdersTable();
    }//GEN-LAST:event_monthAfterCmbBoxActionPerformed

    private void dayAfterCmbBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dayAfterCmbBoxActionPerformed
        // TODO add your handling code here:
        populateRevenueOrdersTable();
    }//GEN-LAST:event_dayAfterCmbBoxActionPerformed

   
    
    
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
        /* Set the FlatLaf look and feel */
        try {
            javax.swing.UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VendorForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VendorForm(new User(3, "Jack Kwan", "jack@picante.apu.edu.my", "qweqweqwe".toCharArray(), "vendor")).setVisible(true);
            }
        });
    }
  
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddBtn;
    private javax.swing.JButton acceptBtn;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JComboBox<Integer> dayAfterCmbBox;
    private javax.swing.JComboBox<Integer> dayBeforeCmbBox;
    private javax.swing.JLabel dayRevenueLabel;
    private javax.swing.JButton declineBtn;
    private javax.swing.JButton declineBtn1;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JButton homeNavBtn;
    private javax.swing.JPanel homePanel;
    private javax.swing.JTable incomingOrdersTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton menuBtn;
    private javax.swing.JLabel menuLabel;
    private javax.swing.JPanel menuPanel;
    private javax.swing.JTable menuTable;
    private javax.swing.JComboBox<Integer> monthAfterCmbBox;
    private javax.swing.JComboBox<Integer> monthBeforeCmbBox;
    private javax.swing.JLabel monthRevenueLabel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JButton notificationsBtn;
    private javax.swing.JLabel notificationsLabel;
    private javax.swing.JPanel notificationsPanel;
    private javax.swing.JButton notificationsRefreshBtn;
    private javax.swing.JTable notificationsTable;
    private javax.swing.JButton orderHistoryBtn;
    private javax.swing.JPanel orderHistoryPanel;
    private javax.swing.JButton orderHistoryRefreshBtn1;
    private javax.swing.JTable orderHistoryTable;
    private javax.swing.JButton ordersBtn;
    private javax.swing.JTable ordersInProgressTable;
    private javax.swing.JLabel ordersLabel;
    private javax.swing.JPanel ordersPanel;
    private javax.swing.JButton ordersRefreshBtn;
    private javax.swing.JButton readNotificationBtn;
    private javax.swing.JButton readyBtn;
    private javax.swing.JButton refreshBtn;
    private javax.swing.JButton removeBtn;
    private javax.swing.JButton revenueDashboardBtn;
    private javax.swing.JPanel revenueDashboardPanel;
    private javax.swing.JTable revenueOrdersTable;
    private javax.swing.JButton saveAllBtn;
    private javax.swing.JPanel sidePanel;
    private javax.swing.JPanel topBarPanel;
    private javax.swing.JLabel totalRevenueLabel;
    private javax.swing.JLabel vendorLabel;
    private javax.swing.JComboBox<Integer> yearAfterCmbBox;
    private javax.swing.JComboBox<Integer> yearBeforeCmbBox;
    private javax.swing.JLabel yearRevenueLabel;
    // End of variables declaration//GEN-END:variables
}
