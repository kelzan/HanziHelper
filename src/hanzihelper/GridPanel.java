/*
 Hanzi Helper, http://hanzihelper.sourceforge.net
 Copyright (C) 2005, Colin Jacobs

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package hanzihelper;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * This component renders the characters in a grid for copying and practice. It's this component
 * that gets sent to the printer. It's complicated and messy.
 */
public class GridPanel extends JPanel implements Printable {

    private static DateFormat df = new SimpleDateFormat("MM/dd/yy");
    public static final int anInch = 72;
    private double margins = .25f; // inches   paper.margin
    private int padding = 20; // pixels extra margin
    private int topBox = (int) (0.5 * anInch);
    private PrintMode printMode;
    private boolean drawGuides = true;
    private double boxWInches = 0.75; // box.width
    private double boxHInches = 0.75; // box.height
    private Font chineseFont, pinyinFont, otherFont;
    private Font headerFont = new Font("Times New Roman", Font.BOLD, 16);
    private int pages = 1, currentPageIndex = 0;
    private boolean randomOrder = false;
    private boolean fillThePage = false;
    private boolean header = true;
    private boolean gridLines = true;
    private Color guidesColor = Color.gray;

    public GridPanel(PrintMode printMode) {
        this.printMode = printMode;
        Dimension preferredSize;
        String paperSize = CharProps.getProperty("paper.size");
        if ("A4".equalsIgnoreCase(paperSize)) {
            preferredSize = new Dimension((int) (210 / 25.4 * anInch), (int) (297 / 25.4 * anInch));
        } else { // Letter
            preferredSize = new Dimension((int) (8.5 * anInch), ((int) 11 * anInch));
        }
        boxWInches = CharProps.getDoubleProperty("box.width", boxWInches);
        boxHInches = CharProps.getDoubleProperty("box.height", boxHInches);
        margins = CharProps.getDoubleProperty("paper.margins", margins);
        padding = CharProps.getIntProperty("padding", padding);
        if (CharProps.getProperty("draw.guides") != null) {
            drawGuides = "true".equalsIgnoreCase(CharProps.getProperty("draw.guides"));
        }

        // Initialize fonts
        chineseFont = new Font(CharProps.getStringProperty("font.ch.face", "Arial Unicode MS"),
                CharProps.getIntProperty("font.ch.prop", Font.PLAIN),
                CharProps.getIntProperty("font.ch.size", 40));

        pinyinFont = new Font(CharProps.getStringProperty("font.pinyin.face", "Arial Unicode MS"),
                CharProps.getIntProperty("font.pinyin.prop", Font.PLAIN),
                CharProps.getIntProperty("font.pinyin.size", 10));

        otherFont = new Font(CharProps.getStringProperty("font.other.face", "Arial Unicode MS"),
                CharProps.getIntProperty("font.other.prop", Font.PLAIN),
                CharProps.getIntProperty("font.other.size", 10));

        String guidesColorString = CharProps.getStringProperty("guides.color", "gray");
        if (guidesColorString.equalsIgnoreCase("lightgray")) {
            guidesColor = Color.lightGray;
        }

        this.setPreferredSize(preferredSize);

        calcPages((int) (getPreferredSize().getWidth() - (2 * margins * anInch) - 2 * padding),
                (int) (getPreferredSize().getHeight() - (2 * margins * anInch) - 2 * padding));
    }

    public void paint(Graphics g) {
        double scale = 1.0;
        paintGrid(g, null, currentPageIndex, scale);
    }

