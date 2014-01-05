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

import hanzihelper.CharRecord;
import hanzihelper.CharApp;
import hanzihelper.CharProps;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

/**
 * This is the main entry point for the "reminder" application. It puts a undecorated ittle window
 * in the corner of the screen and cycles through the records in the database.
 * <p/>
 * JDK 1.5 only.
 */
public class ReminderFrame extends JFrame {

    ReminderPanel panel;
    public static ReminderFrame frame = null;
    JDialog mainWindow;

    public ReminderFrame(String filename) {

        setupSystemTray();
        frame = this;

        CharRecord record = null;
        try {
            String file = CharProps.getStringProperty("record.file", CharApp.RECORD_FILE);
            if (filename != null) {
                file = filename;
            }
            record = new CharRecord(file);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        panel = new ReminderPanel(record);
//    add(panel);

        mainWindow = new JDialog(this, false);
        final JDialog window = mainWindow;
        setUndecorated(true);
//    setAlwaysOnTop(true);

        KeyAdapter keyAdapt = new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                } else if (e.getKeyCode() == KeyEvent.VK_F1) {
                    showSettings();
                } else if (e.getKeyCode() == KeyEvent.VK_F2) {
                    window.setAlwaysOnTop(!window.isAlwaysOnTop());
                } else {
                    panel.keyPress(e.getKeyCode());
                }
            }
        };

        this.addKeyListener(keyAdapt);
        this.pack();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((int) (screen.getWidth() - getWidth()),
                (int) (screen.getHeight() - getHeight() - 30));

//    setVisible(true);

        window.setUndecorated(true);
        window.add(panel);
        window.setAlwaysOnTop(true);
        window.addKeyListener(keyAdapt);
        panel.addKeyListener(keyAdapt);
        window.setSize(50, 100);
        window.setLocation((int) (screen.getWidth() - window.getWidth()),
                (int) (screen.getHeight() - window.getHeight() - 30));
        window.setVisible(true);
    }

    public void setPosition(int x, int y) {
        mainWindow.setLocation(x, y);
    }

    public Point getPosition() {
        return mainWindow.getLocation();
    }

    public void showSettings() {
        String inputValue = JOptionPane.
                showInputDialog("Time for cards, in sec (is " + panel.getDelay() + "):");
        if (inputValue == null) {
            return;
        }
        try {
            int secs = Integer.parseInt(inputValue);
            CharProps.getProperties().setProperty("reminder.delay", secs + "");
            CharProps.storeProps();
            panel.setDelay(secs);
        } catch (NumberFormatException n) {
            displayMessage("Please enter a valid number.");
            showSettings();
        }
    }

    public void displayMessage(String message) {
        JFrame frame = new JFrame();
        JOptionPane.showMessageDialog(frame, message);
        frame.dispose();
    }

    private void setupSystemTray() {
        /*
         final SysTrayMenuIcon icon = new SysTrayMenuIcon(ReminderFrame.class.getClassLoader()
         .getResource("icon.ico"));
         menu = new SysTrayMenu(icon);
         menu.setToolTip("DGS: None");
         SysTrayMenuItem quitItem = new SysTrayMenuItem("Quit");
         SysTrayMenuItem nextItem = new SysTrayMenuItem("Next");
         SysTrayMenuItem prevItem = new SysTrayMenuItem("Prev");
         SysTrayMenuItem timeItem = new SysTrayMenuItem("Timing");

         SysTrayMenuListener listener = new SysTrayMenuAdapter() {
         public void iconLeftDoubleClicked(SysTrayMenuEvent event) {
         menu.setIcon(icon);
         System.out.println("ReminderFrame.iconLeftDoubleClicked");
         }
         };

         icon.addSysTrayMenuListener(listener);
         menu.addItem(quitItem);
         menu.addSeparator();
         menu.addItem(prevItem);
         menu.addItem(nextItem);
         menu.addItem(timeItem);

         quitItem.addSysTrayMenuListener(new SysTrayMenuAdapter() {
         public void menuItemSelected(SysTrayMenuEvent event) {
         System.exit(0);
         }
         });

         nextItem.addSysTrayMenuListener(new SysTrayMenuAdapter() {
         public void menuItemSelected(SysTrayMenuEvent event) {
         panel.keyPress(KeyEvent.VK_RIGHT);
         }
         });

         prevItem.addSysTrayMenuListener(new SysTrayMenuAdapter() {
         public void menuItemSelected(SysTrayMenuEvent event) {
         panel.keyPress(KeyEvent.VK_LEFT);

         }
         });

         timeItem.addSysTrayMenuListener(new SysTrayMenuAdapter() {
         public void menuItemSelected(SysTrayMenuEvent event) {
         showSettings();
         }
         });
         */
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            new ReminderFrame(args[0]);
        } else {
            new ReminderFrame(null);
        }
    }
}
