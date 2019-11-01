package org.test.ldapsearch.view;

import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import org.test.ldapsearch.utils.PropertiesUtils;

@SuppressWarnings("serial")
public class MenuBar extends JMenuBar{

    private MainFrame mainFrame;
    private JMenu menuFile = new JMenu("File");

    public MenuBar(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    
        menuFile.add(menuItem("New", evt->newConfig()));
        menuFile.add(menuItem("Open", evt->open()));
        menuFile.add(menuItem("Save", evt->save()));
        menuFile.add(menuItem("Save As...", evt->saveAs()));
        menuFile.add(new JSeparator());
        loadRecentFiles();
        menuFile.add(new JSeparator());
        menuFile.add(menuItem("Clear Recent Files List", evt->clearRecentFiles()));
        
        add(menuFile);
    }
    
    private void newConfig() {
        // TODO Auto-generated method stub
    }
    
    private void openFile(String fileName) {
        // TODO Auto-generated method stub
    }
    
    private void open() {
        // TODO Auto-generated method stub
    }
    
    private void save() {
        // TODO Auto-generated method stub
    }
    
    private void saveAs() {
        // TODO Auto-generated method stub
    }
    
    private void clearRecentFiles() {
        // TODO Auto-generated method stub
    }

    private void loadRecentFiles() {
        List<String> files = Arrays.asList(PropertiesUtils.PROP_FILE);
        int i = 1;
        for (String f : files) {
            menuFile.add(menuItem((i++)+": "+f, evt->openFile(f)));
        }
    }

    private JMenuItem menuItem(String text, ActionListener action) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.addActionListener(action);
        return menuItem;
    }
    
}
