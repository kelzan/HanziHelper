package supermemo;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

/**
 * By Colin Jacobs, coljac@coljac.net Date: Apr 21, 2005 (c) 2005
 */
public class SMEngine {

    static InputStreamReader converter = new InputStreamReader(System.in);
    static BufferedReader in = new BufferedReader(converter);
    private SMDatabase db;
//  private static SuperMemoAlgorithm algorithm = new MySuperMemo();
    private static SuperMemoAlgorithm algorithm = new SM2();

    public void openDB(String dbFile) throws IOException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dbFile));
        try {
            db = (SMDatabase) ois.readObject();
        } catch (ClassNotFoundException e) {
        }
    }

    public void saveDB(String file) throws IOException {
        if (db == null) {
            return;
        }
        new ObjectOutputStream(new FileOutputStream(file)).writeObject(db);
    }

    public void test() {
        if (db == null) {
            return;
        }

        List today = db.getTodays();
        Date todaysDate = db.getToday();
        for (Iterator iterator = today.iterator(); iterator.hasNext();) {
            Item item = (Item) iterator.next();
            System.out.println("    Item before: " + item);
            System.out.println(item.getQuestion() + "/" + item.getAnswer());
            int q = -1;
            try {
                q = Integer.parseInt(getString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (q < 0 || q > 5) {
                System.out.println("Error: response must be in range 0-5.");
                q = 0;
            }
            algorithm.testItem(item, q);
            item.setLastSeen(todaysDate);
            System.out.println("    Item after: " + item);
        }
    }

// Read a String from standard system input
    public static String getString() {
        try {
            return in.readLine();
        } catch (Exception e) {
            System.out.println("getString() exception, returning empty string");
            return "";
        }
    }

    public static void main(String[] args) {
//     Create a test db
        List list = new ArrayList();
        for (int i = 1; i <= 10; i++) {
            Item item = new Item();
            item.setQuestion("What is " + i);
            item.setAnswer(i + " is " + i);
            list.add(item);
        }
        SMDatabase testDB = new SMDatabase(list);
        try {
            new ObjectOutputStream(new FileOutputStream("db.dat")).writeObject(testDB);
        } catch (IOException e) {
            e.printStackTrace();
        }


        SMEngine engine = new SMEngine();
        try {
            engine.openDB("db.dat");
            for (int i = 0; i < 100; i++) {
                System.out.println("======  Day: " + engine.db.getToday() + " ==========");
                engine.test();
                engine.db.setToday(new Date(engine.db.getToday().getTime() + (24 * 3600 * 1000)));
            }
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
