package org.test.ldapsearch.view.comps;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class PlaceHolderTextField extends JTextField {

	private final JTextField tfPlaceHolder = new JTextField();

	public PlaceHolderTextField(String placeHolderText) {
		tfPlaceHolder.setOpaque(false);
		setPlaceHolderText(placeHolderText);
		setPlaceHolderForeground(Color.LIGHT_GRAY);
	}

	public void setPlaceHolderForeground(Color placeHolderForeground) {
		tfPlaceHolder.setForeground(placeHolderForeground);
	}
	
	public void setPlaceHolderText(String placeHolderText) {
		tfPlaceHolder.setText(placeHolderText);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if(getText().isEmpty() && !tfPlaceHolder.getText().isEmpty()) {
			tfPlaceHolder.setBorder(getBorder());
			tfPlaceHolder.setFont(getFont());
			tfPlaceHolder.setSize(getSize());
			tfPlaceHolder.paint(g);
		}
	}
	
}