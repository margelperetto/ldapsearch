package org.test.ldapsearch.view;

import java.awt.Window;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class ErrorModal extends JDialog{

    public ErrorModal(Window owner, String msg, Throwable e) {
        super(owner);
        
        JTextArea jtaMsg = new JTextArea(msg);
        jtaMsg.setEditable(false);
        jtaMsg.setOpaque(false);
        jtaMsg.setWrapStyleWord(true);
        jtaMsg.setLineWrap(true);
        
        JTextArea jtaDetails = new JTextArea(getStackStrace(e));
        jtaDetails.setEditable(false);
        
        JButton btn = new JButton("CLOSE");
        btn.addActionListener(evt->dispose());
        
        setLayout(new MigLayout(new LC().fill().wrapAfter(1)));
        add(jtaMsg, new CC().gapTop("15").growX());
        add(new JLabel("Details:"), new CC().gapTop("15"));
        add(new JScrollPane(jtaDetails), new CC().grow().height("0:100%:"));
        add(btn, new CC().alignX("right"));
        
        setTitle("Error");
        setSize(800, 600);
        setLocationRelativeTo(owner);
        setModal(true);
    }

    private String getStackStrace(Throwable e) {
        try {
            if(e==null) return "No details";
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return sw.toString();
        } catch (Exception e2) {
            e.printStackTrace();
            System.out.println("------ get stacktrace error->");
            e2.printStackTrace();
            return "Error! See log for details!";
        }
    }
}
