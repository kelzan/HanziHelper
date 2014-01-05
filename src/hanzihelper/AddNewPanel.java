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
import java.awt.event.*;

/**
 * A dialog for adding another character or characters to the database.
 */
public class AddNewPanel extends JDialog implements ActionListener {

    protected JTextField pinyin, chars, english;
    protected JTextField book, chapter;
    protected JButton add, cancel;
    protected JLabel engLabel, pinLabel, charLabel;
    protected JLabel bookLabel, chapLabel;

    public AddNewPanel() {
        Container thiss = getContentPane();
        thiss.setBackground(CharApp.COLOR_BG);

        this.setTitle("Add new record");
        thiss.setLayout(new GridLayout(6, 1));
        pinyin = new JTextField(12);
        pinyin.setFont(new Font("Arial Unicode MS", Font.PLAIN, 16));
        chars = new JTextField(12);
        english = new JTextField(12);
        english.setFont(new Font("Arial Unicode MS", Font.PLAIN, 16));
        chars.setFont(new Font("Arial Unicode MS", Font.PLAIN, english.getFont().getSize()));
        book = new JTextField(15);
        chapter = new JTextField(12);

        engLabel = new JLabel("English:");
        charLabel = new JLabel("Chinese:");
        pinLabel = new JLabel("Pinyin:");
        bookLabel = new JLabel("Book:");
        chapLabel = new JLabel("Chapter:");

        JPanel temp;

        temp = new JPanel(new FlowLayout());
        temp.add(pinLabel);
        temp.add(pinyin);
        thiss.add(temp);

        temp = new JPanel(new FlowLayout());
        temp.add(charLabel);
        temp.add(chars);
        thiss.add(temp);

        temp = new JPanel(new FlowLayout());
        temp.add(engLabel);
        temp.add(english);
        thiss.add(temp);

        temp = new JPanel(new FlowLayout());
        temp.add(bookLabel);
        temp.add(book);
        thiss.add(temp);

        temp = new JPanel(new FlowLayout());
        temp.add(chapLabel);
        temp.add(chapter);
        thiss.add(temp);

        add = new JButton("Add");
        cancel = new JButton("Cancel");

        temp = new JPanel(new FlowLayout());
        temp.add(add);
        temp.add(cancel);
        thiss.add(temp);

        add.addActionListener(this);
        cancel.addActionListener(this);

        KeyListener enterListener = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    actionPerformed(new ActionEvent(add, -1, ""));
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    actionPerformed(new ActionEvent(cancel, -1, ""));
                }
            }
        };

        pinyin.addKeyListener(enterListener);
        english.addKeyListener(enterListener);
        chars.addKeyListener(enterListener);
        book.addKeyListener(enterListener);
        chapter.addKeyListener(enterListener);

        this.pack();
        pinyin.requestFocusInWindow();
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

            Record rec = new Record(-1, pinyin.getText(), chars.getText(), english.getText(), book.getText(), chapter.getText());
            try {
                CharApp.getInstance().getRecord().addRecord(rec);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, ex.getMessage(), "A problem occured", JOptionPane.ERROR_MESSAGE);
            }
            CharApp.getInstance().refresh();
            CharApp.getInstance().getFilterPanel().refresh();
            Rectangle v = CharApp.getInstance().getListPanel().getTable().getVisibleRect();
            v.y = CharApp.getInstance().getListPanel().getTable().getHeight() - v.height + 100;
            CharApp.getInstance().getListPanel().getTable().scrollRectToVisible(v);
            pinyin.setText("");
            chars.setText("");
            english.setText("");
            pinyin.requestFocusInWindow();
        } else {
            this.setVisible(false);
            this.dispose();
        }
    }
}
