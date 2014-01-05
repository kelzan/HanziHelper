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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Author: coljac Date: Oct 8, 2004 Sets a filter on book and chapter.
 */
public class FilterPanel extends JPanel implements ActionListener {

    private String book;
    private String chapters;
    private JComboBox bookList;
    private JTextField chaptersField;
    private JButton ok, clear;
    private JLabel bookLabel = new JLabel("Book:");
    private JLabel chaptersLabel = new JLabel("Chapter range:");
    private CharApp app;

    public FilterPanel(CharApp app) {
        this.app = app;
        this.setBackground(CharApp.COLOR_BG);

        Set books = bookSet();

        chaptersField = new JTextField(15);
        bookList = new JComboBox((String[]) books.toArray(new String[]{}));
        ok = new JButton("OK");
        clear = new JButton("Clear");
        ok.setBackground(CharApp.COLOR_BUTTON);
        clear.setBackground(CharApp.COLOR_BUTTON);
        bookList.setBackground(CharApp.COLOR_BUTTON);

        this.setLayout(new FlowLayout());
        this.add(bookLabel);
        this.add(bookList);
        this.add(chaptersLabel);
        this.add(chaptersField);
        this.add(ok);
        this.add(clear);

        ok.addActionListener(this);
        clear.addActionListener(this);
    }

    public void refresh() {
        Set books = bookSet();
        bookList.removeAllItems();
        for (Iterator iterator = books.iterator(); iterator.hasNext();) {
            bookList.addItem(iterator.next());
        }
    }

    private Set bookSet() {
        Set books = new TreeSet();
        books.add("(none)");
        Iterator it = app.getRecord().getRecords(false).iterator();
        while (it.hasNext()) {
            Record rec = (Record) it.next();
            if (rec.getBook() != null && rec.getBook().length() > 0) {
                books.add(rec.getBook());
            }
        }
        return books;
    }

    public String getChapters() {
        return chapters;
    }

    public void setChapters(String chapters) {
        this.chapters = chapters;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public RecordFilter getFilter() {
        if (book == null || book.equals("(none)")) {
            book = "";
        }
        if (chapters == null) {
            chapters = "";
        }
        RecordFilter filter = new RecordFilter(book, chapters);
        return filter;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == clear) {
            book = null;
            chapters = null;
            bookList.setSelectedIndex(0);
            chaptersField.setText("");
            app.getListPanel().setFilter(null);
        } else if (e.getSource() == ok) {
            book = bookList.getSelectedItem().toString();
            chapters = chaptersField.getText();
            app.getListPanel().setFilter(getFilter());
        }
    }
}
