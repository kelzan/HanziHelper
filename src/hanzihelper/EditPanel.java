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

/**
 * This panel is for editing the fields in a record.
 *
 * @see AddNewPanel
 */
public class EditPanel extends AddNewPanel {

    private Record rec;

    public EditPanel(Record rec) {
        super();
        add.setText("Commit");
        this.rec = rec;
        setTitle("Edit record");
        english.setText(rec.getEnglish());
        pinyin.setText(rec.getPinyin());
        chars.setText(rec.getChars());
        trad.setText(rec.getTrad());
        book.setText(rec.getBook());
        chapter.setText(rec.getChapter());
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == add) {
            pinLabel.setForeground(Color.black);
            engLabel.setForeground(Color.black);
            charLabel.setForeground(Color.black);

            if (pinyin.getText().length() == 0) {
                pinLabel.setForeground(Color.red);
                pinyin.requestFocusInWindow();
                return;
            }
            if (chars.getText().length() == 0) {
                charLabel.setForeground(Color.red);
                chars.requestFocusInWindow();
                return;
            }

            rec.setPinyin(pinyin.getText());
            rec.setChars(chars.getText());
            rec.setTrad(trad.getText());
            rec.setEnglish(english.getText());
            rec.setBook(book.getText());
            rec.setChapter(chapter.getText());
            try {
                CharApp.getInstance().getRecord().flushToDisk();
            } catch (Exception e1) {
                CharApp.getInstance().showErrorMessage("Problem saving: " + e1.getMessage());
            }

            CharApp.getInstance().refresh();
            CharApp.getInstance().getFilterPanel().refresh();
        }
        this.setVisible(false);
        this.dispose();
    }
}
