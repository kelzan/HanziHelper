package supermemo;

import java.io.Serializable;
import java.util.Date;

/**
 * An item in a set of items to be memorized with supermemo.
 */
public class Item implements Serializable {

    private double EF = 2.5;
    private String question = "";
    private String answer = "";
    private String extra = "";
    private int interval = 0;
    private int n = 1;
    private Date lastSeen;

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public double getEF() {
        return EF;
    }

    public void setEF(double EF) {
        this.EF = EF;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String toString() {
        return "Q:" + question + " , A: " + answer + ", Interval: " + interval + " , n: " + n
                + ", EF: " + EF;
    }
}
