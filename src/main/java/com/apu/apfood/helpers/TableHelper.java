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

    public void populateTable(List<String[]> data, JTable tableName, boolean isWithoutIndex) {
        DefaultTableModel model = (DefaultTableModel) tableName.getModel();
        model.setRowCount(0);

        for (String[] row : data) {
            model.addRow(row);
        }
    }

    // For 2D static array
    public void refreshTable(javax.swing.JTable jtable, Object[][] payload, String[] tableHeaders) {
        DefaultTableModel model = (DefaultTableModel) jtable.getModel();
        model.setDataVector(payload, tableHeaders);
        model.fireTableDataChanged();
    }

    // For list of array
    public void refreshTable(javax.swing.JTable jtable, List<String[]> payload, String[] tableHeaders) {

        Object[][] customerCreditDetails = new Object[payload.size()][2];
        for (int i = 0; i < payload.size(); i++) {
            customerCreditDetails[i] = payload.get(i);
        }
        DefaultTableModel model = (DefaultTableModel) jtable.getModel();
        model.setDataVector(customerCreditDetails, tableHeaders);
        model.fireTableDataChanged();
    }
}
