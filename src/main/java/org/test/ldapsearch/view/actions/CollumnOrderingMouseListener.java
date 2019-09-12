package org.test.ldapsearch.view.actions;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class CollumnOrderingMouseListener extends MouseAdapter{

    private final JTable table;
    private final DefaultTableModel model;
    private int lastColOrder = -1;
    private Point mousePoint;
    
    public CollumnOrderingMouseListener(JTable table){
        this.table = table;
        this.model = (DefaultTableModel) table.getModel();
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        if(!mousePoint.equals(e.getPoint())) return;
        int col = table.columnAtPoint(e.getPoint());
        col = table.convertColumnIndexToModel(col);
        if(e.getClickCount()==1) {
            orderByCol(col);
        } else if(e.getClickCount()==2) {
            autoResize(col);
        }
    }
    @Override
    public void mousePressed(MouseEvent e) {
        mousePoint = e.getPoint();
    }
    
    protected void autoResize(int col) {
        // TODO Auto-generated method stub
    }

    @SuppressWarnings("unchecked")
    private void orderByCol(int col) {
        boolean reverse = col == lastColOrder;
        Vector<Vector<String>> data = model.getDataVector();
        Collections.sort(data, (o1, o2) -> {
            String v1 = o1.get(col);
            String v2 = o2.get(col);
            if(reverse) {
                lastColOrder = -1;
                return v2.compareTo(v1);
            } else {
                lastColOrder = col;
                return v1.compareTo(v2);
            }
        });
        lastColOrder = reverse ? -1 : col;
        model.fireTableDataChanged();
    }
}
