/*
 * Copyright (C) 2014 Daddy.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package hanzihelper;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Daddy
 */
public class FlashcardDeck {

    int numRepeat = 2;
    int numPenalty = 3;
    List<Record> flashcards = new LinkedList<Record>();

    int totalCards;
    int totalUnique;
    int totalCorrect;
    int totalWrong;
    int currentCardOffset;

    public FlashcardDeck(CharRecord records) {
        Collection c = records.getRecords(false);
        Iterator iterator = c.iterator();
        while (iterator.hasNext()) {
            Record record = (Record) iterator.next();
            totalUnique++;
            for (int i = 0; i < numRepeat; i++) {
                flashcards.add(record);
                totalCards++;
            }
        }
        Collections.shuffle(flashcards);
    }

    public void answerCorrect() {
        totalCorrect++;
        currentCardOffset++;
    }

    public void answerWrong() {
        Record wrongFlashcard = flashcards.get(currentCardOffset);
        totalWrong++;
        flashcards.subList(0, currentCardOffset + 1).clear();
        for (int i = 0; i < numPenalty; i++) {
            flashcards.add(wrongFlashcard);
            totalCards++;
        }
        Collections.shuffle(flashcards);
        currentCardOffset = 0;
    }

    public boolean hasNext() {
        return (currentCardOffset < flashcards.size());
    }

    public String getChars() {
        return (flashcards.get(currentCardOffset).getChars());
    }

    public String getPinyin() {
        return ("<html>" + flashcards.get(currentCardOffset).getPinyinColorized() + "</html>");
    }

    public String getDefinition() {
        return (flashcards.get(currentCardOffset).getEnglish());
    }

    public String getBookAndChapter() {
        return (flashcards.get(currentCardOffset).getChapterFormatted());
    }

    public String getCardCount() {
        return (String.format("%d/%02d", totalCorrect + totalWrong + 1, totalCards));
    }

}
