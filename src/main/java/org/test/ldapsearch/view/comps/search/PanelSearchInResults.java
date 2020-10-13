package org.test.ldapsearch.view.comps.search;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.test.ldapsearch.view.PanelSearch;
import org.test.ldapsearch.view.comps.AppIcons;
import org.test.ldapsearch.view.comps.PlaceHolderTextField;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class PanelSearchInResults extends JPanel{

	private JTextField tfSearch = new PlaceHolderTextField("Search in results");
	private JButton btnPrev = new JButton("<<");
	private JButton btnNext = new JButton(">>");
	private JLabel lbCount = new JLabel("No results");
	private JLabel lbIcon = new JLabel(AppIcons.SEARCH.icon());
	private final PanelSearch panelSearch;
	private IndexedData indexedData;
	
	public PanelSearchInResults(PanelSearch panelSearch) {
		this.panelSearch = panelSearch;
		panelSearch.getTableResults().getModel().addTableModelListener(evt->clear());
		lbCount.setFont(lbCount.getFont().deriveFont(Font.BOLD));
		
		setLayout(new MigLayout(new LC().fillX().insetsAll("0")));
		add(lbIcon, new CC().growY());
		add(tfSearch, new CC().growY().width("200:100%:"));
		add(btnPrev, new CC());
		add(btnNext, new CC());
		add(lbCount, new CC().width("150").growY());
		
		tfSearch.addKeyListener(new SearchDelayKeyListener(this));
		btnNext.addActionListener(evt->indexedData.next());
		btnPrev.addActionListener(evt->indexedData.previous());
		clear();
	}
	
	public void clear() {
		tfSearch.setText("");
		setIndexedData(new IndexedData());
	}
	
	public void search() {
		try { 
			setIndexedData(index());
			btnNext.doClick();
		} catch (Exception e) {
			System.err.println("Unable to search in results!");
			setIndexedData(new IndexedData());
		} 
	}
	
	private void setIndexedData(IndexedData indexedData) {
		this.indexedData = indexedData;
		
		if(tfSearch.getText().isEmpty()) {
			lbCount.setText("");
		} else if(indexedData.length()==0) {
			lbCount.setForeground(Color.RED.darker());
			lbCount.setText("No results");
		} else {
			lbCount.setForeground(Color.GREEN.darker());
			lbCount.setText(indexedData.length()+" result"+(indexedData.length()>1?"s":""));
		}
	}

	private IndexedData index() {
		String text = tfSearch.getText();
		if(text.isEmpty()) {
			return new IndexedData();
		}
		JTable table = panelSearch.getTableResults();
		Set<Integer> rowsSet = new HashSet<>();
		for (int r = 0; r < table.getRowCount(); r++) {
			for (int c = 0; c < table.getColumnCount(); c++) {
				Object value = table.getValueAt(r, c);
				if(value!=null && value.toString().toLowerCase().contains(text.toLowerCase())) {
					rowsSet.add(r);
					break;
				}
			}
		}
		List<Integer> rowsOrdered = new ArrayList<>(rowsSet);
		Collections.sort(rowsOrdered);
		return new IndexedData(text, rowsOrdered.toArray(new Integer[0])).consumer(this::selectTableRow);
	}
	
	private void selectTableRow(Integer row) {
		JTable table = panelSearch.getTableResults();
		table.clearSelection();
		table.addRowSelectionInterval(row, row);
		table.scrollRectToVisible(table.getCellRect(row, 1, true));
	}

	public IndexedData getIndexedData() {
		return indexedData;
	}

	public void setLoading(boolean l) {
		lbIcon.setIcon(l?AppIcons.LOADING.icon():AppIcons.SEARCH.icon());
	}
}
