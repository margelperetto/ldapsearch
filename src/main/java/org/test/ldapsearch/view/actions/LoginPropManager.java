package org.test.ldapsearch.view.actions;

import java.util.Base64;

import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.test.ldapsearch.storage.PropInfos;
import org.test.ldapsearch.storage.PropertiesStorage;

public class LoginPropManager {
    
    private final JTextField jtfUrl;
    private final JTextField jtfUser;
    private final JPasswordField jtfPass;

    public LoginPropManager(JTextField jtfUrl, JTextField jtfUser, JPasswordField jtfPass) {
        this.jtfUrl = jtfUrl;
        this.jtfUser = jtfUser;
        this.jtfPass = jtfPass;
    }

    public void loadProperties() {
        try {
            setProp(jtfUrl, PropInfos.URL);
            setProp(jtfUser, PropInfos.USER);
            
            String pass = PropertiesStorage.getInstance().getProp(PropInfos.PASS);
            if(pass!=null && !pass.trim().isEmpty()){
                jtfPass.setText(new String(Base64.getDecoder().decode(pass.trim())));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void setProp(JTextField jtf, PropInfos prop) {
        String value = PropertiesStorage.getInstance().getProp(prop);
        if(value!=null && !value.trim().isEmpty()){
            jtf.setText(value.trim());
        }
    }

    public void saveProperties() {
        try {
            PropertiesStorage.getInstance().setProp(PropInfos.URL, jtfUrl.getText());
            PropertiesStorage.getInstance().setProp(PropInfos.USER, jtfUser.getText());
            String plain = String.valueOf(jtfPass.getPassword());
            String encoded = Base64.getEncoder().encodeToString(plain.getBytes());
            PropertiesStorage.getInstance().setProp(PropInfos.PASS, encoded);
            PropertiesStorage.getInstance().saveProperties();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
