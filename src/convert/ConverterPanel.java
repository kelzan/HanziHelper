package convert;

import hanzihelper.CharApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.File;

/**
 * By Colin Jacobs, colin@q9software.com Date: 21/04/2006
 */
public class ConverterPanel extends JPanel implements ActionListener {

    private JTextField inFileName, outFileName;
    private JButton chooseIn, chooseOut, convert;
    private JLabel inputLabel, outputLabel, encodingLabel1, encodingLabel2, convertLabel;
    private JCheckBox bom;
    private JComboBox encodingIn, encodingOut;
    private JComboBox convertChars;
    private String[] encodings = new String[]{"UTF-8", "GB2312", "UTF-16", "Big5"};
    private JLabel status = new JLabel(" ");

    public ConverterPanel() {
        inFileName = new JTextField(20);
        outFileName = new JTextField(20);
        chooseIn = new JButton("Choose");
        chooseOut = new JButton("Choose");
        convert = new JButton("Convert");
        encodingLabel1 = new JLabel("Encoding:");
        encodingLabel2 = new JLabel("Encoding:");
        convertLabel = new JLabel("Convert characters:");
        encodingIn = new JComboBox(encodings);
        encodingOut = new JComboBox(encodings);
        convertChars = new JComboBox(new String[]{"No", "Simplified to Traditional", "Traditional to Simplified"});

        bom = new JCheckBox("Write BOM");

        inputLabel = new JLabel("Input File");
        outputLabel = new JLabel("Output File");
        Font font = inputLabel.getFont();
        inputLabel.setFont(new Font(font.getName(), Font.BOLD, font.getSize() + 2));
        outputLabel.setFont(new Font(font.getName(), Font.BOLD, font.getSize() + 2));
        inputLabel.setHorizontalAlignment(JLabel.CENTER);
        outputLabel.setHorizontalAlignment(JLabel.CENTER);


        this.setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 2));

        JPanel temp, temp2;

        topPanel.add(inputLabel);
        topPanel.add(outputLabel);
        this.add(topPanel, BorderLayout.NORTH);

        topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 2));

        temp = new JPanel(new BorderLayout());
        temp2 = new JPanel(new FlowLayout());

        temp2.add(inFileName);
        temp2.add(chooseIn);
//    this.add(temp);
//    temp = new JPanel();
        temp.add(temp2, BorderLayout.NORTH);
        temp2 = new JPanel(new FlowLayout());
        temp2.add(encodingLabel1);
        temp2.add(encodingIn);
        temp.add(temp2, BorderLayout.SOUTH);
        temp.setBorder(BorderFactory.createLineBorder(Color.black));
        topPanel.add(temp);

        temp = new JPanel(new BorderLayout());
        temp2 = new JPanel(new FlowLayout());

        temp2.add(outFileName);
        temp2.add(chooseOut);
        temp.add(temp2, BorderLayout.NORTH);
        temp2 = new JPanel(new FlowLayout());
        temp2.add(encodingLabel2);
        temp2.add(encodingOut);
        temp.add(temp2, BorderLayout.SOUTH);
        temp.setBorder(BorderFactory.createLineBorder(Color.black));
        topPanel.add(temp);

        this.add(topPanel, BorderLayout.CENTER);

        topPanel = new JPanel(new GridLayout(3, 1));

        temp = new JPanel();
        temp.add(bom);
        topPanel.add(temp);

        temp = new JPanel();
        temp.add(convertLabel);
        temp.add(convertChars);
        topPanel.add(temp);

        temp = new JPanel(new BorderLayout());
        temp2 = new JPanel();
        temp2.add(convert);
        temp.add(temp2, BorderLayout.NORTH);
        temp.add(status, BorderLayout.SOUTH);
        topPanel.add(temp);

        this.add(topPanel, BorderLayout.SOUTH);

        convert.addActionListener(this);
        chooseIn.addActionListener(this);
        chooseOut.addActionListener(this);
        encodingOut.addActionListener(this);


    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == convert) {
            try {
                Converter.convertFile(inFileName.getText(), outFileName.getText(),
                        encodingIn.getSelectedItem().toString(), encodingOut.getSelectedItem().toString(),
                        bom.isSelected(), convertChars.getSelectedItem().equals("Simplified to Traditional"),
                        convertChars.getSelectedItem().equals("Traditional to Simplified"));
                status.setText("Success: " + inFileName.getText() + " -> "
                        + outFileName.getText());
            } catch (IOException ex) {
                showError(ex.getMessage());
            }
        } else if (e.getSource() == chooseIn) {
            File f = CharApp.getInstance().selectFile("txt", "Text files", false, false);
            if (f != null) {
                inFileName.setText(f.getAbsolutePath());
            }
        } else if (e.getSource() == chooseOut) {
            File f = CharApp.getInstance().selectFile("txt", "Text files", false, true);
            if (f != null) {
                outFileName.setText(f.getAbsolutePath());
            }
        } else if (e.getSource() == encodingOut) {
            bom.setEnabled(encodingOut.getSelectedItem() == "UTF-8");
        }
    }

    private void showError(String msg) {
        CharApp.getInstance().showErrorMessage(msg);
    }
}
