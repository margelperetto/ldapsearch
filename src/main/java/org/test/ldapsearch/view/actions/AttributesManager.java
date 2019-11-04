package org.test.ldapsearch.view.actions;

import static javax.swing.JOptionPane.WARNING_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showInputDialog;
import static javax.swing.JOptionPane.showMessageDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class AttributesManager{
    
    private final DefaultTableModel tmAttributes;
    private final JTable tableAttributtes;
    
    public AttributesManager(JTable tableAttributtes) {
        this.tableAttributtes = tableAttributtes;
        this.tmAttributes = (DefaultTableModel) tableAttributtes.getModel();
    }

    public void moveSelecteds(boolean up) {
        int[] sels = tableAttributtes.getSelectedRows();
        if(sels.length == 0) {
            showMessageDialog(tableAttributtes.getParent(), "Selected an attribute!", "Atention", WARNING_MESSAGE);
            return;
        }
        if(moveAttrsBlocked(sels, up)) {
            return;
        }
        tableAttributtes.clearSelection();
        
        List<String> attrs = new ArrayList<>(Arrays.asList(getAttributes()));
        if(up) 
            moveUp(attrs, sels);
        else   
            moveDown(attrs, sels);
        
        updateAttributes(attrs);
    }
    
    private void updateAttributes(List<String> attrs) {
        for (int row = 0; row < attrs.size(); row++) {
            tmAttributes.setValueAt(attrs.get(row), row, 0);
        }
    }

    private boolean moveAttrsBlocked(int[] sels, boolean up) {
        return sels.length>1 && ((sels[0]==0 && up) || (sels[sels.length-1]==tableAttributtes.getRowCount()-1 && !up));
    }

    private void moveUp(List<String> attrs, int[] sels) {
        for (int row : sels) {
            moveAttr(attrs, row, row==0?attrs.size()-1:row-1);
        }
    }
    
    private void moveDown(List<String> attrs, int[] sels) {
        for (int i = sels.length-1; i >= 0; i--) {
            int row = sels[i];
            moveAttr(attrs, row, row==attrs.size()-1?0:row+1);
        }
    }

    private void moveAttr(List<String> attrs, int row, int newRow) {
        attrs.add(newRow, attrs.remove(row));
        tableAttributtes.addRowSelectionInterval(newRow, newRow);
    }
    
    public String[] getAttributes() {
        int size = tableAttributtes.getRowCount();
        String[] attrs = new String[size];
        for (int i = 0; i < size; i++) {
            attrs[i] = (String) tmAttributes.getValueAt(i, 0);
        }
        return attrs;
    }
    
    public void add() {
        String attr = showInputDialog(tableAttributtes.getParent(), "Attribute Name");
        if(attr!=null && !attr.trim().isEmpty()) {
            tmAttributes.addRow(new String[] {attr});
        }
    }
    
    public void removeSelecteds() {
        int[] selectedRows = tableAttributtes.getSelectedRows();
        if(selectedRows.length == 0) {
            showMessageDialog(tableAttributtes.getParent(), "Selected an attribute!", "Atention", WARNING_MESSAGE);
            return;
        }
        if(showConfirmDialog(tableAttributtes.getParent(), "Remove select attributes?", "Confirm", YES_NO_OPTION, WARNING_MESSAGE) == YES_OPTION) {
            for (int i = selectedRows.length-1; i >= 0 ; i--) {
                tmAttributes.removeRow(selectedRows[i]);
            }
        }
    }

    public void addRow(String row) {
        tmAttributes.addRow(new String[] {row});
    }

    public int getRowCount() {
        return tableAttributtes.getRowCount();
    }

    public void clear() {
        while(tmAttributes.getRowCount()>0) {
            tmAttributes.removeRow(0);
        }
    }
}
