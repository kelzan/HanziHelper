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

import common.Glyph;
import common.StrokePoint;
import common.WordlistReader;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.geom.GeneralPath;
import java.util.Iterator;
import java.util.List;

/**
 * Displays the stroke order for a chacter using data from Dragon-Char
 * (http://dragon-char.sourceforge.net) glyph files.
 *
 * This class contains some code blatantly stolen from Dragon Char's Editor.java. Please direct all
 * kudos to the orginal authors.
 */
public class StrokeOrderPanel extends Canvas implements Runnable {

    private Glyph glyph;
    private boolean running = true;
    private StrokePoint[] pointsArray;
    private int currentIndex = 0;

    public StrokeOrderPanel(char ch) throws Exception {
        this.setBackground(Color.white);
        this.setForeground(Color.black);
        try {
            glyph = new WordlistReader().loadGlyphFromClasspath(ch);
        } catch (Throwable e) {
            glyph = null;
        }
        if (glyph == null) {
            throw new Exception("No glyph data available.");
        }
        pointsArray = new StrokePoint[glyph.getStrokePointCount()];
        init();

        this.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                running = false;
                if (e.getButton() == MouseEvent.BUTTON3) {
                    currentIndex = 0;
                } else {
                    currentIndex++;
                    while (currentIndex < pointsArray.length
                            && !pointsArray[currentIndex].getType().equals("A")) {
                        currentIndex++;
                    }
                }
                if (currentIndex >= pointsArray.length) {
                    currentIndex = pointsArray.length - 1;
                }

                repaint();
            }
        });

        new Thread(this).start();
    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        float w = getWidth();
        float h = getHeight();

        g2.setStroke(new BasicStroke(7));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        StrokePoint a = null;
        StrokePoint b = null;
        StrokePoint c = null;
        // draw the path
        GeneralPath path = new GeneralPath();
        for (int i = 0; i < pointsArray.length; i++) {
            if (i > currentIndex) {
                break;
            }
            StrokePoint p = pointsArray[i];

            if (p.getType().equals("A")) {
                path.moveTo(w * p.getX(), h * p.getY());
            } else if (p.getType().equals("L")) {
                path.lineTo(w * p.getX(), h * p.getY());
            } else if (p.getType().equals("B")) {
                b = p;
            } else if (p.getType().equals("C")) {
                c = p;
            } else if (p.getType().equals("D")) {
                path.curveTo(w * b.getX(),
                        h * b.getY(),
                        w * c.getX(),
                        h * c.getY(),
                        w * p.getX(),
                        h * p.getY());
            }
        }
        g2.setColor(Color.blue);
        g2.draw(path);
    }

    public void run() {
        while (running) {
            currentIndex = 0;

            while (currentIndex < pointsArray.length) {
                StrokePoint strokePoint = pointsArray[currentIndex];
                if (strokePoint.getType().equals("A")) {
                    try {
                        Thread.sleep(750);
                        if (!running) {
                            return;
                        }
                    } catch (InterruptedException e) {
                    }
                }
                repaint();
                currentIndex++;
            }
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
            }
        }
    }

    public void init() {
        List strokePoints = glyph.getStrokePoints();
        int i = 0;
        for (Iterator iterator = strokePoints.iterator(); iterator.hasNext(); i++) {
            pointsArray[i] = (StrokePoint) iterator.next();
        }
    }
}
