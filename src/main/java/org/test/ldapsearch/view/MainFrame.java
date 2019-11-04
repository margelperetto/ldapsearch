package org.test.ldapsearch.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.naming.ldap.LdapContext;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import org.test.ldapsearch.storage.ConfigFileStorage;
import org.test.ldapsearch.storage.PropFileConfig;
import org.test.ldapsearch.storage.PropertiesStorage;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class MainFrame extends JFrame{

    private JPanel panelCredentials = new JPanel();
    private JLabel lbCredentialsInfo = new JLabel();
    private JButton jbLogout = new JButton("Disconnect");
    private JPanel panelCenter;
    private PanelLogin panelLogin = new PanelLogin(this, this::setLdapContext);
    private PanelSearch panelSearch = new PanelSearch(this);
    private CardLayout card;

    private LdapContext ctx;

    public MainFrame() {
        super("LDAP Query Tool");

        jbLogout.addActionListener(evt->closeConnection());
        jbLogout.setVisible(false);
        jbLogout.setFocusable(false);
        jbLogout.setForeground(Color.RED);
        jbLogout.setFont(jbLogout.getFont().deriveFont(Font.BOLD));

        lbCredentialsInfo.setFont(lbCredentialsInfo.getFont().deriveFont(Font.BOLD));
        lbCredentialsInfo.setForeground(Color.DARK_GRAY);

        panelCredentials.setLayout(new MigLayout(new LC().insets("5", "0", "0", "0")));
        panelCredentials.add(jbLogout, new CC().gapLeft("5"));
        panelCredentials.add(lbCredentialsInfo, new CC().gapLeft("5").wrap());
        panelCredentials.add(new JSeparator(), new CC().spanX().width("100%"));

        card = new CardLayout();
        panelCenter = new JPanel(card);
        panelCenter.add(panelLogin, "LOGIN");
        panelCenter.add(panelSearch, "SEARCH");

        setLayout(new BorderLayout(0, 0));
        add(panelCredentials, BorderLayout.NORTH);
        add(panelCenter, BorderLayout.CENTER);

        pack();
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent evt) {
                closeConnection();
            }
        });

        setJMenuBar(new MenuBar(this));
        reload();
    }

    private void closeConnection() {
        try {
            if(ctx!=null) {
                ctx.close();
            }
            lbCredentialsInfo.setText("");
            jbLogout.setVisible(false);
            panelSearch.clearTableData();
            card.show(panelCenter, "LOGIN");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setLdapContext(LdapContext ctx) {
        this.ctx = ctx;
        lbCredentialsInfo.setText("<html>"+panelLogin.getURL()+" <font color=red>|</font> "+panelLogin.getUser()+"</html>");
        jbLogout.setVisible(true);
        panelSearch.setLdapContext(ctx);
        card.show(panelCenter, "SEARCH");
    }

    public void reload() {
        closeConnection();
        panelLogin.reload();
        panelSearch.reload();
        updateTitle();
    }

    public void save() {
        panelLogin.save();
        panelSearch.save();
    }

    public void updateTitle() {
        try {
            String configFile = ConfigFileStorage.getInstance().getProp(PropFileConfig.CONFIG_FILE);
            setTitle("LDAP Query Tool - "+(configFile==null?PropertiesStorage.DEFAULT_PROP_FILE:configFile));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}
