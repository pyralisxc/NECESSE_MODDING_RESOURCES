/*
 * Decompiled with CFR 0.152.
 */
package necesse.reports;

import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import necesse.engine.input.controller.ControllerInput;

public class NoticeJFrame
extends JFrame {
    public static void main(String[] args) {
        new NoticeJFrame(300, "This is just a test\nSome next line text");
    }

    public NoticeJFrame(int preferredWidth, String noticeText) {
        super("Necesse notice");
        JPanel panel = new JPanel();
        this.add(panel);
        panel.setLayout(new BoxLayout(panel, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 8, 16, 8));
        JPanel infoPanel = new JPanel();
        panel.add(infoPanel);
        infoPanel.setLayout(new BoxLayout(infoPanel, 2));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        JLabel infoLabel = new JLabel();
        infoPanel.add(infoLabel);
        infoLabel.setHorizontalAlignment(0);
        String infoText = noticeText.replace("\n", "<br>");
        infoLabel.setText("<html><body style=\"width: " + (preferredWidth - 120) + "px\"><div style=\"text-align: center\">" + infoText + "</div></body></html>");
        JPanel inputPanel = new JPanel();
        inputPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        inputPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 50));
        JButton closeButton = new JButton("Close");
        inputPanel.add(closeButton);
        closeButton.addActionListener(e -> this.dispose());
        panel.add(inputPanel);
        this.setContentPane(panel);
        this.setDefaultCloseOperation(2);
        ControllerInput.setActiveActionSet(ControllerInput.DESKTOP_CONTROLS);
        this.pack();
        this.setPreferredSize(new Dimension(preferredWidth, infoLabel.getHeight() + 160));
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.requestFocus();
    }
}

