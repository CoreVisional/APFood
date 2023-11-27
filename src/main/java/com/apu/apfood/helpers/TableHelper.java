package com.apu.apfood.helpers;

import java.awt.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Alex
 */
public class TableHelper {

    public void centerTableValues(JTable tableName) {

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        tableName.setDefaultRenderer(String.class, centerRenderer);
        tableName.setDefaultRenderer(Integer.class, centerRenderer);
        tableName.setDefaultRenderer(Double.class, centerRenderer);
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
    
    /**
     * Generic method to create a map from a JTable.
     * 
     * @param <K> the type of keys maintained by the map
     * @param <V> the type of mapped values
     * @param table the JTable to extract data from
     * @param keyColumnIndex the column index for keys
     * @param valueColumnIndex the column index for values
     * @param keyMapper function to map table value to key type
     * @param valueMapper function to map table value to value type
     * @return a map containing keys and values extracted from the table
     */
    public <K, V> Map<K, V> createTableMap(JTable table, 
                                               int keyColumnIndex, 
                                               int valueColumnIndex, 
                                               Function<Object, K> keyMapper, 
                                               Function<Object, V> valueMapper) {
        
        Map<K, V> map = new HashMap<>();
        
        for (int i = 0; i < table.getRowCount(); i++) {
            K key = keyMapper.apply(table.getValueAt(i, keyColumnIndex));
            
            V value = valueMapper.apply(table.getValueAt(i, valueColumnIndex));
            
            map.put(key, value);
        }
        
        return map;
    }
    
    public void addRowinTable(JTable jtable, List<String> row)
    {
        DefaultTableModel model = (DefaultTableModel) jtable.getModel();
        int id = model.getRowCount()+1;
        Object[] obj = new Object[row.size()+1];
        obj[0] = String.valueOf(id);
        for (int i = 1; i <= row.size(); i++)
        {
            obj[i] = row.get(i-1);
        }
        model.addRow(obj); 
        //model.addRow(new Object[]{"Column 1", "Column 2", "Column 3"});

    }
    
    public void SetupTableSorter(JTable table)
    {
        centerTableValues(table);
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel());
        table.setRowSorter(sorter);
    }
    
    public void adjustColumnsToScalable(int[] scalableColumns, JTable table)
    {
        for (int column : scalableColumns)
        {
            table.getColumnModel().getColumn(column).setCellRenderer(new MultiLineCellRenderer());
            for (int row = 0; row < table.getRowCount(); row++) 
            {
                int rowHeight = calculateRowHeight(table, row, column);
                table.setRowHeight(row, rowHeight);
            }
        }
      
        table.setFillsViewportHeight(true);
        // Set the row height based on the content
        for (int row = 0; row < table.getRowCount(); row++) {
            for (int column : scalableColumns)
            {
                int rowHeight = calculateRowHeight(table, row, column);
                table.setRowHeight(row, rowHeight);
            }
            
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
    
    static class MultiLineCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            // Let the default renderer prepare the component
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // If the value contains newline characters, use HTML to display them
            if (value != null && value.toString().contains("\n")) {
                label.setText("<html>" + value.toString().replaceAll("\n", "<br/>") + "</html>");
            }

            return label;
        }
    }
    
    private static int calculateRowHeight(JTable table, int row, int column) {
        TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
        Component comp = table.prepareRenderer(cellRenderer, row, column);
        return Math.max(comp.getPreferredSize().height, table.getRowHeight(row));
    }
}
