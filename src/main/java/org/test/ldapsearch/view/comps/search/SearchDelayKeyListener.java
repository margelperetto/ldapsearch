package org.test.ldapsearch.view.comps.search;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.SwingWorker;
import static java.awt.event.KeyEvent.*;

public class SearchDelayKeyListener extends KeyAdapter{

	private static final List<Integer> IGNORED_KEYS = Arrays.asList(
			VK_LEFT, VK_RIGHT, VK_UP, VK_DOWN, VK_SHIFT, VK_CONTROL, VK_CAPS_LOCK, VK_ALT, VK_ALT_GRAPH,
			VK_HOME, VK_END, VK_PAGE_UP, VK_PAGE_DOWN);
	
	private static final long DELAY = 250;

	private final PanelSearchInResults panelSearch;
	private long lastSearch;
	private boolean waiting;

	public SearchDelayKeyListener(PanelSearchInResults panelSearch) {
		this.panelSearch = panelSearch;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode()==VK_ENTER) {
			panelSearch.getIndexedData().next();
		} else if(!IGNORED_KEYS.contains(e.getKeyCode())) {
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
