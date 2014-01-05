package supermemo;

/**
 * Implementation of the algorithm found at: http://www.supermemo.com/english/ol/sm2.htm
 */
public class SM2 implements SuperMemoAlgorithm {

    public void testItem(Item item, int score) {
        item.setEF(EF(score, item));
        item.setInterval(interval(item.getN(), item));
        item.setN(item.getN() + 1);
    }

    public SM2() {
    }

    private int interval(int n, Item item) {
        if (n == 1) {
            return 1;
        }
        if (n == 2) {
            return 6;
        }
        int newInterval = (int) (interval(n - 1, item) * item.getEF());
        return newInterval;
    }

    private double EF(int q, Item item) {
        double newEF = item.getEF() - 0.8 + 0.28 * q - 0.02 * q * q;
        if (newEF > 2.5) {
            return 2.5;
        } else if (newEF < 1.3) {
            return 1.3;
        }
        return newEF;
    }
}
