package org.test.ldapsearch.view.comps;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public enum AppIcons {

	SEARCH("search_black_18x18.png"),
	LOADING("loading_black_18x18.gif")
	;
	
	private String iconName;
	
	private AppIcons(String iconName) {
		this.iconName = iconName;
	}
	
	public Icon icon() {
		return getIcon(iconName);
	}
	
	public Image image() {
		ImageIcon imageIcon = getImageIcon(iconName);
		return imageIcon==null?null:imageIcon.getImage();
	}
	
	public BufferedImage bufferedImage() {
		ImageIcon icon = getImageIcon(iconName);
		if(icon==null) {
			return null;
		}
		BufferedImage bi = new BufferedImage(
				icon.getIconWidth(),
				icon.getIconHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();
		icon.paintIcon(null, g, 0,0);
		g.dispose();
		return bi;
	}
	
	public static Icon getIcon(String iconName) {
		return getImageIcon(iconName);
	}
	
	public static ImageIcon getImageIcon(String iconName) {
		String res = "icons/"+iconName;
		try {
			return new ImageIcon(AppIcons.class.getClassLoader().getResource(res));
		} catch (Exception e) {
			System.err.println("Error load resource -> "+res);
			return null;
		}
	}
}
