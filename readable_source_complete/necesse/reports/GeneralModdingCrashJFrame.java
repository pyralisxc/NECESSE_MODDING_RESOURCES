/*
 * Decompiled with CFR 0.152.
 */
package necesse.reports;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModListData;
import necesse.engine.modLoader.ModLoader;
import necesse.reports.CrashReportData;

public class GeneralModdingCrashJFrame
extends JFrame {
    public static void main(String[] args) {
        new GeneralModdingCrashJFrame(new CrashReportData(Collections.singletonList(new RuntimeException()), null, null, "Test"));
    }

    public GeneralModdingCrashJFrame(CrashReportData crashData) {
        super("Necesse modded crash report");
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
        String infoText = GeneralModdingCrashJFrame.getLocale("ui", "moddederror", "An error occurred while running mods.");
        infoLabel.setText("<html><div>" + infoText + "</div></html>");
        JPanel consolePanel = new JPanel();
        panel.add(consolePanel);
        consolePanel.setLayout(new BoxLayout(consolePanel, 3));
        JTextArea console = new JTextArea();
        console.setEditable(false);
        consolePanel.add(new JScrollPane(console));
        consolePanel.setPreferredSize(new Dimension(400, 200));
        console.setText(crashData.getFullReport(null));
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, 2));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        inputPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 0));
        JButton disableAll = new JButton(GeneralModdingCrashJFrame.getLocale("ui", "moderrordisableall", "Disable all mods"));
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
        JButton quit = new JButton(GeneralModdingCrashJFrame.getLocale("ui", "closebutton", "Close"));
        inputPanel.add(quit);
        consolePanel.add(inputPanel);
        quit.addActionListener(e -> this.dispose());
        this.setContentPane(panel);
        this.setPreferredSize(new Dimension(600, 450));
        this.setDefaultCloseOperation(2);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.requestFocus();
    }

    private static String getLocale(Supplier<String> stringSupplier, String errorDefault) {
        try {
            return stringSupplier.get().replace("\n", "<br>");
        }
        catch (Exception e) {
            return errorDefault.replace("\n", "<br>");
        }
    }

    private static String getLocale(String localeCategory, String localeKey, String errorDefault) {
        return GeneralModdingCrashJFrame.getLocale(() -> Localization.translate(localeCategory, localeKey), errorDefault);
    }
}

