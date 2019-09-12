package org.test.ldapsearch.view.actions;

import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.test.ldapsearch.view.PropertiesUtils;
import org.test.ldapsearch.view.PropertiesUtils.Prop;

public class SearchPropManager {
    
    private final JTextField jtfPath;
    private final JTextArea jtaFilter;
    private final JSpinner spLimit;
    private final AttributesManager attrManager;

    public SearchPropManager(JTextField jtfPath, JTextArea jtaFilter, JSpinner spLimit, AttributesManager attrManager) {
        this.jtfPath = jtfPath;
        this.jtaFilter = jtaFilter;
        this.spLimit = spLimit;
        this.attrManager = attrManager;
    }

    private void setProp(JTextComponent jtf, Prop prop, String defaultValue) {
        String value = PropertiesUtils.getProp(prop);
        if(value!=null && !value.trim().isEmpty()){
            jtf.setText(value.trim());
        } else {
            jtf.setText(defaultValue);
        }
    }

    public void saveProperties() {
        try {
            PropertiesUtils.setProp(Prop.PATH, jtfPath.getText());
            PropertiesUtils.setProp(Prop.FILTER, jtaFilter.getText());
            PropertiesUtils.setProp(Prop.LIMIT, spLimit.getValue().toString());
            PropertiesUtils.setPropArray(Prop.ATTRIBUTES, attrManager.getAttributes());
            PropertiesUtils.saveProperties();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void loadProperties() {
        try {
            PropertiesUtils.loadProperties();
            setProp(jtfPath, Prop.PATH, "DC=contoso,DC=local");
            setProp(jtaFilter, Prop.FILTER, "(&(displayName=*)(objectClass=User))");
            spLimit.setValue(PropertiesUtils.getPropInt(Prop.LIMIT, 1000));
            for(String row : PropertiesUtils.getPropArray(Prop.ATTRIBUTES)) {
                attrManager.addRow(row);
            }
            if(attrManager.getRowCount()==0) {
                attrManager.addRow("displayName");
                attrManager.addRow("mail");
                attrManager.addRow("samAccountName");
                attrManager.addRow("department");
                attrManager.addRow("title");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
