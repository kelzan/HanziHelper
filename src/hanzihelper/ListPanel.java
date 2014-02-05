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
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.StringTokenizer;

/**
 * This components lists all the characters in the database in a sortable JTable
 */
public class ListPanel extends JPanel {

    private JTable table;
    private JScrollPane scroller;
    private RecordTableModel model;
    static Font defaultFont = new Font(CharProps.getProperty("font.pinyin.face"), Font.PLAIN, 20);
    static Font chineseFont = new Font(CharProps.getProperty("font.ch.face"), Font.PLAIN, 50);
    static Font smallFont = new Font("Arial", Font.PLAIN, 12);
    private static int ROW_HEIGHT = 60;

    public static void refreshFonts() {
        defaultFont = new Font(CharProps.getProperty("font.pinyin.face"), Font.PLAIN, 20);
        chineseFont = new Font(CharProps.getProperty("font.ch.face"), Font.PLAIN, 50);
    }

    public void setFilter(RecordFilter filter) {
        model.setFilter(filter);
        model.fireTableDataChanged();
    }

    public ListPanel(CharRecord record) {
        try {
            this.setLayout(new BorderLayout());
            this.setPreferredSize(new Dimension(700, 400));
            String[] columnNames = {"Order", "Pinyin", "Chinese", "Trad", "English", "Book", "Chap"};

            int[] widths = loadColumnWidths();

            model = new RecordTableModel(record);
            TableSorter sorter = new TableSorter(model);

            table = new JTable(sorter);
            sorter.setTableHeader(table.getTableHeader());

            TableCellRenderer renderer = new MyRenderer();
            for (int i = 0; i < columnNames.length; i++) {
                TableColumn col = table.getColumnModel().getColumn(i);
                col.setPreferredWidth(widths[i]);
                col.setHeaderValue(columnNames[i]);
                col.setCellRenderer(renderer);
            }
            table.setRowHeight(ROW_HEIGHT);
            table.getTableHeader().setResizingAllowed(true);
            table.getTableHeader().setReorderingAllowed(false);
            table.getTableHeader().setBackground(CharApp.COLOR_BG);
            table.setRowSelectionAllowed(true);

            table.setAutoCreateColumnsFromModel(false);

            scroller = new JScrollPane(table);
            scroller.getVerticalScrollBar().setBackground(CharApp.COLOR_BG);
            scroller.getHorizontalScrollBar().setBackground(CharApp.COLOR_BG);
            this.add(scroller, BorderLayout.CENTER);

            MouseListener ml = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON3) {
                        Point origin = e.getPoint();
                        int row = table.rowAtPoint(origin);
                        if (row > -1) {
                            // Used to show stroke order, but this feature is deprecated.
//                            showStrokeOrder("" + table.getModel().getValueAt(row, 2));
                        }
                    } else if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                        CharApp.getInstance().editRecord();
                    }
                }
            };

            table.addMouseListener(ml);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public int[] getSelectedRows() {
        return table.getSelectedRows();
    }

    public void refresh() {
        ((AbstractTableModel) table.getModel()).fireTableDataChanged();
    }

    public RecordFilter getFilter() {
        return ((RecordTableModel) table.getModel()).getFilter();
    }

    static class RecordTableModel extends AbstractTableModel {

        private CharRecord records;

        public RecordTableModel(CharRecord record) {
            this.records = record;
        }

        public int getColumnCount() {
            return 7;
        }

        public int getRowCount() {
            return records.getFilteredCount();
        }

        public RecordFilter getFilter() {
            return records.getRecordFilter();
        }

        public void setFilter(RecordFilter filter) {
            records.setRecordFilter(filter);
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            Record record = records.getRecordNumber(rowIndex, true);
            switch (columnIndex) {
                case 0:
                    return "" + record.getOrder();
                case 1:
                    return record.getPinyinAsUnicode();
                case 2:
                    return record.getChars();
                case 3:
                    return record.getTrad();
                case 4:
                    return record.getEnglish();
                case 5:
                    return record.getBook();
                case 6:
                    return record.getChapter();
                default:
                    return "";
            }

        }
    }

    static class MyRenderer extends DefaultTableCellRenderer {

        public MyRenderer() {
            super();
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel comp = new SmoothLabel(value.toString());
            comp.setHorizontalAlignment(SwingConstants.CENTER);
            comp.setVerticalAlignment(SwingConstants.CENTER);
            comp.setForeground(Color.black);
            comp.setOpaque(true);
            comp.setBackground(isSelected ? new Color(240, 240, 0) : Color.white);
            if (column == 2 || column == 3) {
                comp.setFont(chineseFont);
            } else if (column == 5 || column == 6) {
                comp.setFont(smallFont);
            } else {
                comp.setFont(defaultFont);
            }
            return comp;
        }
    }

    static class SmoothLabel extends JLabel {

        public SmoothLabel() {
        }

        public SmoothLabel(String text) {
            super(text);
        }

        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            super.paintComponent(g);
        }
    }

    public void saveColumnWidths() {
        StringBuffer sb = new StringBuffer();
        TableColumn column = null;
        for (int i = 0; i < 7; i++) {
            column = table.getColumnModel().getColumn(i);
            sb.append(column.getPreferredWidth());
            if (i < 6) {
                sb.append(",");
            }
        }
        CharProps.getProperties().setProperty("column.widths", sb.toString());
    }

    private int[] loadColumnWidths() {
        int[] defaults = new int[]{40, 100, 200, 200, 200, 150, 50};
        int[] widths = new int[7];

        String prefs = CharProps.getProperty("column.widths");
        try {
            if (prefs != null) {
                StringTokenizer st = new StringTokenizer(prefs, ",");
                for (int i = 0; i < widths.length; i++) {
                    widths[i] = Integer.parseInt(st.nextToken());
                }
            }
        } catch (Throwable t) {
            widths = defaults;
        }
        return widths;
    }

    public JTable getTable() {
        return table;
    }

    public TableSorter getSorter() {
        return (TableSorter) table.getModel();
    }
}
