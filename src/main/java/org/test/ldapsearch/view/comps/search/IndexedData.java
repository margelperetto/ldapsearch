package org.test.ldapsearch.view.comps.search;

import java.util.function.Consumer;

public class IndexedData {

	private final Integer[] positions;
	private int cursor = -1;
	private String text;
	private Consumer<Integer> positionConsumer;
	
	public IndexedData() {
		this("", new Integer[0]);
	}
	
	public IndexedData(String text, Integer[] positions) {
		this.text = text;
		this.positions = positions;
	}
	
	public int length() {
		return positions.length;
	}
	
	public String getText() {
		return text;
	}
	
	public IndexedData consumer(Consumer<Integer> positionConsumer) {
		this.positionConsumer = positionConsumer;
		return this;
	}

	public void next() {
		if(cursor >= positions.length-1) {
			cursor = 0;
		} else {
			cursor++;
		}
		updataSelectValue();
	}
	
	public void previous() {
		if(cursor <= 0) {
			cursor = positions.length-1;
		} else {
			cursor--;
		}
		updataSelectValue();
	}

	private void updataSelectValue() {
		if(length()==0) {
			return;
		}
		try {
			if(positionConsumer!=null) {
				positionConsumer.accept(positions[cursor]);
			}
		} catch (Exception e) {
			System.err.println("Error select indexed data position");
		}
	}

}
