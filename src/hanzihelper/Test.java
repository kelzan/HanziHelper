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

import junit.framework.TestCase;

public class Test extends TestCase {

    private static String pinyinVowelsLC = "āáǎàōóǒòēéěèīíǐìūúǔùǖǘǚǜü";
    private static String pinyinVowelsUC = "ĀÁǍÀŌÓǑÒĒÉĚÈ";

    public void testPinyin() throws Exception {

        assertTrue(PinyinUtil.isValidSyllable("ban4"));
        assertTrue(PinyinUtil.isValidSyllable("can"));
        assertTrue(PinyinUtil.isValidSyllable("cang"));
        assertTrue(PinyinUtil.isValidSyllable("chuang"));
        assertTrue(PinyinUtil.isValidSyllable("a1"));
        assertTrue(PinyinUtil.isValidSyllable("duan3"));
        assertTrue(PinyinUtil.isValidSyllable("mei"));
        assertTrue(PinyinUtil.isValidSyllable("ren"));
        assertTrue(PinyinUtil.isValidSyllable("zhe"));
        assertTrue(PinyinUtil.isValidSyllable("e"));
        assertTrue(!PinyinUtil.isValidSyllable("erg"));
        assertTrue(!PinyinUtil.isValidSyllable("fnord"));
        assertTrue(!PinyinUtil.isValidSyllable("hoepner"));
        assertTrue(!PinyinUtil.isValidSyllable("haueong"));
        assertTrue(!PinyinUtil.isValidSyllable("hng"));

        assertEquals("bàn", PinyinUtil.toUnicode("ban4"));
        assertEquals("rén", PinyinUtil.toUnicode("ren2"));
        assertEquals("bì", PinyinUtil.toUnicode("bi4"));
        assertEquals("měi", PinyinUtil.toUnicode("mei3"));
        assertEquals("Ān", PinyinUtil.toUnicode("An1"));
        assertEquals("Ě", PinyinUtil.toUnicode("E3"));
        assertEquals("hao", PinyinUtil.toUnicode("hao"));
        assertEquals("xué", PinyinUtil.toUnicode("xue2"));
        assertEquals("jiǔ", PinyinUtil.toUnicode("jiu3"));
        assertEquals("huì", PinyinUtil.toUnicode("hui4"));
        assertEquals("nǚ", PinyinUtil.toUnicode("nü3"));
        assertEquals("nǚ", PinyinUtil.toUnicode("nv3"));

        assertEquals("xià bān", PinyinUtil.toUnicode("xia4ban1"));
        assertEquals("wǎn shang", PinyinUtil.toUnicode("wan3shang"));
        assertEquals("shangban", PinyinUtil.toUnicode("shangban"));
        assertEquals("miàn tiáo", PinyinUtil.toUnicode("mian4tiao2"));
        assertEquals("cān guǎn", PinyinUtil.toUnicode("can1guan3"));
        assertEquals("yī diǎn er", PinyinUtil.toUnicode("yi1dianr3"));
        assertEquals("nǚ 'ér", PinyinUtil.toUnicode("nü3'er2"));
        assertEquals("diǎn rōng", PinyinUtil.toUnicode("dian3rong1"));
        assertEquals("dì fang", PinyinUtil.toUnicode("di4fang5"));

//    assertEquals("shangbǎn", PinyinUtil.toUnicode("shangban3"));   // Unlikely and hard to deal with

        assertTrue(arrayEquals(PinyinUtil.getSyllables("xia4"), new String[]{"xia4"}));
        assertTrue(arrayEquals(PinyinUtil.getSyllables("ban1"), new String[]{"ban1"}));
        assertTrue(arrayEquals(PinyinUtil.getSyllables("hao"), new String[]{"hao"}));
        assertTrue(arrayEquals(PinyinUtil.getSyllables("xia4ban1"), new String[]{"xia4", "ban1"}));
        assertTrue(arrayEquals(PinyinUtil.getSyllables("wan3shang"), new String[]{"wan3", "shang"}));
        assertTrue(arrayToString(PinyinUtil.getSyllables("lao3shi1")),
                arrayEquals(PinyinUtil.getSyllables("lao3shi1"), new String[]{"lao3", "shi1"}));
        assertTrue(arrayToString(PinyinUtil.getSyllables("shang5ban")),
                arrayEquals(PinyinUtil.getSyllables("shang5ban"), new String[]{"shang5", "ban"}));
        assertTrue(arrayEquals(PinyinUtil.getSyllables("mian4tiao2"), new String[]{"mian4", "tiao2"}));
        assertTrue(arrayEquals(PinyinUtil.getSyllables("can1guan3"), new String[]{"can1", "guan3"}));
        assertTrue(arrayEquals(PinyinUtil.getSyllables("yi1dianr3"), new String[]{"yi1", "dian3", "er"}));

    }

    private static boolean arrayEquals(String[] array1, String[] array2) {
        if (array1.length != array2.length) {
            System.err.println("Not equal lengths: " + array1.length + "/" + array2.length);
            return false;
        }
        for (int i = 0; i < array1.length; i++) {
            if (!(array1[i].equals(array2[i]))) {
                System.err.println("Not equal: " + array1 + "/" + array2);
                return false;
            }
        }
        return true;
    }

    private static String arrayToString(String[] array) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i] + "/");
        }
        return sb.toString();
    }
}
