/*
 * Decompiled with CFR 0.152.
 */
package necesse.reports;

import java.awt.Dimension;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
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
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModListData;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.util.GameUtils;

public class ModCrashJFrame
extends JFrame {
    public static void main(String[] args) {
        new ModCrashJFrame(null, new RuntimeException());
    }

    public ModCrashJFrame(List<LoadedMod> mods, Throwable ... modExceptions) {
        super("");
        String modNames = mods == null || mods.isEmpty() ? "N/A" : GameUtils.join(mods.toArray(new LoadedMod[0]), LoadedMod::getModNameString, ", ", " & ");
        this.setTitle((mods == null || mods.size() <= 1 ? "Mod" : modNames) + " crash");
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
        String infoText = ModCrashJFrame.getLocale(() -> Localization.translate("ui", "modrunerror", "mod", modNames), "An error occurred while running mods: " + modNames + "\nYou can try to contact the author with the below log, or disable the mod.");
        infoLabel.setText("<html><div>" + infoText + "</div></html>");
        JPanel consolePanel = new JPanel();
        panel.add(consolePanel);
        consolePanel.setLayout(new BoxLayout(consolePanel, 3));
        JTextArea console = new JTextArea();
        console.setEditable(false);
        consolePanel.add(new JScrollPane(console));
        consolePanel.setPreferredSize(new Dimension(400, 200));
        try (StringWriter writer = new StringWriter();){
            writer.write("Mods: " + modNames + "\n\n");
            for (Throwable modException : modExceptions) {
                modException.printStackTrace(new PrintWriter(writer));
            }
            writer.flush();
            console.setText(writer.toString());
        }
        catch (IOException e2) {
            console.setText("Error writing error log");
        }
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, 2));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        inputPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 0));
        if (mods != null && !mods.isEmpty()) {
            JButton disableMod = new JButton(ModCrashJFrame.getLocale("ui", "moderrordisablethis", "Disable the mod"));
            inputPanel.add(disableMod);
            inputPanel.add(Box.createRigidArea(new Dimension(5, 0)));
            disableMod.addActionListener(e -> {
                ArrayList<ModListData> list = new ArrayList<ModListData>();
                for (LoadedMod m : ModLoader.getAllMods()) {
                    ModListData data = new ModListData(m);
                    if (mods.stream().anyMatch(mod -> m == mod)) {
                        data.enabled = false;
                    }
                    list.add(data);
                }
                ModLoader.saveModListSettings(list);
                this.dispose();
            });
        }
        JButton disableAll = new JButton(ModCrashJFrame.getLocale("ui", "moderrordisableall", "Disable all mods"));
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
        JButton quit = new JButton(ModCrashJFrame.getLocale("ui", "closebutton", "Close"));
        inputPanel.add(quit);
        consolePanel.add(inputPanel);
        quit.addActionListener(e -> this.dispose());
        this.setContentPane(panel);
        this.setPreferredSize(new Dimension(600, 450));
        this.setDefaultCloseOperation(2);
        ControllerInput.setActiveActionSet(ControllerInput.DESKTOP_CONTROLS);
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
        return ModCrashJFrame.getLocale(() -> Localization.translate(localeCategory, localeKey), errorDefault);
    }
}

