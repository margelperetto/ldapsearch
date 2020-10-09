package org.test.ldapsearch.view.comps;

import static java.lang.Math.max;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

@SuppressWarnings("serial")
public class TextAreaCellRederer extends DefaultTableCellRenderer implements TableCellRenderer{
	
	private final JTextArea jta;
	
	public TextAreaCellRederer() {
		jta = new JTextArea();
		jta.setEditable(false);
	}

	@Override
	public Component getTableCellRendererComponent(
			JTable table, 
			Object value, 
			boolean isSelected, 
			boolean hasFocus,
			int row, int column) {
		
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		jta.setText(getText());
		jta.setBorder(getBorder());
		jta.setFont(getFont());
		jta.setForeground(getForeground());
		jta.setBackground(getBackground());
		
		table.setRowHeight(row, max(max(table.getRowHeight(), table.getRowHeight(row)), jta.getPreferredSize().height));
		
		return jta;
	}

}
