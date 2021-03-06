package org.test.ldapsearch.view;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CancellationException;

import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.test.ldapsearch.RuntimeSearch;
import org.test.ldapsearch.utils.LDAPSearchUtils;
import org.test.ldapsearch.view.actions.AttributesManager;
import org.test.ldapsearch.view.actions.CollumnOrderingMouseListener;
import org.test.ldapsearch.view.actions.SearchPropManager;
import org.test.ldapsearch.view.comps.TextAreaCellEditor;
import org.test.ldapsearch.view.comps.TextAreaCellRederer;
import org.test.ldapsearch.view.comps.UndoRedoInstaller;
import org.test.ldapsearch.view.comps.search.PanelSearchInResults;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class PanelSearch extends JPanel{

	private JTextField jtfPath = new JTextField();
	private JTextArea jtaFilter = new JTextArea();
	private JSpinner spPageSize = new JSpinner(new SpinnerNumberModel(0, 0, 999999, 1));
	private JButton btnSearch = new JButton("Search!");
	private JButton btnCancel = new JButton("X");
	private JButton btnAdd = new JButton("Add+");
	private JButton btnUp = new JButton("Up↑");
	private JButton btnDown = new JButton("Down↓");
	private JButton btnRemove = new JButton("Remove-");
	private JLabel lbCount = new JLabel();
	private JLabel lbLastSearch = new JLabel();
	private DefaultTableModel tmResults = new DefaultTableModel();
	private JTable tableResults = new JTable(tmResults);
	private DefaultTableModel tmAttributes = new DefaultTableModel();
	private JTable tableAttributtes = new JTable(tmAttributes);
	private JTextArea jtaPlainText = new JTextArea();
	private PanelSearchInResults panelSearchInResults = new PanelSearchInResults(this);
	private AttributesManager attrManager = new AttributesManager(tableAttributtes);
	private SearchPropManager propManager = new SearchPropManager(jtfPath, jtaFilter, spPageSize, attrManager);

	private SwingWorker<List<SearchResult>, Integer> searchWorker;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private MainFrame frame;
	private LdapContext ctx;

	public PanelSearch(MainFrame frame) {
		this.frame = frame;

		initComponents();

		JPanel panelPathAndFilter = createPanelPathAndFilter();
		JPanel panelAttrs = createAttributePanel();

		JSplitPane splitTop = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelAttrs, panelPathAndFilter);
		splitTop.setBorder(new EmptyBorder(0, 0, 0, 0));

		JPanel panelResults = createPanelResults();

		JSplitPane splitCenter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitTop, panelResults);
		splitCenter.setBorder(new EmptyBorder(0, 0, 0, 0));

		JPanel panelFooter = createFooter();

		setLayout(new BorderLayout(0, 0));
		add(splitCenter, BorderLayout.CENTER);
		add(panelFooter, BorderLayout.SOUTH);
	}

	private void initComponents() {
		
		jtaFilter.setWrapStyleWord(true);
		jtaFilter.setLineWrap(true);
		jtaFilter.setFont(jtfPath.getFont());
		
		tmAttributes.addColumn("Attributes");

		btnSearch.addActionListener(evt->search());
		btnSearch.setFont(btnSearch.getFont().deriveFont(Font.BOLD));
		
		btnCancel.addActionListener(evt->cancelCurrentSearch());
		btnCancel.setForeground(Color.RED.darker());
		btnCancel.setFont(btnSearch.getFont());
		btnCancel.setVisible(false);
		btnCancel.setToolTipText("Cancel");

		btnAdd.addActionListener(evt->attrManager.add());
		btnRemove.addActionListener(evt->attrManager.removeSelecteds());
		btnUp.addActionListener(evt->attrManager.moveSelecteds(true));
		btnDown.addActionListener(evt->attrManager.moveSelecteds(false));

		tableResults.setDefaultEditor(Object.class, new TextAreaCellEditor());
		tableResults.setDefaultRenderer(Object.class, new TextAreaCellRederer());
		tableResults.getTableHeader().addMouseListener(new CollumnOrderingMouseListener(tableResults));

		jtaPlainText.setEditable(false);
		jtaPlainText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
		jtaPlainText.setBorder(new EmptyBorder(0, 0, 0, 0));

		spPageSize.setEditor(new JSpinner.NumberEditor(spPageSize, "0"));
		
		UndoRedoInstaller.install(jtaFilter);
		UndoRedoInstaller.install(jtfPath);
	}

	private JPanel createPanelPathAndFilter() {

		JLabel lbPath = new JLabel("Path");
		lbPath.setVerticalAlignment(JLabel.BOTTOM);

		JPanel panelPage = new JPanel(new MigLayout(new LC().fillX().insets("0","0","0","0")));
		panelPage.add(new JLabel("Page Size"), new CC());
		panelPage.add(spPageSize, new CC());

		JPanel panelFilterPath = new JPanel(new MigLayout(new LC().fillX().insetsAll("5")));
		panelFilterPath.add(lbPath, new CC().height(btnAdd.getPreferredSize().getHeight()+"!").spanX().wrap());
		panelFilterPath.add(jtfPath, new CC().width("400:100%:").spanX().wrap());
		panelFilterPath.add(new JLabel("Filter"), new CC().spanX());

		JScrollPane scrollFilter = new JScrollPane(jtaFilter);
		scrollFilter.setBorder(jtfPath.getBorder());

		JPanel panelFilterScroll = new JPanel(new BorderLayout());
		panelFilterScroll.setBorder(new EmptyBorder(0, 5, 0, 5));
		panelFilterScroll.add(scrollFilter);
		panelFilterScroll.setMinimumSize(new Dimension(0, jtfPath.getPreferredSize().height*3));

		JPanel panelFilterFooter = new JPanel(new MigLayout(new LC().fillX().insetsAll("5")));
		panelFilterFooter.add(btnSearch, new CC().alignY("bottom").split(2).spanX(2));
		panelFilterFooter.add(btnCancel, new CC().alignY("bottom"));
		panelFilterFooter.add(panelPage, new CC().alignX("right"));

		JPanel panelPathAndFilter = new JPanel(new BorderLayout(0, 0));
		panelPathAndFilter.add(panelFilterPath, BorderLayout.NORTH);
		panelPathAndFilter.add(panelFilterScroll, BorderLayout.CENTER);
		panelPathAndFilter.add(panelFilterFooter, BorderLayout.SOUTH);

		return panelPathAndFilter;
	}

	private JPanel createAttributePanel() {

		JPanel panelAttrsBtns = new JPanel(new MigLayout(new LC().fillX().insetsAll("5")));
		panelAttrsBtns.add(btnAdd, new CC().split(2));
		panelAttrsBtns.add(btnRemove, new CC().gapRight("10"));
		panelAttrsBtns.add(btnUp, new CC().alignX("right").split().spanX());
		panelAttrsBtns.add(btnDown, new CC().alignX("right"));

		JPanel panelAttrsTable = new JPanel(new BorderLayout());
		panelAttrsTable.setBorder(new EmptyBorder(0, 5, 6, 5));
		panelAttrsTable.add(new JScrollPane(tableAttributtes));
		panelAttrsTable.setPreferredSize(new Dimension(0, 50));

		JPanel panelAttrs = new JPanel(new BorderLayout(0, 0));
		panelAttrs.add(panelAttrsBtns, BorderLayout.NORTH);
		panelAttrs.add(panelAttrsTable, BorderLayout.CENTER);

		return panelAttrs;
	}

	private JPanel createPanelResults() {
		JTabbedPane tabViewMode = new JTabbedPane(JTabbedPane.BOTTOM);
		tabViewMode.addTab("Table", new JScrollPane(tableResults));
		tabViewMode.addTab("Plain Text", new JScrollPane(jtaPlainText));
		tabViewMode.addChangeListener(evt->panelSearchInResults.setVisible(tabViewMode.getSelectedIndex()==0));

		JPanel panelResults = new JPanel(new BorderLayout());
		panelResults.setBorder(new EmptyBorder(5, 4, 0, 2));
		panelResults.add(tabViewMode);

		return panelResults;
	}
	
	private JPanel createFooter() {
		JPanel panelFooter = new JPanel(new MigLayout(new LC().fillX().insetsAll("5")));
		panelFooter.add(panelSearchInResults, new CC().width("35%"));
		panelFooter.add(lbLastSearch, new CC().alignX("left").gapRight("15").split().spanX().alignX("right"));
		panelFooter.add(lbCount, new CC().alignX("right"));
		return panelFooter;
	}
	
	public void reload() {
		propManager.loadProperties();
		UndoRedoInstaller.discardAllEdits();
	}

	public void setLdapContext(LdapContext ctx) {
		this.ctx = ctx;
	}
	
	private void cancelCurrentSearch() {
		if(searchWorker!=null && !searchWorker.isCancelled() && !searchWorker.isDone()) {
			searchWorker.cancel(true);
		}
	}

	private void search() {
		if(ctx==null) {
			showMessageDialog(null, "Not connected!", "Error", ERROR_MESSAGE);
			return;
		}
		String path = jtfPath.getText();
		String filter = jtaFilter.getText().replaceAll("\n", "");
		String[] attributes = attrManager.getAttributes();
		int limit = (int) spPageSize.getValue();

		btnSearch.setText("Searching...");
		btnSearch.setEnabled(false);
		btnCancel.setVisible(true);

		searchWorker = new SwingWorker<List<SearchResult>, Integer>() {
			@Override
			protected List<SearchResult> doInBackground() throws Exception {
				return LDAPSearchUtils.getSearchResults(ctx, path , filter, attributes, limit, this::publish);
			}
			@Override
			protected void process(List<Integer> chunks) {
				btnSearch.setText("Searching... ("+chunks.get(chunks.size()-1)+" results)");
			}
			@Override
			protected void done() {
				try {
					showResults(get());
					propManager.saveProperties();
				} catch (CancellationException e) {
					JOptionPane.showMessageDialog(PanelSearch.this, "Operation canceled!", "Cancel", JOptionPane.WARNING_MESSAGE);
				} catch (Exception e) {
					new ErrorModal(frame, e.getMessage(), e.getCause()!=null?e.getCause():e).setVisible(true);
				} finally {
					btnSearch.setText("Search!");
					btnSearch.setEnabled(true);
					btnSearch.requestFocus();
					btnCancel.setVisible(false);
				}
			}
		};
		searchWorker.execute();
	}

	public void clearData() {
		tmResults.setDataVector(new Object[0][0], new Object[0]);
		jtaPlainText.setText("");
		updateCounter();
	}

	private void updateCounter() {
		lbCount.setText("<html><b>Count:</b> "+tableResults.getRowCount()+" </html>");
		lbLastSearch.setText("<html><b>Last search:</b> "+sdf.format(new Date())+" </html>");
		revalidate();
	}

	private void showResults(List<SearchResult> results) {
		try {
			String[] attributes = attrManager.getAttributes();
			String[][] data = LDAPSearchUtils.getData(results, attributes);
			tmResults.setDataVector(data, attributes);
			showPlainTextResults(data, attributes);
		}catch (Exception e) {
			throw new RuntimeException("Error show results! "+e.getMessage(), e);
		} finally {
			updateCounter();
		}
	}

	private void showPlainTextResults(String[][] data, String[] attributes) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			String utf8 = StandardCharsets.UTF_8.name();
			try (PrintStream ps = new PrintStream(baos, true, utf8)) {
				RuntimeSearch.showResults(ps, data, attributes);
			}
			String strData = baos.toString(utf8);
			jtaPlainText.setText(strData);
			jtaPlainText.setCaretPosition(0);
		} catch (Exception e) {
			throw new RuntimeException("Error show plain text results! "+e.getMessage(), e);
		}
	}

	public void save() {
		propManager.saveProperties();
	}

	public JTable getTableResults() {
		return tableResults;
	}

}