    private void paintGrid(Graphics g, PageFormat pageFormat, int pageIndex, double scale) {
        int topBox = header ? this.topBox : 0;
        int width = getWidth();
        int height = getHeight();
        double drawX;
        double drawY;
        // Clear
        g.setColor(Color.white);
        g.fillRect(0, 0, width, height);

        // Oh, we're printing? Let's check the printable area and translate the graphics object.
        if (pageFormat != null) {
            drawX = pageFormat.getImageableX();
            drawY = pageFormat.getImageableY();
            width = (int) pageFormat.getImageableWidth();
            height = (int) pageFormat.getImageableHeight();
        } else {
            ((Graphics2D) g).scale((double) getWidth() / (8.5d * anInch), (double) getHeight() / (11 * anInch));
            width = (int) (8.5 * anInch);
            height = 11 * anInch;
            drawX = (margins * anInch);
            drawY = (margins * anInch);
            width -= (2 * drawX);
            height -= (2 * drawY);
        }
        drawX += padding;
        drawY += padding;
        width -= 2 * padding;
        height -= 2 * padding;

        ((Graphics2D) g).translate(drawX, drawY);

        g.setColor(Color.black);

        int x1 = 0;
        int y1 = topBox;
        int x2 = x1 + width;
        int y2 = y1 + (int) (height - topBox);
        int w = x2 - x1;
        int h = y2 - y1;

        int boxW = (int) (boxWInches * anInch);
        int boxH = (int) (boxHInches * anInch);
        int boxesAcross = w / boxW;
        int boxesDown = h / boxH;
        int dx = w % boxW;
        int dy = h % boxH;

        pages = calcPages(width, height);
        if (currentPageIndex > pages - 1) {
            currentPageIndex = pages - 1;
            repaint();
            return;
        }

        if (header) {
            drawHeader(g, 0, 0, width, topBox, pages, pageIndex + 1);
        }

        // Draw the boxes
        if (gridLines) {
            Color gray = new Color(200, 200, 200);
            for (int i = 0; i < boxesAcross; i++) {
                for (int j = 0; j < boxesDown; j++) {
                    int x = x1 + (dx / 2) + i * boxW;
                    int y = y1 + (dy / 2) + j * boxH;
                    g.drawRect(x, y, boxW, boxH);
                    if (drawGuides) {
                        g.setColor(Color.gray);
//                        g.setColor(guidesColor);
                        //                       g.drawLine(x, y, x + boxW, y + boxH);
                        //                       g.drawLine(x + boxW, y, x, y + boxH);
                        g.drawLine(x + boxW / 2, y, x + boxW / 2, y + boxH);
                        g.drawLine(x, y + boxH / 2, x + boxW, y + boxH / 2);

                        g.setColor(Color.lightGray);
                        g.drawLine(x + boxW / 4, y, x + boxW / 4, y + boxH);
                        g.drawLine(x + boxW / 4 + boxW / 2, y, x + boxW / 4 + boxW / 2, y + boxH);
                        g.drawLine(x, y + boxH / 4, x + boxW, y + boxH / 4);
                        g.drawLine(x, y + boxH / 2 + boxH / 4, x + boxW, y + boxH / 2 + boxH / 4);
                        g.setColor(Color.black);
                    }
                }
            }
        }

        // Finally, draw the text

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int charsPerPage = 1;
        if (printMode.getStyle() == PrintMode.STYLE_CRAM) {
            charsPerPage = (boxesDown * (boxesAcross - (boxesAcross % 2)) / 2);
        } else if (printMode.getStyle() == PrintMode.STYLE_ONE_PER_LINE) {
            charsPerPage = boxesDown;
        } else if (printMode.getStyle() == PrintMode.STYLE_ALTERNATING_LINES) {
            charsPerPage = boxesDown / 2;
        } else if (printMode.getStyle() == PrintMode.STYLE_READING) {
            charsPerPage = boxesDown * boxesAcross;
        }


        // Get the text to draw
        PrintableRecord[] printableRecords = new PrintableRecord[charsPerPage];
        Collection allPrintables = getPrintables(CharApp.getInstance().getRecord().getRecords(randomOrder));
        List list = new ArrayList(allPrintables);

        for (int i = 0; i < printableRecords.length; i++) {
            int listIdx = i + pageIndex * charsPerPage;
            if (listIdx < list.size()) { // If we haven't run out of characters to print
                PrintableRecord rec = (PrintableRecord) list.get(listIdx);
                printableRecords[i] = rec;
            } else if (fillThePage) { // Or we have, and we start again
                PrintableRecord rec = (PrintableRecord) list.get(listIdx % list.size());
                printableRecords[i] = rec;
            }
        }

        // --------------------------
        // Draw the text in the boxes
        // --------------------------
        int currentRec = 0;
        if (printMode.getStyle() == PrintMode.STYLE_CRAM) {
            for (int i = 0; i < boxesAcross - 1; i += 2) {
                for (int j = 0; j < boxesDown; j++) {
                    if (printableRecords[currentRec] == null) {
                        if (fillThePage) {
                            currentRec = 0;
                        }
                    }
                    printText(g, printableRecords[currentRec++], x1, y1, dx, dy, i, j, boxW, boxH);
                }
            }
        } else if (printMode.getStyle() == PrintMode.STYLE_READING) {
            for (int i = 0; i < boxesAcross; i++) {
                for (int j = 0; j < boxesDown; j++) {
                    if (printableRecords[currentRec] == null) {
                        if (fillThePage) {
                            currentRec = 0;
                        }
                    }
                    printText(g, printableRecords[currentRec++], x1, y1, dx, dy, i, j, boxW, boxH);
                }
            }
        } else if (printMode.getStyle() == PrintMode.STYLE_ONE_PER_LINE) {
            for (int j = 0; j < boxesDown; j++) {
                printText(g, printableRecords[currentRec++], x1, y1, dx, dy, 0, j, boxW, boxH);
            }
        } else if (printMode.getStyle() == PrintMode.STYLE_ALTERNATING_LINES) {
            for (int j = 0; j < boxesDown - 1; j += 2) {
                PrintableRecord rec = printableRecords[currentRec++];
                for (int i = 0; i < boxesAcross; i++) {
                    printText(g, rec, x1, y1, dx, dy, i, j, boxW, boxH);
                }
            }
        }
    }

