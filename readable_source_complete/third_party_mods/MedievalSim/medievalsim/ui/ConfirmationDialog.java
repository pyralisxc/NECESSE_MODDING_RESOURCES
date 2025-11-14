/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.GlobalData
 *  necesse.engine.state.MainGame
 *  necesse.gfx.forms.Form
 *  necesse.gfx.forms.components.FormComponent
 *  necesse.gfx.forms.components.FormInputSize
 *  necesse.gfx.forms.components.FormLabel
 *  necesse.gfx.forms.components.FormTextButton
 *  necesse.gfx.gameFont.FontOptions
 *  necesse.gfx.ui.ButtonColor
 */
package medievalsim.ui;

import java.awt.Color;
import java.util.function.Consumer;
import necesse.engine.GlobalData;
import necesse.engine.state.MainGame;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;

public class ConfirmationDialog
extends Form {
    private static final int DIALOG_WIDTH = 400;
    private static final int DIALOG_HEIGHT = 200;
    private static final int MARGIN = 20;
    private static final int BUTTON_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 32;
    private static final FontOptions WHITE_TEXT_20 = new FontOptions(20).color(Color.WHITE);
    private static final FontOptions WHITE_TEXT_14 = new FontOptions(14).color(Color.WHITE);
    private final Consumer<Boolean> callback;

    public ConfirmationDialog(String title, String message, Consumer<Boolean> callback) {
        super("confirmation", 400, 200);
        this.callback = callback;
        this.buildUI(title, message);
    }

    private void buildUI(String title, String message) {
        String[] lines;
        int currentY = 20;
        FormLabel titleLabel = new FormLabel(title, WHITE_TEXT_20, -1, 20, currentY, 360);
        this.addComponent((FormComponent)titleLabel);
        currentY += 35;
        for (String line : lines = message.split("\\n")) {
            FormLabel messageLabel = new FormLabel(line, WHITE_TEXT_14, -1, 20, currentY, 360);
            this.addComponent((FormComponent)messageLabel);
            currentY += 22;
        }
        int buttonY = 148;
        int spacing = 20;
        int buttonStartX = (400 - (240 + spacing)) / 2;
        FormTextButton cancelButton = new FormTextButton("Cancel", buttonStartX, buttonY, 120, FormInputSize.SIZE_32, ButtonColor.BASE);
        cancelButton.onClicked(e -> {
            this.callback.accept(false);
            this.close();
        });
        this.addComponent((FormComponent)cancelButton);
        FormTextButton confirmButton = new FormTextButton("Confirm", buttonStartX + 120 + spacing, buttonY, 120, FormInputSize.SIZE_32, ButtonColor.BASE);
        confirmButton.onClicked(e -> {
            this.callback.accept(true);
            this.close();
        });
        this.addComponent((FormComponent)confirmButton);
    }

    private void close() {
        if (GlobalData.getCurrentState() instanceof MainGame) {
            MainGame mainGame = (MainGame)GlobalData.getCurrentState();
            mainGame.formManager.removeComponent((FormComponent)this);
        }
    }

    public static ConfirmationDialog forClearInventory(String playerName, Consumer<Boolean> callback) {
        return new ConfirmationDialog("Clear All Items", "WARNING: This will permanently delete ALL items in " + playerName + "'s inventory.\nThis cannot be undone!", callback);
    }

    public static ConfirmationDialog forKick(String playerName, Consumer<Boolean> callback) {
        return new ConfirmationDialog("Kick Player", "Kick " + playerName + " from the server?\nThey can reconnect unless banned.", callback);
    }

    public static ConfirmationDialog forBan(String playerName, Consumer<Boolean> callback) {
        return new ConfirmationDialog("Ban Player", "PERMANENTLY BAN " + playerName + " from this server?\nThey will not be able to reconnect.", callback);
    }

    public static ConfirmationDialog forCreativeMode(Consumer<Boolean> callback) {
        return new ConfirmationDialog("Enable Creative Mode", "WARNING: Enabling Creative Mode will PERMANENTLY disable achievements.\nThis cannot be reversed!", callback);
    }

    public static ConfirmationDialog forAllowCheats(Consumer<Boolean> callback) {
        return new ConfirmationDialog("Allow Cheats", "WARNING: Allowing cheats will PERMANENTLY disable achievements.\nThis cannot be reversed!", callback);
    }

    public static ConfirmationDialog forClearArea(int width, int height, Consumer<Boolean> callback) {
        return new ConfirmationDialog("Clear Area", String.format("Clear a %dx%d tile area?\nThis will remove all tiles, objects, and walls.", width, height), callback);
    }

    public static ConfirmationDialog forClearAll(Consumer<Boolean> callback) {
        return new ConfirmationDialog("Clear All Entities", "WARNING: This will remove ALL mobs, dropped items, and projectiles from the current level.\nThis cannot be undone!", callback);
    }
}

