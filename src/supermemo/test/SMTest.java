package supermemo.test;

import junit.framework.TestCase;
import supermemo.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: coljac Date: Mar 1, 2005 Time: 6:18:09 AM To change this template
 * use File | Settings | File Templates.
 */
public class SMTest extends TestCase {

    public void testSM() throws Exception {
        List items = new ArrayList();

        for (int i = 0; i < 5; i++) {
            Item item = new Item();
            item.setEF(2.5);
            item.setAnswer("" + i);
            item.setQuestion("" + i);
            item.setN(1);
            item.setInterval(0);
            items.add(item);
        }

//    SM2 sm = new SM2(items);
//    sm.run();
//    sm.run();

    }

    public static void main(String[] args) {
        try {
            SMTest sm = new SMTest();
            sm.testSM();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
