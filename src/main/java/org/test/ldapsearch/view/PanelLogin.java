package org.test.ldapsearch.view;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.util.function.Consumer;

import javax.naming.ldap.LdapContext;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;

import org.test.ldapsearch.utils.LDAPSearchUtils;
import org.test.ldapsearch.view.actions.LoginPropManager;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class PanelLogin extends JPanel{

    private JTextField jtfUrl = new JTextField("ldap://contoso.local");
    private JTextField jtfUser = new JTextField("administrator");
    private JPasswordField jtfPass = new JPasswordField("con");
    private JCheckBox ckbForceSSL = new JCheckBox("Force SSL");
    private JButton btnLogin = new JButton("Connect");
    private MainFrame frame;
    private Consumer<LdapContext> consumer;
    private LoginPropManager propManager = new LoginPropManager(jtfUrl, jtfUser, jtfPass);
    
    public PanelLogin(MainFrame frame, Consumer<LdapContext> consumer) {
        this.frame = frame;
        this.consumer = consumer;
        
        jtfUrl.setFont(jtfUrl.getFont().deriveFont(18f));
        jtfUser.setFont(jtfUser.getFont().deriveFont(18f));
        jtfPass.setFont(jtfPass.getFont().deriveFont(18f));
        btnLogin.setFont(btnLogin.getFont().deriveFont(18f));
        
        btnLogin.addActionListener(evt->login());
        jtfPass.addActionListener(evt->login());
        ckbForceSSL.setToolTipText("LDAPS protocol uses SSL by default");
        
        JPanel panel = new JPanel(new MigLayout(new LC().wrapAfter(1).insetsAll("15").fill()));
        panel.setBorder(new LineBorder(Color.GRAY));
        panel.add(new JLabel("LDAP URL"));
        panel.add(jtfUrl, new CC().growX().gapBottom("15").minWidth("350"));
        panel.add(new JLabel("USER"));
        panel.add(jtfUser, new CC().growX().gapBottom("15"));
        panel.add(new JLabel("PASSWORD"));
        panel.add(jtfPass, new CC().growX().gapBottom("15"));
        panel.add(ckbForceSSL, new CC().growX());
        panel.add(btnLogin, new CC().alignX("center"));
        
        setOpaque(true);
        setBackground(Color.LIGHT_GRAY);
        setLayout(new GridBagLayout());
        add(panel);
        
        propManager.loadProperties();
    }
    
    private void login() {
        setAllEnabled(false);
        String ldapURL = jtfUrl.getText();
        String userName = jtfUser.getText();
        String userPassword = String.valueOf(jtfPass.getPassword());
        boolean forceSSL = ckbForceSSL.isSelected();
        new SwingWorker<LdapContext, Void>() {
            @Override
            protected LdapContext doInBackground() throws Exception {
                return LDAPSearchUtils.connect(ldapURL, userName, userPassword, forceSSL);
            }
            @Override
            protected void done() {
                try {
                    consumer.accept(get());
                    propManager.saveProperties();
                } catch (Exception e) {
                    new ErrorModal(frame, "Error open connection! "+ldapURL+" - "+userName, e.getCause()!=null?e.getCause():e).setVisible(true);
                } finally {
                    setAllEnabled(true);
                }
            }
        }.execute();
    }
    
    private void setAllEnabled(boolean b) {
       jtfUrl.setEnabled(b);
       jtfUser.setEnabled(b);
       jtfPass.setEnabled(b);
       btnLogin.setEnabled(b);
       ckbForceSSL.setEnabled(b);
       btnLogin.setText(b?"Connect":"Connecting...");
    }

    public String getURL() {
        return jtfUrl.getText();
    }
    
    public String getUser() {
        return jtfUser.getText();
    }
    
}
