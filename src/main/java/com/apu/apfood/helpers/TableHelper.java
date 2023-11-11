package com.apu.apfood.helpers;

import java.util.List;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Alex
 */
public class TableHelper {

    public void centerTableValues(JTable tableName) {

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tableName.setDefaultRenderer(String.class, centerRenderer);
        tableName.setDefaultRenderer(Integer.class, centerRenderer);
        tableName.setDefaultRenderer(Object.class, centerRenderer);
    }

    public void populateTable(List<String[]> data, JTable tableName) {
        DefaultTableModel model = (DefaultTableModel) tableName.getModel();
        model.setRowCount(0);

        int index = 1;

        for (String[] row : data) {
            String[] rowDataWithIndex = new String[row.length + 1];
            rowDataWithIndex[0] = String.valueOf(index++);
            System.arraycopy(row, 0, rowDataWithIndex, 1, row.length);
            model.addRow(rowDataWithIndex);
        }
    }

    public void refreshTable(javax.swing.JTable jtable, Object[][] deliveryHistory) {
        DefaultTableModel model = (DefaultTableModel) jtable.getModel();
        model.setDataVector(deliveryHistory, new String[]{
            "Delivery ID", "Order ID", "Customer Name", "Vendor", "Location", "Date", "Time", "DeliveryStatus"
        });
        model.fireTableDataChanged();
    }
}
