package convert;

import net.coljac.util.ColTools;
import net.coljac.util.FileTools;
import net.coljac.util.WebTools;
import hanzihelper.CharApp;
import hanzihelper.CharRecord;
import hanzihelper.PinyinUtil;
import hanzihelper.Record;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * By Colin Jacobs, coljac@coljac.net Date: Jun 8, 2005 (c) 2005
 */
public class ExcelExport {

    static Map radicals = new HashMap();
    static String wholeTemplate;
    static String recordTemplate;
    static String preamble;
    static String postScript;
    static String defaultStyle;
    static String altStyle;

    static {
//    wholeTemplate = FileTools.getFileContentsAsString("chintemplate2.xml");
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(ExcelExport.class.getResourceAsStream("/chintemplate2.xml")));
        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
        } catch (IOException e) {
        }
        wholeTemplate = sb.toString();
        int start = wholeTemplate.indexOf("<Row");
        int end = wholeTemplate.lastIndexOf("</Row>");
        recordTemplate = wholeTemplate.substring(start, end + 6);
        recordTemplate = recordTemplate.replaceFirst("<Row ss:Index=\"4\">", "<Row ss:Index=\"\\$\\{rownum\\}\">");
        int a = wholeTemplate.indexOf("${english}");
        int b = wholeTemplate.indexOf("${alternate}");
