package convert;

import net.coljac.util.ColTools;
import net.coljac.util.WebTools;
import hanzihelper.CharRecord;
import hanzihelper.Record;
import hanzihelper.CharApp;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * By Colin Jacobs, coljac@coljac.net Date: Jun 16, 2005 (c) 2005
 */
public class ZhongwenCom {

    private static String prefix = "http://zhongwen.com";
    private static String prefix2 = "http://zhongwen.com/cgi-bin/zipux2.cgi?b5=";
    private static String cache = CharApp.cache;

    public static void fetchImages(JProgressBar bar, CharRecord rec) throws IOException {
        StringBuffer sb = new StringBuffer();
        ColTools.setDebug(true);
        Collection c = rec.getRecords(false);
        Set doneChars = new HashSet();
        StringBuffer index = new StringBuffer("record#\tchar\tpinyin\tenglish\tstrokes\tradical\tradical number\tradicalname\n");

        int i = 0;
        for (Iterator iterator = c.iterator(); iterator.hasNext();) {
            if (bar != null) {
                bar.setValue(i);
            }
            Record record = (Record) iterator.next();

            char[] chars = record.getChars().toCharArray();
            for (int j = 0; j < chars.length; j++) {
                String hanzi = "" + chars[j];
                fetchAndSaveAnImage(hanzi);
            }
            i++;
        }

    }

    public static void fetchAndSaveAnImage(String zi) throws IOException {
        String hex = Converter.convertToBig5HexString(Converter.simplifiedToTrad(zi)).toUpperCase();
        String postfix = "%" + hex.substring(0, 2) + "%" + hex.substring(2);
        String texturl = prefix2 + postfix;
        if (!new File(cache + "/" + hex + ".gif").exists()) {
            String text = WebTools.getURLAsString(texturl);
            int i = text.indexOf("SRC=\"");
            if (i < 0) {
                return;
            }
            String path = text.substring(i + 10, text.indexOf(".htm", i));
//    SRC="../../d/168/d174.htm"
            URL imageURL = new URL(prefix + path + ".gif");
            WebTools.writeURLToFile(imageURL, cache + "/" + hex + ".gif");
        }
        ImageIcon icon = new ImageIcon(cache + "/" + hex + ".gif");
        int width = icon.getIconWidth();
        int height = 125;
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics g = bi.createGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, width, height);
        g.drawImage(icon.getImage(), 5, -20, null);

        try {
            FileOutputStream fos = new FileOutputStream(cache + "/" + hex + ".png");
            ImageIO.write(bi, "png", fos);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) throws Exception {
        fetchAndSaveAnImage("我");
//    char unicode = '车'; // che1
//    char unicode = '我'; // wo3
//    System.out.println(Converter.convertToBig5HexString(Converter.simplifiedToTrad(unicode + "")));

    }
}
