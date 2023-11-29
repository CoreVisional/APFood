//package com.apu.apfood.helpers;
//
//import java.awt.Component;
//import java.awt.Cursor;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
//import javax.swing.AbstractCellEditor;
//import javax.swing.Action;
//import javax.swing.Icon;
//import javax.swing.JButton;
//import javax.swing.JTable;
//import javax.swing.UIManager;
//import javax.swing.table.TableCellEditor;
//import javax.swing.table.TableCellRenderer;
//import javax.swing.table.TableColumnModel;
//
///**
// * Helper class to create a button in a table cell.
// * @author Alex
// */
//public class TableButtonHelper extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener, MouseListener {
//    private final JTable table;
//    private final Action action;
//    private JButton renderButton;
//    private JButton editButton;
//    private Object editorValue;
//    private boolean isButtonColumnEditor;
//
//    /**
//     * Constructor to set up the button in the table.
//     * @param table the JTable to add the button to.
//     * @param action the action to be performed on button click.
//     */
//    public TableButtonHelper(JTable table, Action action) {
//        this.table = table;
//        this.action = action;
//        
//        setUpButton();
//    }
//    
//    public void initialize(int column) {
//        TableColumnModel columnModel = table.getColumnModel();
//        columnModel.getColumn(column).setCellRenderer(this);
//        columnModel.getColumn(column).setCellEditor(this);
//        table.addMouseListener(this);
//    }
//
//    private void setUpButton() {
//        renderButton = createButton();
//        editButton = createButton();
//        editButton.addActionListener(this);
//    }
//
//    private JButton createButton() {
//        JButton button = new JButton();
//        button.setFocusPainted(false);
//        return button;
//    }
//
//    @Override
//    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
//        setButtonProperties(editButton, value, isSelected);
//        this.editorValue = value;
//        return editButton;
//    }
//
//    @Override
//    public Object getCellEditorValue() {
//        return editorValue;
//    }
//
//    @Override
//    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//        setButtonProperties(renderButton, value, isSelected);
//        return renderButton;
//    }
//
//    private void setButtonProperties(JButton button, Object value, boolean isSelected) {
//        if (isSelected) {
//            button.setForeground(table.getSelectionForeground());
//            button.setBackground(table.getSelectionBackground());
//        } else {
//            button.setForeground(table.getForeground());
//            button.setBackground(UIManager.getColor("Button.background"));
//        }
//        
//        if (value instanceof Icon icon) {
//            button.setText("");
//            button.setIcon(icon);
//        } else {
//            button.setText(value == null ? "" : value.toString());
//            button.setIcon(null);
//        }
//    }
//
//    @Override
//    public void actionPerformed(ActionEvent e) {
//        int row = table.convertRowIndexToModel(table.getEditingRow());
//        fireEditingStopped();
//        ActionEvent event = new ActionEvent(table, ActionEvent.ACTION_PERFORMED, "" + row);
//        action.actionPerformed(event);
//    }
//
//    @Override
//    public void mousePressed(MouseEvent e) {
//        if (table.isEditing() && table.getCellEditor() == this) {
//            isButtonColumnEditor = true;
//        }
//    }
//
//    @Override
//    public void mouseReleased(MouseEvent e) {
//        if (isButtonColumnEditor && table.isEditing()) {
//            table.getCellEditor().stopCellEditing();
//        }
//        isButtonColumnEditor = false;
//    }
//
//    // Empty implementations for other MouseListener methods
//    @Override public void mouseClicked(MouseEvent e) {}
//
//    @Override public void mouseEntered(MouseEvent e) {
//        int column = table.getColumnModel().getColumnIndexAtX(e.getX());
//        int row = e.getY() / table.getRowHeight();
//
//        if (row < table.getRowCount() && row >= 0 && column < table.getColumnCount() && column >= 0) {
//            table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//        }
//    }
//
//    @Override public void mouseExited(MouseEvent e) {
//        table.setCursor(Cursor.getDefaultCursor());
//    }
//}
