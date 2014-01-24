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

import java.util.HashSet;
import java.util.Set;

/**
 * A character (or characters) and the pinyin and english representations.
 */
public class Record implements Comparable {

    static Set books = new HashSet(3);
    private int order;
    private String pinyin;
    private String chars;
    private String trad;
    private String english;
    private String unicode = null;
    private String book = "";
    private String chapter = "";
    private String extra1;

    public Record(int order, String pinyin, String chars, String trad, String english, String book, String chapter) {
        this.order = order;
        this.pinyin = pinyin;
        this.chars = chars;
        this.trad = trad;
        this.english = english;
        this.book = book;
        this.chapter = chapter;
    }

    public Record(int order, String pinyin, String chars, String trad, String english, String book, String chapter,
            String extra1) {
        this(order, pinyin, chars, trad, english, book, chapter);
        this.extra1 = extra1;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getPinyin() {
        return pinyin;
    }

    public String getPinyinAsUnicode() {
        if (unicode == null) {
            unicode = PinyinUtil.toUnicode(pinyin);
        }
        return unicode;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
        unicode = null;
    }

    public String getChars() {
        return chars;
    }

    public void setChars(String chars) {
        this.chars = chars;
    }

    public String getTrad() {
        return trad;
    }

    public void setTrad(String trad) {
        this.trad = trad;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public int compareTo(Object o) {
        return this.order - ((Record) o).order;
    }

    public String toString() {
        return /*order + "," +*/ pinyin + "\t" + chars + "\t" + trad + "\t" + english + "\t" + book + "\t" + chapter;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public String getExtra1() {
        return extra1;
    }

    public void setExtra1(String extra1) {
        this.extra1 = extra1;
    }

    public int getLength() {
        return chars.length();
    }
}