    private Collection getPrintables(Collection records) {
        List l = new ArrayList();
        for (Iterator iterator = records.iterator(); iterator.hasNext();) {
            Record record = (Record) iterator.next();
            for (int c = 0; c < record.getChars().length(); c++) {
                l.add(getPrintable(record, c, c < record.getChars().length() - 1));
            }
        }
        return l;
    }

    private int getFontWidth(Font font) {
        FontMetrics metrics = getFontMetrics(font);
        int[] ints = metrics.getWidths();
        int fontWidth = ints[20] + ints[40] + ints[60] + ints[80] + ints[100] + ints[120] + ints[140]
                + ints[180] + ints[200] + ints[220] + ints[240];
        fontWidth /= 10;
        return fontWidth;
    }

    private void printText(Graphics g, PrintableRecord record, int x1, int y1, int dx, int dy, int i, int j,
            int boxW, int boxH) {
        if (record == null) {
            return;
        }


        g.setFont(record.getFont());

        FontMetrics fm = g.getFontMetrics(g.getFont());

        String rawText = record.getTextToPrint();
        String lines[] = new String[]{rawText};

        if (rawText.indexOf("\n") > 0) {
            StringTokenizer st = new StringTokenizer(rawText, "\n");
            lines = new String[st.countTokens()];
            int s = 0;
            while (st.hasMoreTokens()) {
                lines[s++] = st.nextToken();
            }
        }

        for (int k = 0; k < lines.length; k++) {
            int x = (x1 + (dx / 2) + i * boxW);
            int y = (y1 + (dy / 2) + j * boxH);
            String line = lines[k];
            java.awt.geom.Rectangle2D rect = fm.getStringBounds(line, g);
            int textHeight = (int) (rect.getHeight());
            int textWidth = (int) (rect.getWidth());
            x += (boxW - textWidth) / 2;
            y += (boxH - (textHeight * lines.length)) / 2 + fm.getAscent();
            y += textHeight * k;
            g.drawString(line, x, y);
        }
    }

