package org.test.ldapsearch;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//Credits: https://www.logicbig.com/how-to/code-snippets/jcode-java-cmd-command-line-table.html
public class CommandLineTable {
    private static final String HORIZONTAL_SEP = "-";
    private String verticalSep;
    private String joinSep;
    private String[] headers;
    private List<String[]> rows = new ArrayList<>();
    private boolean rightAlign;
    private final PrintStream out;

    public CommandLineTable(PrintStream out) {
    	this.out = out;
        setShowVerticalLines(false);
    }

    public void setRightAlign(boolean rightAlign) {
        this.rightAlign = rightAlign;
    }

    public void setShowVerticalLines(boolean showVerticalLines) {
        verticalSep = showVerticalLines ? "|" : "";
        joinSep = showVerticalLines ? "+" : " ";
    }

    public void setHeaders(String... headers) {
        this.headers = headers;
    }

    public void addRow(String... cells) {
    	List<String[]> linesList = new ArrayList<>();
    	for (int c = 0; c < cells.length; c++) {
			String colValue = cells[c];
			String[] colLines = colValue.split("\n");
			for (int l = 0; l < colLines.length; l++) {
				String colLineValue = colLines[l];
				String[] lineCells;
				if(l < linesList.size()) {
					lineCells = linesList.get(l);
				} else {
					lineCells = new String[cells.length];
					for (int i = 0; i < lineCells.length; i++) {
						lineCells[i] = "";
					}
					linesList.add(lineCells);
				}
				lineCells[c] = colLineValue;
			}
		}
    	for (String[] lineCells : linesList) {
    		rows.add(lineCells);
		}
    }

    public void print() {
        int[] maxWidths = headers != null ?
                Arrays.stream(headers).mapToInt(String::length).toArray() : null;

        for (String[] cells : rows) {
            if (maxWidths == null) {
                maxWidths = new int[cells.length];
            }
            if (cells.length != maxWidths.length) {
                throw new IllegalArgumentException("Number of row-cells and headers should be consistent");
            }
            for (int i = 0; i < cells.length; i++) {
                maxWidths[i] = Math.max(maxWidths[i], cells[i].length());
            }
        }

        if (headers != null) {
            printLine(maxWidths);
            printRow(headers, maxWidths);
            printLine(maxWidths);
        }
        for (String[] cells : rows) {
            printRow(cells, maxWidths);
        }
        if (headers != null) {
            printLine(maxWidths);
        }
    }

    private void printLine(int[] columnWidths) {
        for (int i = 0; i < columnWidths.length; i++) {
            String line = String.join("", Collections.nCopies(columnWidths[i] +
                    verticalSep.length() + 1, HORIZONTAL_SEP));
            out.print(joinSep + line + (i == columnWidths.length - 1 ? joinSep : ""));
        }
        out.println();
    }

    private void printRow(String[] cells, int[] maxWidths) {
        for (int i = 0; i < cells.length; i++) {
            String s = cells[i]==null?"":cells[i];
            String verStrTemp = i == cells.length - 1 ? verticalSep : "";
            if (rightAlign) {
                out.printf("%s %" + maxWidths[i] + "s %s", verticalSep, s, verStrTemp);
            } else {
                out.printf("%s %-" + maxWidths[i] + "s %s", verticalSep, s, verStrTemp);
            }
        }
        out.println();
    }
    
}