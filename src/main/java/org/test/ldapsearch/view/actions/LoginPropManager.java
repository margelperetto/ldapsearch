package org.test.ldapsearch.view.actions;

import java.util.Base64;

import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.test.ldapsearch.utils.PropertiesUtils;
import org.test.ldapsearch.utils.PropertiesUtils.Prop;

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
            PropertiesUtils.loadProperties();
            setProp(jtfUrl, Prop.URL);
            setProp(jtfUser, Prop.USER);
            
            String pass = PropertiesUtils.getProp(Prop.PASS);
            if(pass!=null && !pass.trim().isEmpty()){
                jtfPass.setText(new String(Base64.getDecoder().decode(pass.trim())));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void setProp(JTextField jtf, Prop prop) {
        String value = PropertiesUtils.getProp(prop);
        if(value!=null && !value.trim().isEmpty()){
            jtf.setText(value.trim());
        }
    }

    public void saveProperties() {
        try {
            PropertiesUtils.setProp(Prop.URL, jtfUrl.getText());
            PropertiesUtils.setProp(Prop.USER, jtfUser.getText());
            String plain = String.valueOf(jtfPass.getPassword());
            String encoded = Base64.getEncoder().encodeToString(plain.getBytes());
            PropertiesUtils.setProp(Prop.PASS, encoded);
            PropertiesUtils.saveProperties();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