    public void initFonts() {
        int boxH = (int) (boxHInches * anInch);
        int boxW = (int) (boxWInches * anInch);

        int fontSize = 60;
        FontMetrics metrics;
        do {
            chineseFont = new Font(chineseFont.getFontName(), Font.PLAIN, fontSize -= 1);
            metrics = getFontMetrics(chineseFont);
        } while (metrics.getHeight() > boxH * 3f / 4f);

        fontSize = 20;
        do {
            pinyinFont = new Font(pinyinFont.getFontName(), Font.PLAIN, fontSize -= 1);
            metrics = getFontMetrics(pinyinFont);
        } while (getFontWidth(pinyinFont) * 8 > boxW);


        fontSize = 10;
        do {
            otherFont = new Font(otherFont.getFontName(), Font.PLAIN, fontSize -= 1);
            metrics = getFontMetrics(otherFont);
        } while (getFontWidth(otherFont) * 8 > boxW);
        CharProps.getProperties().setProperty("font.ch.face", chineseFont.getFontName());
        CharProps.getProperties().setProperty("font.ch.size", chineseFont.getSize() + "");
        CharProps.getProperties().setProperty("font.ch.style", chineseFont.getStyle() + "");
        CharProps.getProperties().setProperty("font.pinyin.face", pinyinFont.getFontName());
        CharProps.getProperties().setProperty("font.pinyin.size", pinyinFont.getSize() + "");
        CharProps.getProperties().setProperty("font.pinyin.style", pinyinFont.getStyle() + "");
        CharProps.getProperties().setProperty("font.other.face", otherFont.getFontName());
        CharProps.getProperties().setProperty("font.other.size", otherFont.getSize() + "");
        CharProps.getProperties().setProperty("font.other.style", otherFont.getStyle() + "");

    }

