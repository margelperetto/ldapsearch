package org.test.ldapsearch.view;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;

import org.test.ldapsearch.storage.ConfigFileStorage;
import org.test.ldapsearch.storage.PropFileConfig;
import org.test.ldapsearch.storage.PropertiesStorage;

@SuppressWarnings("serial")
public class MenuBar extends JMenuBar{

    private MainFrame mainFrame;
    private JMenu menuFile = new JMenu("File   ");
    private JMenu menuHelp = new JMenu("Help   ");
    private Set<String> filesHistory;

    public MenuBar(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        
        reloadMenuFileItens();
        add(menuFile);
        
        menuHelp.add(menuItem("About LDAP Query Tool", evt->showAbout()));
        add(menuHelp);
    }
    
    private void reloadMenuFileItens() {
        menuFile.removeAll();
        menuFile.add(menuItem("New", evt->newConfig()));
        menuFile.add(menuItem("Open", evt->open()));
        menuFile.add(menuItem("Save", evt->save()));
        menuFile.add(menuItem("Save As...", evt->saveAs()));
        menuFile.add(new JSeparator());
        loadRecentFiles();
        menuFile.add(new JSeparator());
        menuFile.add(menuItem("Clear Recent Files List", evt->clearRecentFiles()));
        menuFile.add(menuItem("Open Instalation Folder", evt->openInstallFolder()));        
    }

    private void newConfig() {
        try {
            JFileChooser chooser = new JFileChooser(PropertiesStorage.DEFAULT_PROP_FILE);
            chooser.setDialogType(JFileChooser.SAVE_DIALOG);
            setCurrentConfigFile(chooser);
            int opt = chooser.showSaveDialog(mainFrame);
            if(opt == JFileChooser.APPROVE_OPTION) {
                File newFile = chooser.getSelectedFile();
                if(newFile.exists() && overrideConfirm()) {
                    Files.delete(newFile.toPath());
                }
                String file = newFile.getAbsolutePath();
                openConfigFile(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean overrideConfirm() {
        return JOptionPane.showConfirmDialog(mainFrame, "File already exists! \nDo you want overwrite?", "Warning", 
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }
    
    private boolean clearHistoryConfirm() {
        return JOptionPane.showConfirmDialog(mainFrame, "Clear all history?", "Warning", 
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private void open() {
        try {
            JFileChooser chooser = new JFileChooser(PropertiesStorage.DEFAULT_PROP_FILE);
            chooser.setDialogType(JFileChooser.OPEN_DIALOG);
            setCurrentConfigFile(chooser);
            int opt = chooser.showOpenDialog(mainFrame);
            if(opt == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                if(!file.exists()) {
                    JOptionPane.showMessageDialog(mainFrame, "File not exists!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                openConfigFile(file.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void setCurrentConfigFile(JFileChooser chooser) throws Exception {
        String selected = ConfigFileStorage.getInstance().getProp(PropFileConfig.CONFIG_FILE);
        if(selected!=null && !selected.trim().isEmpty()) {
            chooser.setSelectedFile(new File(selected));
        } else {
            chooser.setSelectedFile(new File(PropertiesStorage.DEFAULT_PROP_FILE));
        }
    }

    private void save() {
        mainFrame.save();
    }
    
    private void saveAs() {
        try {
            JFileChooser chooser = new JFileChooser(PropertiesStorage.DEFAULT_PROP_FILE);
            chooser.setDialogType(JFileChooser.SAVE_DIALOG);
            setCurrentConfigFile(chooser);
            int opt = chooser.showSaveDialog(mainFrame);
            if(opt == JFileChooser.APPROVE_OPTION) {
                File newFile = chooser.getSelectedFile();
                if(newFile.exists() && !overrideConfirm()) {
                    return;
                }
                String current = ConfigFileStorage.getInstance().getProp(PropFileConfig.CONFIG_FILE);
                Files.copy(new File(current).toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                addNewFileConfig(newFile.getAbsolutePath());
                PropertiesStorage.setInstance(newFile.getAbsolutePath());
                save();
                mainFrame.updateTitle();
                reloadMenuFileItens();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearRecentFiles() {
        try {
            if(clearHistoryConfirm()) {
                filesHistory = new HashSet<>();
                ConfigFileStorage.getInstance().setPropArray(PropFileConfig.HISTORY, filesHistory.toArray(new String[0]));
                ConfigFileStorage.getInstance().saveProperties();
                reloadMenuFileItens();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openInstallFolder() {
        try {
            File defaultPropFile = new File(PropertiesStorage.DEFAULT_PROP_FILE);
            Desktop.getDesktop().open(defaultPropFile.getParentFile());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadRecentFiles() {
        try {
            String current = ConfigFileStorage.getInstance().getProp(PropFileConfig.CONFIG_FILE);
            String[] history = ConfigFileStorage.getInstance().getPropArray(PropFileConfig.HISTORY);
            filesHistory = new HashSet<>();
            if(history!=null && history.length>0) {
                filesHistory.addAll(Arrays.asList(history));
            } else {
                filesHistory.add(current == null || current.trim().isEmpty()?PropertiesStorage.DEFAULT_PROP_FILE:current);
            }
            int i = 1;
            for (String f : filesHistory) {
                menuFile.add(menuItem((i++)+": "+f, evt->openConfigFile(f)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void openConfigFile(String fileName) {
        try {
            addNewFileConfig(fileName);
            PropertiesStorage.setInstance(fileName);
            PropertiesStorage.getInstance().saveProperties();
            mainFrame.reload();
            reloadMenuFileItens();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addNewFileConfig(String fileName) throws Exception {
        if(filesHistory.size() >= 10) {
            filesHistory.remove(filesHistory.iterator().next());
        }
        filesHistory.add(fileName);
        ConfigFileStorage.getInstance().setProp(PropFileConfig.CONFIG_FILE, fileName);
        ConfigFileStorage.getInstance().setPropArray(PropFileConfig.HISTORY, filesHistory.toArray(new String[0]));
        ConfigFileStorage.getInstance().saveProperties();
    }

    private JMenuItem menuItem(String text, ActionListener action) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.addActionListener(action);
        return menuItem;
    }
    
    private void showAbout() {
        String msg = "LDAP Query Tool \n\n"
                + "Version: \n"
                + "    0.1-beta \n\n"
                + "Developer: \n"
                + "    margel.peretto@gmail.com";
        JTextArea jta = new JTextArea(msg );
        jta.setFont(new JLabel().getFont());
        jta.setOpaque(false);
        jta.setEditable(false);
        jta.setCursor(new Cursor(Cursor.TEXT_CURSOR));
        JOptionPane.showMessageDialog(mainFrame, jta, "About", JOptionPane.INFORMATION_MESSAGE);
    }
}
