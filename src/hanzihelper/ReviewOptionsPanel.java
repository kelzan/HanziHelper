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

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

/**
 *
 * @author Daddy
 */
public class ReviewOptionsPanel extends javax.swing.JPanel {

    ReviewOptions reviewOptions;

    /**
     * Creates new form ReviewOptionsPanel
     */
    public ReviewOptionsPanel(ReviewOptions reviewOptions) {
        this.reviewOptions = reviewOptions;
        initComponents();
        soundCheckBox.setSelected(reviewOptions.useSound);
        if (reviewOptions.type == ReviewOptions.ReviewType.SIMPLIFIED) {
            simpButton.setSelected(true);
        } else if (reviewOptions.type == ReviewOptions.ReviewType.TRADITIONAL) {
            tradButton.setSelected(true);
        } else {
            bothButton.setSelected(true);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        repeatText = new javax.swing.JTextField();
        repeatLabel = new javax.swing.JLabel();
        penaltyLabel = new javax.swing.JLabel();
        penaltyText = new javax.swing.JTextField();
        soundLabel = new javax.swing.JLabel();
        soundCheckBox = new javax.swing.JCheckBox();
        okButton = new javax.swing.JButton();
        simpButton = new javax.swing.JRadioButton();
        tradButton = new javax.swing.JRadioButton();
        bothButton = new javax.swing.JRadioButton();

        repeatText.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        repeatText.setText(String.valueOf(reviewOptions.repeatPerChar));

        repeatLabel.setText("#Times Per Character:");

        penaltyLabel.setText("Miss Penalty:");

        penaltyText.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        penaltyText.setText(String.valueOf(reviewOptions.missPenalty));

        soundLabel.setText("Sound?");
        soundLabel.setAlignmentX(0.5F);
        soundLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        soundCheckBox.setAlignmentX(0.5F);
        soundCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        soundCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(simpButton);
        simpButton.setText("Simplified");

        buttonGroup1.add(tradButton);
        tradButton.setText("Traditional");

        buttonGroup1.add(bothButton);
        bothButton.setSelected(true);
        bothButton.setText("Both");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(penaltyLabel)
                            .addComponent(repeatLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(repeatText, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                            .addComponent(penaltyText, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(simpButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(soundLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(soundCheckBox))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(tradButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(bothButton)))))
                .addContainerGap(43, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(okButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(repeatLabel)
                    .addComponent(repeatText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(penaltyLabel)
                    .addComponent(penaltyText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(simpButton)
                    .addComponent(tradButton)
                    .addComponent(bothButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(soundLabel)
                    .addComponent(soundCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addComponent(okButton)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        reviewOptions.repeatPerChar = Integer.parseInt(repeatText.getText());
        reviewOptions.missPenalty = Integer.parseInt(penaltyText.getText());
        reviewOptions.useSound = soundCheckBox.isSelected();
        if (simpButton.isSelected()) {
            reviewOptions.type = ReviewOptions.ReviewType.SIMPLIFIED;
        } else if (tradButton.isSelected()) {
            reviewOptions.type = ReviewOptions.ReviewType.TRADITIONAL;
        } else {
            reviewOptions.type = ReviewOptions.ReviewType.BOTH;
        }
        reviewOptions.setDefaults();
        JDialog frame = (JDialog) SwingUtilities.getWindowAncestor(this);
        frame.dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton bothButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel penaltyLabel;
    private javax.swing.JTextField penaltyText;
    private javax.swing.JLabel repeatLabel;
    private javax.swing.JTextField repeatText;
    private javax.swing.JRadioButton simpButton;
    private javax.swing.JCheckBox soundCheckBox;
    private javax.swing.JLabel soundLabel;
    private javax.swing.JRadioButton tradButton;
    // End of variables declaration//GEN-END:variables
}
