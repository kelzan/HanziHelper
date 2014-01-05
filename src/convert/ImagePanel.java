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
package convert;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @deprecated - Ocrat.com no longer exists.
 * @author coljac
 *
 */
public class ImagePanel extends JPanel {

    private Image image;

    public boolean imageUpdate(Image img, int infoflags, int x, int y, int w, int h) {
        return super.imageUpdate(img, infoflags, x, y, w, h);
    }

    public ImagePanel(String characterCode) {
        Toolkit kit = Toolkit.getDefaultToolkit();
        try {
            // TODO ocrat
            image = kit.getImage(new URL("http://www.ocrat.com/chargif/GB48h/anim/"
                    + characterCode + ".gif"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        this.setSize(56, 56);
        this.setMinimumSize(new Dimension(56, 56));

    }

    public void paint(Graphics g) {
        super.paint(g);
        this.setForeground(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());
        this.setForeground(Color.white);
//    this.setBackground(Color.white);
        if (image == null) {
            g.drawString("Not found", 10, 10);
        } else {
            g.drawImage(image, 0, 0, this);
        }
    }
}
