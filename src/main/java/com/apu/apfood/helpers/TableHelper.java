package com.apu.apfood.helpers;

import java.util.List;
import java.util.function.Function;
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

    // Generic method with index control
    public <T> void populateTable(List<T> data, JTable table, Function<T, Object[]> rowMapper, boolean hasIndex) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        int index = 1;
        
        for (T item : data) {
            Object[] rowData = rowMapper.apply(item);
            Object[] rowDataWithIndex;
            if (hasIndex) {
                rowDataWithIndex = new Object[rowData.length + 1];
                rowDataWithIndex[0] = index++;
                System.arraycopy(rowData, 0, rowDataWithIndex, 1, rowData.length);
            } else {
                rowDataWithIndex = rowData;
            }
            model.addRow(rowDataWithIndex);
        }
    }

    // Overloaded generic method assuming no index
    public <T> void populateTable(List<T> data, JTable table, Function<T, Object[]> rowMapper) {
        populateTable(data, table, rowMapper, false);
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
