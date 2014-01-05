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
package reminder;

import hanzihelper.CharProps;
import hanzihelper.CharRecord;
import hanzihelper.Record;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class ReminderPanel extends JPanel implements Runnable {

    private CharRecord record;
    private int index = 0;
    private boolean randomOrder = false;
    private Record currentRecord = null;
    private Random rand = new Random();
    private boolean running = false;
    private int delay = 1;
    private Color onColor, offColor;
    private JLabel theChar, pinyin, english;
    Font pinyinFont = new Font(CharProps.getProperty("font.pinyin.face"), Font.PLAIN, 15);
    Font englishFont = new Font(CharProps.getProperty("font.pinyin.face"), Font.PLAIN, 8);
    Font chineseFont = new Font(CharProps.getProperty("font.ch.face"), Font.PLAIN, 45);
    Font chineseFont2 = new Font(CharProps.getProperty("font.ch.face"), Font.PLAIN, 23);
    Font chineseFont3 = new Font(CharProps.getProperty("font.ch.face"), Font.PLAIN, 18);

    public ReminderPanel(CharRecord chars) {
        super(new GridLayout(2, 1));
        this.record = chars;
        onColor = Color.white;
        offColor = new Color(220, 220, 220);
        this.setBackground(offColor);
        this.setForeground(Color.BLACK);
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        next();
        this.setMaximumSize(new Dimension(200, 400));

        delay = CharProps.getIntProperty("reminder.delay", 10);
        CharProps.getProperties().setProperty("reminder.delay", "" + delay);
        CharProps.storeProps();
        String randomString = CharProps.getStringProperty("reminder.random", "false");
        randomOrder = "true".equalsIgnoreCase(randomString);

        JPanel temp = new JPanel(new GridLayout(2, 1));
        temp.setBackground(this.getBackground());
        temp.setForeground(this.getForeground());

        pinyin = new JLabel(currentRecord.getPinyinAsUnicode(), JLabel.CENTER);
        theChar = new JLabel(currentRecord.getChars(), JLabel.CENTER);
        english = new JLabel(currentRecord.getEnglish(), JLabel.CENTER);

        theChar.setFont(chineseFont);
        pinyin.setFont(pinyinFont);
        english.setFont(englishFont);

        this.add(theChar);
        temp.add(pinyin);
        temp.add(english);
        this.add(temp);

        MouseListener adapter = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                startDrag(e);
//        if(e.getButton()==MouseEvent.BUTTON3) {
//          startMove(e);
//        }
            }

            public void mouseReleased(MouseEvent e) {
//        if(e.getButton()==MouseEvent.BUTTON3) {
//          stopMove(e);
//        }

                stopDrag(e);
                refresh();
            }

            public void mouseClicked(MouseEvent e) {
//            stopDrag();
                if (e.getButton() == MouseEvent.BUTTON1) {
                    next();
                    refresh();
                }
            }
        };

        this.addMouseListener(adapter);
        if ("true".equals(CharProps.getProperty("reminder.start"))) {
            running = true;
            start();
        }
        refresh();
    }

    public void next() {
        next(true);
    }

    private void startDrag(MouseEvent e) {
        xOff = e.getX();
        yOff = e.getY();
        moving = true;

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int x = (int) ReminderFrame.frame.getPosition().getX();
                int y = (int) ReminderFrame.frame.getPosition().getY();
                int xx = e.getX();
                int yy = e.getY();
                ReminderFrame.frame.setPosition(xx - xOff + x, yy - yOff + y);
            }
        });
    }

    private void stopDrag(MouseEvent e) {
        while (getMouseMotionListeners().length > 0) {
            this.removeMouseMotionListener(getMouseMotionListeners()[0]);
        }
        moving = false;
    }
    int xOff = 0;
    int yOff = 0;
    boolean moving = false;

    private void startMove(MouseEvent e) {
        xOff = e.getX();
        yOff = e.getY();
        moving = true;
    }

    private void stopMove(MouseEvent e) {
        int x = (int) ReminderFrame.frame.getPosition().getX();
        int y = (int) ReminderFrame.frame.getPosition().getY();
        int xx = e.getX();
        int yy = e.getY();
        if (moving) {
            ReminderFrame.frame.setPosition(xx - xOff + x, yy - yOff + y);
        }
        moving = false;
    }

    public void next(boolean respectRandom) {
        if (!randomOrder || !respectRandom) {
            index++;
            if (index >= record.getRecordCount()) {
                index = 0;
            }
        } else if (respectRandom) {
            index = rand.nextInt(record.getRecordCount());
        }
        currentRecord = record.getRecordNumber(index, false);
        if (currentRecord.getExtra1().indexOf("rem=s") >= 0) {
            next();
        }
    }

    public void prev() {
        prev(true);
    }

    public void prev(boolean respectRandom) {
        if (!randomOrder || !respectRandom) {
            index--;
            if (index < 0) {
                index = record.getRecordCount() - 1;
            }
        } else if (respectRandom) {
            index = rand.nextInt(record.getRecordCount());
        }
        currentRecord = record.getRecordNumber(index, false);
    }

    public void refresh() {
        if (running) {
            this.setBackground(onColor);
        } else {
            this.setBackground(offColor);
        }
        pinyin.setText(currentRecord.getPinyinAsUnicode());
        String chars = currentRecord.getChars();
        theChar.setText(chars);
        if (chars.length() == 1) {
            theChar.setFont(chineseFont);
        } else if (chars.length() == 2) {
            theChar.setFont(chineseFont2);
        } else if (chars.length() == 3 || chars.length() == 4) {
            theChar.setFont(chineseFont3);
            theChar.setText("<html>" + chars.substring(0, 2) + "<br>" + chars.substring(2) + "</html>");
            theChar.setFont(chineseFont3);
        } else {
            theChar.setText("<html>" + chars.substring(0, chars.length() / 2) + "<br>" + chars.substring(chars.length() / 2) + "</html>");
            theChar.setFont(new Font(CharProps.getProperty("font.ch.face"), Font.PLAIN, 90 / chars.length()));
        }
        english.setText(currentRecord.getEnglish());
        english.setBackground(this.getBackground());
        english.setOpaque(true);
        pinyin.setBackground(this.getBackground());
        pinyin.setOpaque(true);
        this.setToolTipText(currentRecord.getPinyinAsUnicode());
    }

    public void paint(Graphics g) {
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.paint(g);
    }

    public boolean isRandomOrder() {
        return randomOrder;
    }

    public void setRandomOrder(boolean randomOrder) {
        this.randomOrder = randomOrder;
    }

    public void keyPress(int keyCode) {
        if (keyCode == KeyEvent.VK_SPACE) {
            running = !running;
            if (running) {
                start();
            }
            CharProps.getProperties().setProperty("reminder.start", "" + running);
            CharProps.storeProps();
        } else if (keyCode == KeyEvent.VK_R) {
            randomOrder = !randomOrder;
            CharProps.getProperties().setProperty("reminder.random", "" + randomOrder);
            CharProps.storeProps();
        } else if (keyCode == KeyEvent.VK_LEFT) {
            prev(false);
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            next(false);
        } else if (keyCode == KeyEvent.VK_BACK_SPACE) {
            prev();
        } else if (keyCode == KeyEvent.VK_S) {
            currentRecord.setExtra1("rem=s" + currentRecord.getExtra1());
            next(true);
        } else if (keyCode == KeyEvent.VK_HOME) {
            currentRecord = record.getRecordNumber(0, false);
            index = 0;
        }
        refresh();
    }

    public void start() {
        new Thread(this).start();
    }

    public void run() {
        while (running) {
            next();
            refresh();
            try {
                Thread.sleep(delay * 1000);
            } catch (InterruptedException e) {
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }
}
