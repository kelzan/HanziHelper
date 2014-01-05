package supermemo;

import java.io.Serializable;
import java.util.*;

/**
 * Represents a collection of items to be memorized
 */
public class SMDatabase implements Serializable {

    private Date startDate;
    private Date today;
//  private Date lastUsed;
    List todaysItems;
    List allItems;

    public SMDatabase(List allItems, Date startDate) {
        this.allItems = allItems;
        this.startDate = startDate;
        today = new Date();
    }

    public SMDatabase(List allItems) {
        this.allItems = allItems;
        today = new Date();
        this.startDate = today;
//    lastUsed = today;

        for (int i = 0; i < allItems.size(); i++) {
            Item item = (Item) allItems.get(i);
            item.setEF(2.5);
            item.setLastSeen(today);
            if (i < allItems.size() / 2) {
                item.setInterval(1);
            } else {
                item.setInterval(2);
            }
        }
    }

    public List getTodays() {
        todaysItems = new ArrayList();
        for (Iterator it = allItems.iterator(); it.hasNext();) {
            Item item = (Item) it.next();
            int interval = getDaysBetween(today, item.getLastSeen());
            if (item.getInterval() <= interval) {
                todaysItems.add(item);
            }
        }
        return todaysItems;
    }

    public int getDaysBetween(Date later, Date earlier) {
        Calendar calFirst = new GregorianCalendar();
        calFirst.setTime(later);
        Calendar calSecond = new GregorianCalendar();
        calSecond.setTime(earlier);
        int firstYear = calFirst.get(Calendar.YEAR);
        int firstDay = calFirst.get(Calendar.DAY_OF_YEAR);
        int secondYear = calSecond.get(Calendar.YEAR);
        int secondDay = calSecond.get(Calendar.DAY_OF_YEAR);
        return ((firstYear - secondYear) * 365) + (firstDay - secondDay);
    }

    /**
     * Mainly for test purposes
     */
    public Date getToday() {
        return today;
    }

    /**
     * For test purposes
     */
    public void setToday(Date today) {
        this.today = today;
    }

    public Date getStartDate() {
        return startDate;
    }
}
