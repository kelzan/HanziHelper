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

import com.ozten.font.JFontChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Allows the user to choose fonts to be used on the grid. Also gives them the option of "resizing",
 * letting the program look at how big the boxes are and make a best guess for the correct font
 * sizes.
 */
public class FontDialog extends JDialog implements ActionListener {

    private GridPanel gridPanel;
    JButton resize, chinese, pinyin, other, display;
    JLabel chineseExample, pinyinExample, otherExample;
    JButton ok;
    String chText = "中文很好";
    String pinText = "Zhōngwén hěn hǎo";
    String enText = "I'm full of tinier men!";

    public FontDialog(Frame owner) throws HeadlessException {
        super(owner);
        this.setTitle("Choose fonts");
        init(new GridPanel(new PrintMode(0, 0)));
    }

    public FontDialog(Dialog owner, GridPanel gridPanel) throws HeadlessException {
        super(owner);
        init(gridPanel);
    }

    private void init(GridPanel gridPanel) {

        this.gridPanel = gridPanel;

        Container thiss = getContentPane();
        thiss.setLayout(new BorderLayout());
        thiss.setBackground(CharApp.COLOR_BG);

        JPanel fontPanel = new JPanel(new GridLayout(/*4*/3, 1));
        fontPanel.setBackground(CharApp.COLOR_BG);
        resize = new JButton("Resize");
        chinese = new JButton("Chinese font");
        pinyin = new JButton("Pinyin font");
        other = new JButton("Other font");
        display = new JButton("Main display");
        ok = new JButton("OK");

        chineseExample = new JLabel(chText, SwingConstants.CENTER);
        chineseExample.setFont(gridPanel.getChineseFont());

        pinyinExample = new JLabel(pinText, SwingConstants.CENTER);
        pinyinExample.setFont(gridPanel.getPinyinFont());

        otherExample = new JLabel(enText, SwingConstants.CENTER);
        otherExample.setFont(gridPanel.getOtherFont());


        chinese.setBackground(CharApp.COLOR_BUTTON);
        pinyin.setBackground(CharApp.COLOR_BUTTON);
        other.setBackground(CharApp.COLOR_BUTTON);
        display.setBackground(CharApp.COLOR_BUTTON);

        chinese.addActionListener(this);
        pinyin.addActionListener(this);
        other.addActionListener(this);
        resize.addActionListener(this);
        display.addActionListener(this);
        ok.addActionListener(this);

        JPanel temp = new JPanel();
        temp.setBackground(CharApp.COLOR_BG);
        temp.add(chinese);
        temp.add(chineseExample);
        fontPanel.add(temp);

        temp = new JPanel();
        temp.setBackground(CharApp.COLOR_BG);
        temp.add(pinyin);
        temp.add(pinyinExample);
        fontPanel.add(temp);

        temp = new JPanel();
        temp.setBackground(CharApp.COLOR_BG);
        temp.add(other);
        temp.add(otherExample);
        fontPanel.add(temp);

        // TODO - implement
//    temp = new JPanel();
//    temp.setBackground(CharApp.COLOR_BG);
//    temp.add(display);
//    fontPanel.add(temp);


        thiss.add(fontPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(ok);
        buttonPanel.add(resize);
        ok.setBackground(CharApp.COLOR_BUTTON);
        resize.setBackground(CharApp.COLOR_BUTTON);

        buttonPanel.setBackground(CharApp.COLOR_BG);
        thiss.add(buttonPanel, BorderLayout.SOUTH);
//    this.setSize(450, 350);
        this.pack();
        this.setLocation((int) getOwner().getLocation().getX() - 100,
                (int) getOwner().getLocation().getY() + getOwner().getHeight() / 3);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == display) {
        } else if (e.getSource() == ok) {
            this.setVisible(false);
            this.dispose();
        } else if (e.getSource() == resize) {
            gridPanel.initFonts();
            chineseExample.setFont(gridPanel.getChineseFont());
            pinyinExample.setFont(gridPanel.getPinyinFont());
            otherExample.setFont(gridPanel.getOtherFont());
            gridPanel.repaint();
        } else if (e.getSource() == chinese) {
            Font nf = JFontChooser.showDialog(this, "Choose Chinese Font", chText, gridPanel.getChineseFont());
            if (nf == null) {
                return;
            }
            chineseExample.setFont(nf);
            CharProps.getProperties().setProperty("font.ch.face", nf.getFontName());
            CharProps.getProperties().setProperty("font.ch.size", nf.getSize() + "");
            CharProps.getProperties().setProperty("font.ch.style", nf.getStyle() + "");
            gridPanel.setChineseFont(nf);
        } else if (e.getSource() == pinyin) {
            Font nf = JFontChooser.showDialog(this, "Choose Pinyin Font", pinText, gridPanel.getPinyinFont());
            if (nf == null) {
                return;
            }
            gridPanel.setPinyinFont(nf);
            CharProps.getProperties().setProperty("font.pinyin.face", nf.getFontName());
            CharProps.getProperties().setProperty("font.pinyin.size", nf.getSize() + "");
            CharProps.getProperties().setProperty("font.pinyin.style", nf.getStyle() + "");
            pinyinExample.setFont(nf);
        } else if (e.getSource() == other) {
            Font nf = JFontChooser.showDialog(this, "Choose other font", enText, gridPanel.getOtherFont());
            if (nf == null) {
                return;
            }
            CharProps.getProperties().setProperty("font.other.face", nf.getFontName());
            CharProps.getProperties().setProperty("font.other.size", nf.getSize() + "");
            CharProps.getProperties().setProperty("font.other.style", nf.getStyle() + "");
            gridPanel.setOtherFont(nf);
            otherExample.setFont(nf);
        }
        ListPanel.refreshFonts();
        CharApp.getInstance().getListPanel().repaint();
    }
}
