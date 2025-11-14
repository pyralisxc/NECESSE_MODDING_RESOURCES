/*
 * Decompiled with CFR 0.152.
 */
package necesse.reports;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.DocumentFilter;
import necesse.engine.ThreadFreezeMonitor;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModListData;
import necesse.engine.modLoader.ModLoader;
import necesse.reports.CrashReportData;
import necesse.reports.ReportUtils;

public class CrashJFrame
extends JFrame {
    private JTextArea console;
    private JScrollPane consoleScroll;
    private JButton dontSendReport;
    private JButton sendReport;

    public static void main(String[] args) {
        new CrashJFrame(new CrashReportData(Collections.singletonList(new Exception()), null, null, "TestCrash"));
    }

    public CrashJFrame(CrashReportData crashData) {
        super("Necesse crash report");
        ThreadFreezeMonitor.stopRunning();
        JPanel panel = new JPanel();
        this.add(panel);
        panel.setLayout(new BoxLayout(panel, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JPanel infoPanel = new JPanel();
        panel.add(infoPanel);
        infoPanel.setLayout(new BoxLayout(infoPanel, 2));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        JLabel infoLabel = new JLabel();
        infoPanel.add(infoLabel);
        String infoText = CrashJFrame.getLocale("ui", "crashsorry", "Sorry! Looks like the game crashed :(\nPlease help the development by sending the crash report!");
        infoLabel.setText("<html><div>" + infoText + "</div></html>");
        JPanel consolePanel = new JPanel();
        panel.add(consolePanel);
        consolePanel.setLayout(new BoxLayout(consolePanel, 3));
        this.console = new JTextArea();
        this.console.setEditable(false);
        this.consoleScroll = new JScrollPane(this.console);
        consolePanel.add(this.consoleScroll);
        consolePanel.setPreferredSize(new Dimension(400, 200));
        this.console.setText(crashData.getFullReport(null));
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, 2));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        inputPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 0));
        if (!ModLoader.getEnabledMods().isEmpty()) {
            JButton disableAll = new JButton(CrashJFrame.getLocale("ui", "moderrordisableall", "Disable all mods"));
            inputPanel.add(disableAll);
            inputPanel.add(Box.createRigidArea(new Dimension(5, 0)));
            disableAll.addActionListener(e -> {
                ArrayList<ModListData> list = new ArrayList<ModListData>();
                for (LoadedMod m : ModLoader.getAllMods()) {
                    ModListData data = new ModListData(m);
                    data.enabled = false;
                    list.add(data);
                }
                ModLoader.saveModListSettings(list);
                this.dispose();
            });
        }
        this.sendReport = new JButton(CrashJFrame.getLocale("ui", "sendreport", "Send report"));
        inputPanel.add(this.sendReport);
        inputPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        this.dontSendReport = new JButton(CrashJFrame.getLocale("ui", "dontsendreport", "Don't send report"));
        inputPanel.add(this.dontSendReport);
        consolePanel.add(inputPanel);
        this.sendReport.addActionListener(e -> {
            DetailsFrame sendDialog = new DetailsFrame(this, crashData, CrashJFrame.getLocale("ui", "crashdetailshere", "Type details here..."));
            sendDialog.setVisible(true);
            sendDialog.requestFocus();
            this.dispose();
        });
        this.dontSendReport.addActionListener(e -> {
            this.dispose();
            System.exit(1);
        });
        this.setContentPane(panel);
        this.setPreferredSize(new Dimension(600, 450));
        this.setDefaultCloseOperation(3);
        ControllerInput.setActiveActionSet(ControllerInput.DESKTOP_CONTROLS);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.requestFocus();
    }

    private static String getLocale(String category, String key, String errorDefault) {
        try {
            return Localization.translate(category, key).replace("\n", "<br>");
        }
        catch (Exception e) {
            return errorDefault.replace("\n", "<br>");
        }
    }

    private static class DetailsFrame
    extends JFrame {
        private JTextArea console;
        private JScrollPane consoleScroll;
        private JButton sendReport;

        public DetailsFrame(Frame previous, CrashReportData data, String userDetails) {
            super("Necesse crash report");
            JPanel panel = new JPanel();
            this.add(panel);
            panel.setLayout(new BoxLayout(panel, 1));
            panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            JPanel infoPanel = new JPanel();
            panel.add(infoPanel);
            infoPanel.setLayout(new BoxLayout(infoPanel, 2));
            infoPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
            JLabel infoLabel = new JLabel();
            infoPanel.add(infoLabel);
            String infoText = CrashJFrame.getLocale("ui", "crashgivedetails", "Please give details about what you were doing when the crash happened or any other details you think might be helpful!");
            infoLabel.setText("<html><span>" + infoText + "</span>");
            JPanel consolePanel = new JPanel();
            panel.add(consolePanel);
            consolePanel.setLayout(new BoxLayout(consolePanel, 3));
            this.console = new JTextArea();
            this.console.setEditable(true);
            this.consoleScroll = new JScrollPane(this.console);
            consolePanel.add(this.consoleScroll);
            consolePanel.setPreferredSize(new Dimension(400, 200));
            DefaultStyledDocument doc = new DefaultStyledDocument();
            doc.setDocumentFilter(new DocumentSizeFilter(500));
            this.console.setDocument(doc);
            this.console.setText(userDetails);
            JPanel inputPanel = new JPanel();
            inputPanel.setLayout(new BoxLayout(inputPanel, 2));
            inputPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
            inputPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 0));
            this.sendReport = new JButton(CrashJFrame.getLocale("ui", "sendreport", "Send report"));
            inputPanel.add(this.sendReport);
            consolePanel.add(inputPanel);
            this.sendReport.addActionListener(e -> {
                SendFrame sendFrame = new SendFrame(this, data, this.console.getText());
                sendFrame.setVisible(true);
                sendFrame.requestFocus();
                this.dispose();
            });
            this.setContentPane(panel);
            this.setPreferredSize(new Dimension(400, 300));
            this.setDefaultCloseOperation(3);
            this.pack();
            this.setLocationRelativeTo(previous);
        }
    }

    private static class DocumentSizeFilter
    extends DocumentFilter {
        public int maxChars;

        public DocumentSizeFilter(int maxChars) {
            this.maxChars = maxChars;
        }

        @Override
        public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            int docLength = fb.getDocument().getLength();
            if (docLength + string.length() <= this.maxChars) {
                super.insertString(fb, offset, string, attr);
            } else {
                int remainingChars = this.maxChars - docLength;
                super.insertString(fb, offset, string.substring(0, remainingChars), attr);
                Toolkit.getDefaultToolkit().beep();
            }
        }

        @Override
        public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            int docLength = fb.getDocument().getLength();
            if (docLength + text.length() - length <= this.maxChars) {
                super.replace(fb, offset, length, text, attrs);
            } else {
                int remainingChars = this.maxChars - docLength;
                String substring = text.substring(0, remainingChars);
                super.replace(fb, offset, length, substring, attrs);
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }

    private static class ThankYouFrame
    extends JFrame {
        public ThankYouFrame(Frame previous) {
            super("Necesse crash report");
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, 2));
            infoPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            JLabel infoLabel = new JLabel();
            infoPanel.add(infoLabel);
            infoLabel.setHorizontalAlignment(0);
            String infoText = CrashJFrame.getLocale("ui", "sendreportthanks", "Thank you!");
            infoLabel.setText("<html><span>" + infoText + "</span>");
            JPanel inputPanel = new JPanel();
            inputPanel.setLayout(new BoxLayout(inputPanel, 2));
            inputPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
            inputPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 0));
            JButton closeButton = new JButton(CrashJFrame.getLocale("ui", "closebutton", "Close"));
            inputPanel.add(closeButton);
            closeButton.addActionListener(e -> {
                this.dispose();
                System.exit(1);
            });
            closeButton.setHorizontalAlignment(0);
            this.getContentPane().add((Component)infoPanel, "Center");
            this.getContentPane().add((Component)inputPanel, "Last");
            this.setPreferredSize(new Dimension(300, 150));
            this.setDefaultCloseOperation(3);
            this.pack();
            this.setLocationRelativeTo(previous);
        }
    }

    private static class RetryFrame
    extends JFrame {
        public RetryFrame(Frame previous, String error, CrashReportData data, String userDetails) {
            super("Necesse crash report");
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, 2));
            infoPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            JLabel infoLabel = new JLabel();
            infoPanel.add(infoLabel);
            infoLabel.setHorizontalAlignment(0);
            infoLabel.setText("<html><span>" + error + "</span>");
            JPanel inputPanel = new JPanel();
            inputPanel.setLayout(new BoxLayout(inputPanel, 2));
            inputPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
            inputPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 0));
            JButton retryButton = new JButton(CrashJFrame.getLocale("ui", "sendreportretry", "Retry"));
            inputPanel.add(retryButton);
            retryButton.addActionListener(e -> {
                SendFrame sendFrame = new SendFrame(this, data, userDetails);
                sendFrame.setVisible(true);
                sendFrame.requestFocus();
                this.dispose();
            });
            inputPanel.add(Box.createRigidArea(new Dimension(10, 0)));
            JButton closeButton = new JButton(CrashJFrame.getLocale("ui", "closebutton", "Close"));
            inputPanel.add(closeButton);
            closeButton.addActionListener(e -> {
                this.dispose();
                System.exit(1);
            });
            this.getContentPane().add((Component)infoPanel, "Center");
            this.getContentPane().add((Component)inputPanel, "Last");
            this.setPreferredSize(new Dimension(300, 200));
            this.setDefaultCloseOperation(3);
            this.pack();
            this.setLocationRelativeTo(previous);
        }
    }

    private static class SendFrame
    extends JFrame {
        public SendFrame(Frame previous, CrashReportData data, String userDetails) {
            super("Necesse crash report");
            JLabel infoLabel = new JLabel();
            infoLabel.setHorizontalAlignment(0);
            infoLabel.setVerticalAlignment(0);
            infoLabel.setText("<html><span>Sending report...</span>");
            this.getContentPane().add((Component)infoLabel, "Center");
            this.setPreferredSize(new Dimension(300, 100));
            final AtomicBoolean interrupted = new AtomicBoolean(false);
            new Thread(() -> {
                String error = ReportUtils.sendCrashReport(data, userDetails);
                if (!interrupted.get()) {
                    if (error != null) {
                        error = error.replace("\n", "</br>");
                        RetryFrame retryFrame = new RetryFrame(this, error, data, userDetails);
                        retryFrame.setVisible(true);
                        retryFrame.requestFocus();
                    } else {
                        ThankYouFrame thankYouFrame = new ThankYouFrame(this);
                        thankYouFrame.setVisible(true);
                        thankYouFrame.requestFocus();
                    }
                }
                this.dispose();
            }).start();
            this.addWindowListener(new WindowAdapter(){

                @Override
                public void windowClosing(WindowEvent e) {
                    interrupted.set(true);
                }
            });
            this.setDefaultCloseOperation(3);
            this.pack();
            this.setLocationRelativeTo(previous);
        }
    }
}

