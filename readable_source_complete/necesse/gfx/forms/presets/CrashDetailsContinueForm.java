/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameMath;
import necesse.engine.window.WindowManager;
import necesse.gfx.GameBackground;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormTextBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.presets.ConfirmationContinueForm;
import necesse.gfx.gameFont.FontOptions;

public class CrashDetailsContinueForm
extends ConfirmationContinueForm {
    private FormTextBox textBox;

    public CrashDetailsContinueForm(String text, String infoKey, int maxCharacters, int maxTextBoxWidth, int maxTextBoxHeight, BiConsumer<CrashDetailsContinueForm, String> sendPressed, Consumer<CrashDetailsContinueForm> closePressed) {
        super("crash", GameMath.limit(WindowManager.getWindow().getHudWidth() - 100, Math.min(300, maxTextBoxWidth), maxTextBoxWidth), 10000);
        this.setupConfirmation((FormContentBox content) -> {
            FormLocalLabel label = content.addComponent(new FormLocalLabel("ui", infoKey, new FontOptions(16).color(this.getInterfaceStyle().activeTextColor), 0, this.getWidth() / 2, 10, this.getWidth() - 20));
            FormContentBox textContent = content.addComponent(new FormContentBox(4, label.getHeight() + 20, this.getWidth() - 8, GameMath.limit(WindowManager.getWindow().getHudHeight() - 150, Math.min(100, maxTextBoxHeight), maxTextBoxHeight), GameBackground.textBox));
            this.textBox = textContent.addComponent(new FormTextBox(new FontOptions(16), FairType.TextAlign.LEFT, this.getInterfaceStyle().textBoxTextColor, 0, 0, -1, -1, maxCharacters));
            this.textBox.setText(text);
            this.textBox.allowTyping = true;
            this.textBox.setTyping(true);
            this.textBox.setEmptyTextSpace(new Rectangle(textContent.getX(), textContent.getY(), textContent.getWidth(), textContent.getHeight()));
            this.textBox.onChange(e -> {
                Rectangle box = textContent.getContentBoxToFitComponents();
                textContent.setContentBox(box);
                textContent.scrollToFit(this.textBox.getCaretBoundingBox());
            });
            this.textBox.onCaretMove(e -> {
                if (!e.causedByMouse) {
                    textContent.scrollToFit(this.textBox.getCaretBoundingBox());
                }
            });
            Rectangle box = textContent.getContentBoxToFitComponents();
            textContent.setContentBox(box);
        }, (GameMessage)new LocalMessage("ui", "sendreport"), (GameMessage)new LocalMessage("ui", "dontsendreport"), () -> sendPressed.accept(this, this.textBox.getText()), () -> closePressed.accept(this));
    }
}

