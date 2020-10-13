package org.test.ldapsearch.view.comps.search;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.SwingWorker;

public class SearchDelayKeyListener extends KeyAdapter{

	protected static final long DELAY = 250;
	private final PanelSearchInResults panelSearch;
	private long lastSearch;
	private boolean waiting;

	public SearchDelayKeyListener(PanelSearchInResults panelSearch) {
		this.panelSearch = panelSearch;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_ENTER) {
			panelSearch.getIndexedData().next();
		}else {
			waitAndSearch();
		}
	}

	private void waitAndSearch() {
		lastSearch = System.currentTimeMillis();
		if(waiting) {
			return;
		}
		waiting = true;
		panelSearch.setLoading(true);
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				while (System.currentTimeMillis()-lastSearch < DELAY)
					Thread.sleep(DELAY);
				return null;
			}
			@Override
			protected void done() {
				panelSearch.search();
				waiting = false;
				panelSearch.setLoading(false);
			}
		}.execute();
	}
}