//    String regex = "ss:StyleID=\"([^\"*])\"";
        String temp1 = wholeTemplate.substring(a - 75, a);
        int c = temp1.lastIndexOf("ss:StyleID=\"");
        if (c > 0) {
            defaultStyle = temp1.substring(c + 12, temp1.indexOf("\"", c + 12));
            recordTemplate = recordTemplate.replaceAll("ss:StyleID=\"" + defaultStyle + "\"", "ss:StyleID=\"\\$\\{engstyle\\}\"");
        }
        temp1 = wholeTemplate.substring(b - 75, b);
        c = temp1.lastIndexOf("ss:StyleID=\"");
        if (c > 0) {
            altStyle = temp1.substring(c + 12, temp1.indexOf("\"", c + 12));
        }

        preamble = wholeTemplate.substring(0, start);
        postScript = wholeTemplate.substring(end + 6);
    }

    public static void writeExcelXML(JProgressBar bar, CharRecord rec, String file) throws IOException {
        StringBuffer sb = new StringBuffer();
        ColTools.setDebug(true);
        Collection c = rec.getRecords(false);

        Set doneChars = new HashSet();
        StringBuffer index = new StringBuffer("rec#\tchar\tpinyin\tenglish\tstrks\trad\trad#\tname\n");

        int i = 0;
        int charCount = 0;
        String[] images = new String[3];
        String[] pageChars = new String[3];
        for (Iterator iterator = c.iterator(); iterator.hasNext();) {
            if (bar != null) {
                bar.setValue(i);
            }
            Record record = (Record) iterator.next();

            char[] chars = record.getChars().toCharArray();
            for (int j = 0; j < chars.length; j++) {
                // firstcell, pinyin, english, char, strokes, radname, radeng, radnum, radchar
                // example, exampletext, sourceref, count
                Map values = new HashMap();
                values.put("engstyle", defaultStyle);
                String english = record.getEnglish();

                String hanzi = "" + chars[j];
                if (doneChars.contains(hanzi)) {
                    continue;
                }
                charCount++;

                doneChars.add(hanzi);
                int strokes = 0;

                String unicodePoint = Long.toHexString((long) hanzi.charAt(0));
                String url = "http://www.unicode.org/cgi-bin/GetUnihanData.pl?codepoint=" + unicodePoint;
                String result = getUrlCached(url);
                if (result == null) {
                    System.err.println("No go for " + hanzi + " - " + url);
                    continue;
                }

                // Get strokes
                int k = result.indexOf("<th>Total Strokes</th>");
                int l = result.indexOf("<tr>", k);
                l = result.indexOf("</tr>", l + 10);

                String s = result.substring(k, l);
                k = s.indexOf("<td");
                String definition = s.substring(k + 17, s.indexOf("</td>", k));
                l = s.indexOf("<td", k + 1);
                String number = s.substring(l + 17, s.indexOf("</td>", l));
                strokes = Integer.parseInt(number.trim());

                // Get radical index
                k = result.indexOf("Radical-stroke Counts");
                l = result.indexOf("Morohashi</th>", k);
                k = result.indexOf("<td", l);
                String unicodeRadical = result.substring(k + 17, result.indexOf("</td>", k + 1)).trim();
                RadicalInfo rad = getRadicalInfo(unicodeRadical.substring(0, unicodeRadical.indexOf(".")));

                // Compounds
                k = result.indexOf("Chinese Compounds");
                String compound1 = "";
                String compound1Text = "";
                if (k > 0) {
                    l = result.indexOf("<tr>", k);
                    k = result.indexOf("<td", l);
                    compound1 = result.substring(k + 17, result.indexOf("</td>", k)).trim();
                    k = result.indexOf("<td", k + 17);
                    k = result.indexOf("<td", k + 1);
                    k = result.indexOf("<td", k + 1);
                    compound1Text = result.substring(k + 17, result.indexOf("</td>", k)).trim();
                }

                if (record.getChars().length() > 1) {
                    // Multi-char record
                    // Get the definition for this char.
                    english = definition;
                }
                values.put("char", hanzi);
                String syllable = PinyinUtil.getSyllables(record.getPinyin())[j];
                String pinyin = PinyinUtil.toUnicode(syllable);
                values.put("pinyin", pinyin);

                index.append(charCount + "\t" + hanzi + "\t" + pinyin + "\t" + english.replaceAll("\n", " ") + "\t" + strokes + "\t" + rad.radicalChar + "\t" + rad.number + "\t" + rad.english + "\n");

                values.put("english", english);
                if (english.length() > 40) {
                    values.put("engstyle", altStyle);
                }
                values.put("strokes", strokes + "");
                values.put("radeng", rad.english);
                values.put("radchar", rad.radicalChar);
                values.put("radnum", rad.number);
                values.put("radname", rad.charPinyin);
                values.put("examplechars", compound1);
                values.put("exampletext", xmlSafe(compound1Text));
                values.put("sourceref", record.getBook() + "/" + record.getChapter());
                values.put("count", "" + charCount);
                values.put("rownum", "" + ((18 * (charCount - 1) + 4)));
                if (record.getChars().length() > 1) {
                    values.put("examplechars2", record.getChars());
                    values.put("exampletext2", record.getEnglish());
                }
                values.put("trad", Converter.simplifiedToTrad(hanzi));
                sb.append(replaceValues(values, recordTemplate));

                // images
                images[(charCount - 1) % 3] = CharApp.cache + "/" + Long.toHexString((long) hanzi.charAt(0)).toLowerCase() + ".png";
                pageChars[(charCount - 1) % 3] = hanzi;
                if (CharApp.development) {
                    try {
//          ImageProcessor.makeImage("./cache/", hanzi);
                        ImageProcessor.writeStrokeOrderImage(hanzi, CharApp.cache);
                        String hex = Long.toHexString((long) hanzi.charAt(0)).toLowerCase();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (charCount % 3 == 0) {
                        String imageName = "" + charCount / 3;
                        while (imageName.length() < 3) {
                            imageName = "0" + imageName;
                        }
                        imageName = "new_" + imageName + ".png";
//          ImageProcessor.imageAggregator(images, CharApp.cache + "/ " + imageName);
                        ImageProcessor.imageAggregator2(pageChars, CharApp.cache + "/" + imageName);
                    }
                }
            }
            i++;
        }

        int rowcount = 18 * charCount;
        sb.insert(0, preamble.replaceAll("ss:ExpandedRowCount=\"[0-9]+\"", "ss:ExpandedRowCount=\"" + rowcount + "\""));
        sb.append(postScript);

        PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8")));
        bw.print(sb.toString());
        bw.flush();
        bw.close();
        bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file + ".index.txt"), "UTF-8")));
        bw.print(index.toString());
        bw.flush();
        bw.close();
    }

    private static String replaceValues(Map values, String template) {
        String regex = "\\$\\{[A-Za-z0-9]+\\}";
        // Compile and use regular expression
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(template);
        String copy = template;

        boolean found = false;
        while (matcher.find()) {
            // Get the match result
            String replaceStr = matcher.group();
            String key = replaceStr.substring(2, replaceStr.indexOf("}"));
            String value = (String) values.get(key);
            if (value == null) {
                value = "";
            }
            copy = copy.replaceAll("\\$\\{" + key + "\\}", value);
        }
        return copy;
    }

    private static String xmlSafe(String in) {
        String out = in.replaceAll("<", "&lt;");
        out = in.replaceAll(">", "&gt;");
        return out;
    }

    private static RadicalInfo getRadicalInfo(String radicalNumber) {

        RadicalInfo rad = (RadicalInfo) radicals.get(radicalNumber);
        if (rad == null) {

            String result = getUrlCached("http://www.unicode.org/cgi-bin/UnihanRSIndex.pl?radical=" + radicalNumber + "&minstrokes=0&maxstrokes=1&useutf8=true");
            int j = result.indexOf("<h2>Radical #");
            int k = result.indexOf("(", j + 13);
            int l = result.indexOf(")", j);
            String english = result.substring(k + 1, l);
            k = result.indexOf("src=\"", l);
            l = result.indexOf("\">", k);
            String codePoint = result.substring(l - 4, l);
            char it = (char) Long.parseLong(codePoint, 16);

            // Try and get the character info
            String charChar = null;
            String charPinyin = "";

            j = result.indexOf("<td align=center>0</td>");
            if (j > 0) {
                k = result.indexOf("<a href", j); // First zero-stroke char
                l = result.indexOf("codepoint=", k);
                String charCodePoint = result.substring(l + 10, l + 14);

                charChar = "" + (char) Long.parseLong(charCodePoint, 16);
                String furtherResult = getUrlCached("http://www.unicode.org/cgi-bin/GetUnihanData.pl?codepoint=" + charCodePoint + "&useutf8=true");
                k = furtherResult.indexOf("Phonetic Data");
                if (k > 0) {
                    int tableStart = furtherResult.indexOf("<table", k);
                    int tableEnd = furtherResult.indexOf("</table>", k);
                    String table = furtherResult.substring(tableStart, tableEnd);
                    String[] tokens = table.split("<td align=center>");
                    for (int i = 0; i < tokens.length; i++) {
                        String tok = tokens[i];
                        if (i == 2) {
                            charPinyin = tok.substring(0, tok.indexOf("<"));
                            charPinyin = charPinyin.trim().toLowerCase();
                            charPinyin = PinyinUtil.toUnicode(charPinyin);
                            if (charPinyin.indexOf(" ") >= 0) {
                                charPinyin = charPinyin.substring(0, charPinyin.indexOf(" "));
                            }
                            break;
                        }
                    }
                }
            }

            rad = new RadicalInfo("" + it, charChar, radicalNumber, english, charPinyin);

            radicals.put(radicalNumber, rad);
        }

        return rad;
    }

