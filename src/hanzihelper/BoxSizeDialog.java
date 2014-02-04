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

/**
 * A little dialog to let the user input box height and width for the printable sheets.
 */
public class BoxSizeDialog extends JDialog implements ActionListener {

    private JTextField w, h;
    private JButton ok, cancel;
    private JLabel wLabel, hLabel;
    private GridPanel panel;

    public BoxSizeDialog(Dialog owner, GridPanel panel) throws HeadlessException {
        super(owner);
        this.panel = panel;
        setTitle("Set box size");
        Container contentPane = getContentPane();
        contentPane.setLayout(new FlowLayout());
        contentPane.setBackground(CharApp.COLOR_BG);

        ok = new JButton("OK");
        cancel = new JButton("Cancel");
        ok.setBackground(CharApp.COLOR_BUTTON);
        cancel.setBackground(CharApp.COLOR_BUTTON);

        wLabel = new JLabel("W:");
        hLabel = new JLabel("H:");
        w = new JTextField(panel.getBoxWInches() + "", 4);
        h = new JTextField(panel.getBoxHInches() + "", 4);
        JLabel label = new JLabel("Set box size in inches:");
        contentPane.add(label);
        contentPane.add(wLabel);
        contentPane.add(w);
        contentPane.add(hLabel);
        contentPane.add(h);
        contentPane.add(ok);
        contentPane.add(cancel);

        ok.addActionListener(this);
        cancel.addActionListener(this);

        this.pack();
        this.setLocation((int) owner.getLocation().getX() + 50, (int) owner.getLocation().getY() + 50);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cancel) {
            this.setVisible(false);
            this.dispose();
        } else if (e.getSource() == ok) {
            wLabel.setForeground(Color.black);
            hLabel.setForeground(Color.black);
            boolean ok = true;
            double width = 0.5, height = 0.5;
            try {
                width = Double.parseDouble(w.getText());
            } catch (NumberFormatException e1) {
                wLabel.setForeground(Color.red);
                ok = false;
            }
            try {
                height = Double.parseDouble(h.getText());
            } catch (NumberFormatException e1) {
                hLabel.setForeground(Color.red);
                ok = false;
            }
            if (ok) {
                panel.setBoxHInches(height);
                panel.setBoxWInches(width);
                panel.repaint();
                CharProps.getProperties().setProperty("box.width", width + "");
                CharProps.getProperties().setProperty("box.height", height + "");
                this.setVisible(false);
                this.dispose();
            }
        }
    }
}