    /**
     * Since the boxes to draw in need to be a constant size, the number of boxes and hence the
     * number of pages changes and needs to be recalced, especially at print time.
     *
     * @param height Drawable area height
     * @param width  Drawable area width
     */
    private int calcPages(int width, int height) {
        int x1 = 0;
        int y1 = topBox;
        int x2 = x1 + width;
        int y2 = y1 + (int) (height - topBox);
        int w = x2 - x1;
        int h = y2 - y1;

        int boxW = (int) (boxWInches * anInch);
        int boxH = (int) (boxHInches * anInch);
        double boxesAcross = w / boxW;
        double boxesDown = h / boxH;

        int recordCount = CharApp.getInstance().getRecord().getRecords(randomOrder).size();
        int pages = 0;
        if (printMode.getStyle() == PrintMode.STYLE_CRAM) {
            pages = (int) Math.ceil(recordCount / (boxesDown * (boxesAcross - (boxesAcross % 2)) / 2));
        } else if (printMode.getStyle() == PrintMode.STYLE_ONE_PER_LINE) {
            pages = (int) Math.ceil(recordCount / boxesDown);
        } else if (printMode.getStyle() == PrintMode.STYLE_ALTERNATING_LINES) {
            pages = (int) Math.ceil(recordCount / (boxesDown / 2));
        } else if (printMode.getStyle() == PrintMode.STYLE_READING) {
            pages = (int) Math.ceil(recordCount / (boxesDown * boxesAcross));
        }
        return pages;
    }

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex < calcPages((int) pageFormat.getImageableWidth() - padding * 2,
                (int) pageFormat.getImageableHeight() - padding * 2)) {
            paintGrid(graphics, pageFormat, pageIndex, 1.0);
            return Printable.PAGE_EXISTS;
        } else {
            return Printable.NO_SUCH_PAGE;
        }
    }

    public void drawHeader(Graphics g, int x, int y, int width, int height, int pages, int currentPage) {
// Draw a box around it
//    g.drawRect(x, y, width, height);

        String headerString1 = "Chinese Character Practice: " + df.format(new Date());
        String headerString2 = "Page " + currentPage + "/" + pages;
        g.setFont(headerFont);
        g.setColor(Color.black);

        FontMetrics fm = g.getFontMetrics(g.getFont());
        java.awt.geom.Rectangle2D rect1 = fm.getStringBounds(headerString1, g);
        java.awt.geom.Rectangle2D rect2 = fm.getStringBounds(headerString2, g);
        int textWidth1 = (int) (rect1.getWidth());
        int textWidth2 = (int) (rect2.getWidth());

        int x1 = x, y1 = y, x2 = x, y2 = y;

        // Center
        x1 += (width - textWidth1) / 2;
        x2 += (width - textWidth2) / 2;

        y1 += fm.getAscent();
        y2 += height;
        g.drawString(headerString1, x1, y1);
        g.drawString(headerString2, x2, y2);

    }

    public PrintMode getPrintMode() {
        return printMode;
    }

    public void setPrintMode(PrintMode printMode) {
        this.printMode = printMode;
        calcPages(this.getWidth() - padding * 2, this.getHeight() - padding * 2);
        repaint();
    }

    public int getCurrentPageIndex() {
        return currentPageIndex;
    }

    public double getMargins() {
        return margins;
    }

    public void setMargins(double margins) {
        this.margins = margins;
    }

    public boolean isDrawGuides() {
        return drawGuides;
    }

    public void setDrawGuides(boolean drawGuides) {
        this.drawGuides = drawGuides;
    }

    public int getPages() {
        return pages;
    }

    public void setCurrentPageIndex(int currentPageIndex) {
        this.currentPageIndex = currentPageIndex;
        repaint();
    }

    public Font getChineseFont() {
        return chineseFont;
    }

    public void setChineseFont(Font chineseFont) {
        this.chineseFont = chineseFont;
        repaint();
    }

    public Font getPinyinFont() {
        return pinyinFont;
    }

    public void setPinyinFont(Font pinyinFont) {
        this.pinyinFont = pinyinFont;
        repaint();
    }

    public Font getOtherFont() {
        return otherFont;
    }

    public void setOtherFont(Font otherFont) {
        this.otherFont = otherFont;
        repaint();
    }

    public boolean isRandomOrder() {
        return randomOrder;
    }

    public void setRandomOrder(boolean randomOrder) {
        this.randomOrder = randomOrder;
    }

    public boolean isFillThePage() {
        return fillThePage;
    }

    public void setFillThePage(boolean fillThePage) {
        this.fillThePage = fillThePage;
    }

    public double getBoxWInches() {
        return boxWInches;
    }

    public void setBoxWInches(double boxWInches) {
        this.boxWInches = boxWInches;
    }

    public double getBoxHInches() {
        return boxHInches;
    }

    public void setBoxHInches(double boxHInches) {
        this.boxHInches = boxHInches;
    }

    public boolean isHeader() {
        return header;
    }

    public void setHeader(boolean header) {
        this.header = header;
    }

    public boolean isGridLines() {
        return gridLines;
    }

    public void setGridLines(boolean gridLines) {
        this.gridLines = gridLines;
    }

    private PrintableRecord getPrintable(Record record, int charIndex, boolean last) {
        String text = "";
        Font f = otherFont;
        if (printMode.getText() == PrintMode.TEXT_CHARS) {
            text = record.getChars().substring(charIndex, charIndex + 1);
            f = chineseFont;
        } else if (printMode.getText() == PrintMode.TEXT_ENGLISH_PINYIN) {
            text = record.getEnglish() + "\n" + PinyinUtil.getSyllables(record.getPinyin())[charIndex];
            f = otherFont;
        } else if (printMode.getText() == PrintMode.TEXT_CHINESE_PINYIN) {
            text = record.getChars() + "\n" + PinyinUtil.getSyllables(record.getPinyin())[charIndex];
            f = chineseFont;
        } else if (printMode.getText() == PrintMode.TEXT_ENGLISH) {
            text = record.getEnglish();
            f = otherFont;
        } else if (printMode.getText() == PrintMode.TEXT_PINYIN) {
            text = PinyinUtil.toUnicode(PinyinUtil.getSyllables(record.getPinyin())[charIndex]);
            f = pinyinFont;
        }
        return new PrintableRecord(text, f /*, last*/);
    }

    private static class PrintableRecord {

        private String textToPrint;
        private Font font;
        private boolean last; // Is this the last char in the word? Used for rendering hints

        public PrintableRecord(String textToPrint, Font font) {
            this.textToPrint = textToPrint;
            this.font = font;
        }

        public String getTextToPrint() {
            return textToPrint;
        }

        public Font getFont() {
            return font;
        }
    }
}
