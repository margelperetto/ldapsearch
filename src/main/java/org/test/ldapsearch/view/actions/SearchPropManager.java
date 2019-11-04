package org.test.ldapsearch.view.actions;

import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.test.ldapsearch.storage.PropInfos;
import org.test.ldapsearch.storage.PropertiesStorage;

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

    private void setProp(JTextComponent jtf, PropInfos prop, String defaultValue) {
        String value = PropertiesStorage.getInstance().getProp(prop);
        if(value!=null && !value.trim().isEmpty()){
            jtf.setText(value.trim());
        } else {
            jtf.setText(defaultValue);
        }
    }

    public void saveProperties() {
        try {
            PropertiesStorage.getInstance().setProp(PropInfos.PATH, jtfPath.getText());
            PropertiesStorage.getInstance().setProp(PropInfos.FILTER, jtaFilter.getText());
            PropertiesStorage.getInstance().setProp(PropInfos.LIMIT, spLimit.getValue().toString());
            PropertiesStorage.getInstance().setPropArray(PropInfos.ATTRIBUTES, attrManager.getAttributes());
            PropertiesStorage.getInstance().saveProperties();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void loadProperties() {
        try {
            setProp(jtfPath, PropInfos.PATH, "DC=contoso,DC=local");
            setProp(jtaFilter, PropInfos.FILTER, "(&(displayName=*)(objectClass=User))");
            spLimit.setValue(PropertiesStorage.getInstance().getPropInt(PropInfos.LIMIT, 1000));
            attrManager.clear();
            for(String row : PropertiesStorage.getInstance().getPropArray(PropInfos.ATTRIBUTES)) {
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
