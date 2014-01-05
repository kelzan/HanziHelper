package supermemo;

/**
 * Test algorithm. Get it right, see it in a week. Get it wrong, see it tomorrow.
 */
public class MySuperMemo implements SuperMemoAlgorithm {

    public void testItem(Item item, int score) {
        if (score >= 3) {
            item.setInterval(7);
        } else {
            item.setInterval(1);
        }
    }
}
