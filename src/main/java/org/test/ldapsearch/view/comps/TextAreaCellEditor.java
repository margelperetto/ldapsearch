package org.test.ldapsearch.view.comps;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

@SuppressWarnings("serial")
public class TextAreaCellEditor extends DefaultCellEditor{
	
	public TextAreaCellEditor() {
		super(new JTextField());
		
		JTextArea jta = new JTextArea();
		jta.setEditable(false);
		jta.setBorder(new EmptyBorder(0, 0, 0, 0));
		jta.setFont(editorComponent.getFont());
		jta.setForeground(editorComponent.getForeground());
		jta.setBackground(editorComponent.getBackground());
		
        delegate = new EditorDelegate() {
        	@Override
            public void setValue(Object value) {
            	jta.setText((value != null) ? value.toString() : "");
            }
        	@Override
            public Object getCellEditorValue() {
                return jta.getText();
            }
        };
        JScrollPane scroll = new JScrollPane(jta);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        editorComponent = scroll;
	}
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		super.getTableCellEditorComponent(table, value, isSelected, row, column);
		
		TableCellRenderer renderer = table.getCellRenderer(row, column);
        Component c = renderer.getTableCellRendererComponent(table, value, isSelected, true, row, column);
        if (c instanceof JComponent) {
        	editorComponent.setBorder(((JComponent)c).getBorder());
        }
		return editorComponent;
	}
}