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

import hanzihelper.CharRecord;
import hanzihelper.Record;
import hanzihelper.CharApp;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import palmos.MakePDB;

/**
 * This class separates the logic for exporting to a couple of other formats. SuperMemo is for the
 * Palm implementation of the SuperMemo flashcard software (sadly, not open source) by MapleTop
 * software. Dragon is a fantastic palm Hanzi testing application that *is* open source and can be
 * found at http://dragon-char.sf.net.
 */
public class RecordExport {

    /**
     * Tab-delimted records with GB-encoded characters. The output needs to go through the
     * SMCONV.exe util to make a PDB. Sorry... I'm working on an open-source replacement for that
     * thing
     */
    public static void superMemoExport(CharRecord rec, String file) throws IOException {
        Collection c = rec.getRecords(false);
        BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "GB2312"));
        for (Iterator iterator = c.iterator(); iterator.hasNext();) {
            Record record = (Record) iterator.next();
            br.write(record.getChars() + "\t" + record.getPinyin() + "\t" + record.getEnglish() + "\t"
                    + getPaddedNumber(record.getOrder() + "", 3) + "\t\t\t" + record.getBook() + "\n");
        }
        br.close();
    }

    private static String getPaddedNumber(String number, int padTo) {
        while (number.length() < padTo) {
            number = "0" + number;
        }
        return number;
    }

    public static String getDragonWords(CharRecord records) {
        List list = new ArrayList(records.getRecordCount());
        list.addAll(records.getRecords(false));
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            Record record = (Record) list.get(i);
            String chars = record.getChars();
            String pinyin = record.getPinyin();
            String english = record.getEnglish();
            String word = charsToUnicodePoints(chars);
            sb.append("word " + word + " pinyin " + pinyin + " verb en \"" + english + "\" end\n");
        }
        String result = sb.toString();
        result = result.replaceAll("ü", "v");
        return result;
    }

    private static String charsToUnicodePoints(String unicode) {
        StringBuffer sb = new StringBuffer();
        char[] chars = unicode.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            sb.append("u");
            sb.append(Long.toHexString((long) c));
            sb.append(" ");
        }
        return sb.toString();
    }

    // String language, String charset, String wordString, String
    // glyphPath, String dbname, File wordFile
    public static void dragonExport(CharRecord record, String filename) {
        int i = filename.lastIndexOf("\\");
        int j = filename.lastIndexOf("/");
        int k = filename.lastIndexOf(".");
        String dbName = filename.substring(Math.max(i, j) + 1, k);
        String wordString = getDragonWords(record);
        try {
            new MakePDB().buildPDB("en", "simplified", wordString,
                    "/java/dragon/dragon-data/characters",
                    dbName, null, filename);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            CharApp.getInstance().showErrorMessage("Couldn't save PDB file: "
                    + e.getMessage());
        }
    }

    public static void textExport(CharRecord rec, String filename) throws IOException {
        Collection c = rec.getRecords(false);
        BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
        for (Iterator iterator = c.iterator(); iterator.hasNext();) {
            Record record = (Record) iterator.next();
            br.write(record.getChars());
        }
        br.flush();
        br.close();
    }

    public static void plecoExport(CharRecord rec, String filename) throws IOException {
        Collection c = rec.getRecords(false);
        BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
        for (Iterator iterator = c.iterator(); iterator.hasNext();) {
            Record record = (Record) iterator.next();
            br.write(record.getChars() + "\t" + record.getPinyinAsUnicode() + "\t" + record.getEnglish() + "\n");
        }
        br.flush();
        br.close();
    }

    /**
     * Exports to VTrain - side one is Chinese and pinyin, side two is English. Delimiters are the
     * default =, |
     *
     * @param rec
     * @param filename
     * @throws IOException
     */
    public static void vtrainExport1(CharRecord rec, String filename, boolean one) throws IOException {
        Collection c = rec.getRecords(false);
        FileOutputStream fis = new FileOutputStream(filename);
        fis.write(Converter.BOM);
        OutputStreamWriter osw = new OutputStreamWriter(fis, "UTF-8");
        PrintWriter bw = new PrintWriter(new BufferedWriter(osw));
        boolean whitespace = false;
        for (Iterator iterator = c.iterator(); iterator.hasNext();) {
            Record record = (Record) iterator.next();
            if (!whitespace) { // Vtrain chokes on unicode if the first char is chinese
                bw.print(" ");
                whitespace = true;
            }
            if (one) {
                bw.print(record.getChars() + " \r\n(" + record.getPinyin() + ")=");
                bw.println(record.getEnglish() + "|");
            } else {
                bw.print(record.getChars() + "=");
                bw.println(record.getPinyin() + " \r\n " + record.getEnglish() + "|");

            }
        }
        bw.flush();
        bw.close();
    }
}