//  static class RecordFromWeb {
//    String hanzi;
//    String pinyinUnicode;
//    String radical;
//    String exampleChars;
//    String exampleEnglish;
//    int strokes;
//
//    public RecordFromWeb(String hanzi, String pinyinUnicode, String radical,
//                         String exampleChars, String exampleEnglish, int strokes) {
//      this.hanzi = hanzi;
//      this.pinyinUnicode = pinyinUnicode;
//      this.radical = radical;
//      this.exampleChars = exampleChars;
//      this.exampleEnglish = exampleEnglish;
//      this.strokes = strokes;
//    }
//  }
    static class RadicalInfo {

        String radicalChar;
        String charChar;
        String number;
        String english;
        String charPinyin;

        public RadicalInfo(String radicalChar, String charChar, String number, String english, String charPinyin) {
            this.radicalChar = radicalChar;
            this.charChar = charChar;
            this.number = number;
            this.english = english;
            this.charPinyin = charPinyin;
        }

        public String toString() {
            return radicalChar + "/" + charChar + "/" + number + "/" + english + "/" + charPinyin;
        }
    }
    static boolean makeDir = true;

    private static String getUrlCached(String url) {
        if (makeDir && !new File(CharApp.cache).exists()) {
            new File(CharApp.cache).mkdir();
            makeDir = true;
        }
        String prefix = CharApp.cache + "/";
        int hashCode = url.hashCode();
        File file = new File(prefix + hashCode);
        if (!file.exists()) {
            String result = WebTools.getURLAsString(url);
            if (result != null) {
                FileTools.writeFileUTF8(prefix + hashCode, result);
            }
            return result;
        } else {
            return FileTools.getFileContentsAsString(prefix + hashCode, "UTF-8");
        }
    }

    public static void main(String[] args) {
        try {
            Map map = new HashMap();
            map.put("name", "Col");
            map.put("age", "29");

            System.out.println(replaceValues(map, "My name is ${name}, and my age is ${age}, I like to eat ${food}."));
        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
