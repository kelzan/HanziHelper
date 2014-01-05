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
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

/**
 * Author: Colin Jacobs Date: Oct 7, 2004 Lets the user set a filter on the characters viewed.
 */
public class FilterDialog extends JDialog implements ActionListener {

    private String book;
    private String chapters;
    private JComboBox bookList; // Your choices are the set of books in the record list
    private JTextField chaptersField;
    private JButton ok, clear, cancel;
    private JLabel bookLabel = new JLabel("Book:");
    private JLabel chaptersLabel = new JLabel("Chapter range:");

    public FilterDialog(CharRecord records, RecordFilter oldFilter) {
        Set books = new HashSet();
        Iterator it = records.getRecords(false).iterator();
        while (it.hasNext()) {
            Record rec = (Record) it.next();
            if (rec.getBook() != null && rec.getBook().length() > 0) {
                books.add(rec.getBook());
            }

        }

        chaptersField = new JTextField(15);
        bookList = new JComboBox((String[]) books.toArray(new String[]{}));
        ok = new JButton("OK");
        clear = new JButton("Clear");
        cancel = new JButton("Cancel");

        if (oldFilter != null) {
            if (oldFilter.getChapter() != null) {
                chapters = oldFilter.getChapter();
                chaptersField.setText(chapters);
            }
            if (oldFilter.getBook() != null) {
                for (int i = 0; i < bookList.getItemCount(); i++) {
                    if (((String) bookList.getItemAt(i)).equals(oldFilter.getBook())) {
                        bookList.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }

        Container thiss = this.getContentPane();
        thiss.setLayout(new FlowLayout());
        thiss.add(bookLabel);
        thiss.add(bookList);
        thiss.add(chaptersLabel);
        thiss.add(chaptersField);
        thiss.add(ok);
        thiss.add(clear);
        thiss.add(cancel);

        ok.addActionListener(this);
        cancel.addActionListener(this);
        clear.addActionListener(this);
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
        if (book == null) {
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
        } else if (e.getSource() == ok) {
            book = bookList.getSelectedItem().toString();
            chapters = chaptersField.getText();
            setVisible(false);
        } else if (e.getSource() == cancel) {
            setVisible(false);
        }
    }
}
