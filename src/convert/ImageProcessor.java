package convert;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JProgressBar;

import hanzihelper.CharApp;
import hanzihelper.CharRecord;
import hanzihelper.Record;
import hanzihelper.PinyinUtil;

import common.Glyph;
import common.StrokePoint;
import common.WordlistReader;

public class ImageProcessor {

    private String imageDir = "";
    private static final int IMG_WIDTH = 56;
    private static final int IMG_HEIGHT = 56;

    public ImageProcessor(String imageDir) {
        this.imageDir = imageDir;
        if (!this.imageDir.endsWith("/")) {
            this.imageDir = imageDir + "/";
        }
    }

    public static void makeImages(JProgressBar bar, File destDir,
            CharRecord record) throws Exception {

        int val = 0;
        for (Iterator it = record.getRecords(false).iterator(); it.hasNext();) {
            Record rec = (Record) it.next();
            String chinese = rec.getChars();
            char[] chars = chinese.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                char aChar = chars[i];
                try {
                    ImageProcessor.writeStrokeOrderImage("" + aChar, destDir
                            .getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
            val++;
            bar.setValue(val);
        }
    }

    public static void imageAggregator2(String[] chars, String outputFile) {
        String[] strokeImages = new String[chars.length];
        String[] zwImages = new String[chars.length];
        for (int i = 0; i < chars.length; i++) {
            String c = chars[i];
            String hex = Converter.convertToBig5HexString(Converter
                    .simplifiedToTrad("" + c));
            zwImages[i] = CharApp.cache + "/" + hex + ".png";
            String codePoint = Long.toHexString((long) c.charAt(0));
            strokeImages[i] = CharApp.cache + "/" + codePoint + ".png";
        }

        int width = 600;
        int height = 800;
        BufferedImage bi = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        int boxWidth = width;
        int boxHeight = height / chars.length;
        Image[] strkImage = new Image[chars.length];
        Image[] zwImage = new Image[chars.length];
        for (int i = 0; i < strkImage.length; i++) {
            strkImage[i] = new ImageIcon(strokeImages[i]).getImage();
            zwImage[i] = new ImageIcon(zwImages[i]).getImage();
        }

        Graphics g = bi.createGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.black);
        for (int i = 0; i < strkImage.length; i++) {
            g.drawLine(0, i * boxHeight, width, i * boxHeight);

            Image strokes = strkImage[i];
            Image zhongwen = zwImage[i];
            int x = 0;
            int y = 0;
            y += i * boxHeight;
            g.drawImage(zhongwen, 20, y + 10, null);
            int margin = 20 + zhongwen.getWidth(null);

            x += margin + (boxWidth - margin) / 2 - strkImage[i].getWidth(null) / 2;
            y += boxHeight / 2 - strkImage[i].getHeight(null) / 2;
            g.drawImage(strokes, x, y, null);
        }

        try {
            FileOutputStream fos = new FileOutputStream(outputFile);
            ImageIO.write(bi, "png", fos);
            fos.flush();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeStrokeOrderImage(String hanzi, String file) {
        Glyph glyph = null;
        StrokePoint[] pointsArray;
        try {
            glyph = new WordlistReader().loadGlyphFromClasspath(hanzi.charAt(0));
        } catch (Throwable e) {
        }
        if (glyph == null) {
//      System.out.println("Couldn't find a glyph for this character: " + hanzi);
            System.setErr(null);
            System.out.print(hanzi + "(" + Integer.toHexString((char) hanzi.charAt(0)) + "), ");

            return;
        }
        file = file + "/" + Long.toHexString((long) hanzi.charAt(0)) + ".png";

        java.util.List strokePoints = glyph.getStrokePoints();
        int j = 0;
        pointsArray = new StrokePoint[glyph.getStrokePointCount()];
        for (Iterator iterator = strokePoints.iterator(); iterator.hasNext(); j++) {
            pointsArray[j] = (StrokePoint) iterator.next();
        }
        // -- Count strokes
        int strokecount = 0;
        for (int i = 0; i < pointsArray.length; i++) {
            StrokePoint p = pointsArray[i];
            if (p.getType().equals("A")) {
                strokecount++;
            }
        }

        float w = IMG_WIDTH;
        float h = IMG_HEIGHT;
        int totalW = IMG_WIDTH * 5;
        int totalH = IMG_HEIGHT
                * (((strokecount / 5)) + (strokecount % 5 == 0 ? 0 : 1));

        BufferedImage bi = new BufferedImage(totalW, totalH,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bi.createGraphics();

        g2.setColor(Color.white);
        g2.fillRect(0, 0, totalW, totalH);

        g2.setStroke(new BasicStroke(4));
        // g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        // RenderingHints.VALUE_ANTIALIAS_ON);
        StrokePoint a = null;
        StrokePoint b = null;
        StrokePoint c = null;
        // draw the path
        GeneralPath path = new GeneralPath();
        int x0, y0;
        g2.setColor(Color.blue);

        for (int stroke = 0; stroke < strokecount; stroke++) {
            x0 = IMG_WIDTH * (stroke % 5);
            y0 = IMG_HEIGHT * (stroke / 5);
            g2.translate(x0, y0);
            int drawnStrokes = 0;
            for (int i = 0; i < pointsArray.length; i++) {
                StrokePoint p = pointsArray[i];
                if (drawnStrokes == stroke + 2) {
                    break;
                }
                if (p.getType().equals("A")) {
                    path.moveTo(w * p.getX(), h * p.getY());
                    drawnStrokes++;
                } else if (p.getType().equals("L")) {
                    path.lineTo(w * p.getX(), h * p.getY());
                } else if (p.getType().equals("B")) {
                    b = p;
                } else if (p.getType().equals("C")) {
                    c = p;
                } else if (p.getType().equals("D")) {
                    path.curveTo(w * b.getX(), h * b.getY(), w * c.getX(), h * c.getY(),
                            w * p.getX(), h * p.getY());
                }
            }
            g2.draw(path);
            g2.translate(-1 * x0, -1 * y0);
        }
        // g2.setColor(Color.blue);
        // g2.draw(path);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            ImageIO.write(bi, "png", fos);
            fos.flush();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
