package org.test.ldapsearch.view.comps;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

public final class UndoRedoInstaller {
	
	private static Map<JTextComponent, UndoManager> map = new HashMap<>();

	private UndoRedoInstaller() {}

	public static UndoManager install(JTextComponent textComp) {
		UndoManager undoManager = new UndoManager();
		textComp.getDocument().addUndoableEditListener(e->undoManager.addEdit(e.getEdit()));
		textComp.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent evt) {
				readKey(evt, undoManager);
			}
		});
		map.put(textComp, undoManager);
		return undoManager;
	}

	private static void readKey(KeyEvent evt, UndoManager manager) {
		if(!evt.isControlDown()) return;

		if(evt.getKeyCode() == KeyEvent.VK_Z) {
			if(manager.canUndo()) manager.undo();
		} else if(evt.getKeyCode() == KeyEvent.VK_Y && manager.canRedo()) {
			manager.redo();
		}
	}

	public static void discardAllEdits(JTextComponent textComp) {
		UndoManager manager = map.get(textComp);
		if(manager!=null) manager.discardAllEdits();
	}

	public static void discardAllEdits() {
		map.keySet().stream().forEach(UndoRedoInstaller::discardAllEdits);
	}
}
