package org.test.ldapsearch.view;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import org.test.ldapsearch.utils.LDAPSearchUtils;
import org.test.ldapsearch.view.actions.AttributesManager;
import org.test.ldapsearch.view.actions.CollumnOrderingMouseListener;
import org.test.ldapsearch.view.actions.SearchPropManager;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class PanelSearch extends JPanel{
    
    private JTextField jtfPath = new JTextField();
    private JTextArea jtaFilter = new JTextArea();
    private JSpinner spPageSize = new JSpinner(new SpinnerNumberModel(0, 0, 999999, 1));
    private JButton btnSearch = new JButton("Search!");
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
    private AttributesManager attrManager = new AttributesManager(tableAttributtes);
    private SearchPropManager propManager = new SearchPropManager(jtfPath, jtaFilter, spPageSize, attrManager);
    
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private MainFrame frame;
    private LdapContext ctx;
    
    public PanelSearch(MainFrame frame) {
        this.frame = frame;
        
        JScrollPane scrollFilter = new JScrollPane(jtaFilter);
        jtaFilter.setWrapStyleWord(true);
        jtaFilter.setLineWrap(true);
        jtfPath.setFont(jtaFilter.getFont());
        jtfPath.setBorder(scrollFilter.getBorder());
        tmAttributes.addColumn("Atributes");
        
        btnSearch.addActionListener(evt->search());
        btnSearch.setFont(btnSearch.getFont().deriveFont(Font.BOLD));
        
        btnAdd.addActionListener(evt->attrManager.add());
        btnRemove.addActionListener(evt->attrManager.removeSelecteds());
        btnUp.addActionListener(evt->attrManager.moveSelecteds(true));
        btnDown.addActionListener(evt->attrManager.moveSelecteds(false));
        
        tableResults.getTableHeader().addMouseListener(new CollumnOrderingMouseListener(tableResults));
        
        spPageSize.setEditor(new JSpinner.NumberEditor(spPageSize, "0"));
        
        JLabel lbPath = new JLabel("Path");
        lbPath.setVerticalAlignment(JLabel.BOTTOM);
        
        JPanel pPage = new JPanel(new MigLayout(new LC().fillX().insets("0","0","5","0")));
        pPage.add(new JLabel("Page Size"), new CC());
        pPage.add(spPageSize, new CC());
        
        JPanel panelPathAndFilter = new JPanel(new MigLayout(new LC().fillX().insetsAll("0")));
        panelPathAndFilter.add(lbPath, new CC().height(btnAdd.getPreferredSize().getHeight()+"!").spanX().wrap());
        panelPathAndFilter.add(jtfPath, new CC().width("380:100%:").spanX().wrap());
        panelPathAndFilter.add(new JLabel("Filter"), new CC().spanX().wrap());
        panelPathAndFilter.add(scrollFilter, new CC().width("380:100%:").height("100%").grow().spanX().wrap());
        panelPathAndFilter.add(btnSearch, new CC().width("220::"));
        panelPathAndFilter.add(pPage, new CC().alignX("right"));
        
        JPanel panelAttrs = new JPanel(new MigLayout(new LC().noGrid().fillX().insetsAll("0")));
        panelAttrs.add(btnAdd);
        panelAttrs.add(btnRemove);
        panelAttrs.add(new JLabel(" "), new CC().width("0:100%:"));
        panelAttrs.add(btnUp);
        panelAttrs.add(btnDown, new CC().wrap());
        panelAttrs.add(new JScrollPane(tableAttributtes), new CC().spanX().grow().gapBottom("5"));
        
        JPanel panelCount = new JPanel(new MigLayout(new LC().fillX().insetsAll("0")));
        panelCount.add(lbLastSearch);
        panelCount.add(lbCount, new CC().alignX("right"));
        
        setLayout(new MigLayout(new LC().fill().gridGap("10","5")));
        add(panelPathAndFilter, new CC().grow().width("100%"));
        add(panelAttrs, new CC().grow().minWidth("300").wrap());
        add(new JScrollPane(tableResults), new CC().width("500:100%:").height("100:100%:").spanX().wrap());
        add(panelCount, new CC().spanX().growX());
    }
    
    public void reload() {
        propManager.loadProperties();
    }

    public void setLdapContext(LdapContext ctx) {
        this.ctx = ctx;
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
        new SwingWorker<List<SearchResult>, Integer>() {
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
                    List<SearchResult> results = get();
                    showResults(results);
                    propManager.saveProperties();
                } catch (Exception e) {
                    new ErrorModal(frame, e.getMessage(), e.getCause()!=null?e.getCause():e).setVisible(true);
                } finally {
                    btnSearch.setText("Search!");
                    btnSearch.setEnabled(true);
                    btnSearch.requestFocus();
                }
            }
        }.execute();
    }
    
    public void clearTableData() {
        tmResults.setDataVector(new Object[0][0], new Object[0]);
        updateCounter();
    }
    
    private void updateCounter() {
        lbCount.setText("<html><b>Count:</b> "+tableResults.getRowCount()+" </html>");
        lbLastSearch.setText("Last search: "+sdf.format(new Date()));
        revalidate();
    }

    private void showResults(List<SearchResult> results) {
        try {
            String[] attributes = attrManager.getAttributes();
            String[][] data = LDAPSearchUtils.getData(results, attributes);
            tmResults.setDataVector(data, attributes);
            updateCounter();
        }catch (Exception e) {
            throw new RuntimeException("Error show results! "+e.getMessage(), e);
        }
    }

    public void save() {
        propManager.saveProperties();
    }

}
