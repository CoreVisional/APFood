package com.apu.apfood.helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
}
