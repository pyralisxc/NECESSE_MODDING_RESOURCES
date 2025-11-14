/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import java.util.function.Consumer;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameMath;
import necesse.engine.window.WindowManager;
import necesse.gfx.GameBackground;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.ConfirmationContinueForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.reports.CrashReportData;

public class CrashReportContinueForm
extends ConfirmationContinueForm {
    public CrashReportContinueForm(CrashReportData data, String infoKey, int maxTextBoxWidth, int maxTextBoxHeight, Consumer<CrashReportContinueForm> sendPressed, Consumer<CrashReportContinueForm> closePressed) {
        super("crash", 450, 10000);
        this.setupConfirmation((FormContentBox content) -> {
            FormLocalLabel label = content.addComponent(new FormLocalLabel("ui", infoKey, new FontOptions(16).color(this.getInterfaceStyle().activeTextColor), 0, this.getWidth() / 2, 10, this.getWidth() - 20));
            int buttonWidth = Math.min(250, this.getWidth() - 8);
            content.addComponent(new FormLocalTextButton("ui", "crashshowlog", this.getWidth() / 2 - buttonWidth / 2, label.getHeight() + 20, buttonWidth, FormInputSize.SIZE_20, ButtonColor.BASE)).onClicked(e -> this.setupLog(data, infoKey, maxTextBoxWidth, maxTextBoxHeight, sendPressed, closePressed));
        }, (GameMessage)new LocalMessage("ui", "sendreport"), (GameMessage)new LocalMessage("ui", "dontsendreport"), () -> sendPressed.accept(this), () -> closePressed.accept(this));
    }

    private void setupLog(CrashReportData data, String infoKey, int maxTextBoxWidth, int maxTextBoxHeight, Consumer<CrashReportContinueForm> sendPressed, Consumer<CrashReportContinueForm> closePressed) {
        this.setWidth(GameMath.limit(WindowManager.getWindow().getHudWidth() - 100, Math.min(450, maxTextBoxWidth), maxTextBoxWidth));
        this.setupConfirmation((FormContentBox content) -> {
            FormLocalLabel label = content.addComponent(new FormLocalLabel("ui", infoKey, new FontOptions(16).color(this.getInterfaceStyle().activeTextColor), 0, this.getWidth() / 2, 10, this.getWidth() - 20));
            FormContentBox textContent = content.addComponent(new FormContentBox(4, label.getHeight() + 20, this.getWidth() - 8, GameMath.limit(WindowManager.getWindow().getHudHeight() - 150, Math.min(100, maxTextBoxHeight), maxTextBoxHeight), GameBackground.textBox));
            FormTextBox textBox = textContent.addComponent(new FormTextBox(new FontOptions(16), FairType.TextAlign.LEFT, this.getInterfaceStyle().textBoxTextColor, 0, 0, -1, -1, -1));
            textBox.setText(data.getFullReport(null));
            textBox.allowTyping = false;
            textBox.setEmptyTextSpace(new Rectangle(textContent.getX(), textContent.getY(), textContent.getWidth(), textContent.getHeight()));
            textBox.onChange(e -> {
                Rectangle box = textContent.getContentBoxToFitComponents();
                textContent.setContentBox(box);
                textContent.scrollToFit(textBox.getCaretBoundingBox());
            });
            textBox.onCaretMove(e -> {
                if (!e.causedByMouse) {
                    textContent.scrollToFit(textBox.getCaretBoundingBox());
                }
            });
            Rectangle box = textContent.getContentBoxToFitComponents();
            textContent.setContentBox(box);
        }, (GameMessage)new LocalMessage("ui", "sendreport"), (GameMessage)new LocalMessage("ui", "dontsendreport"), () -> sendPressed.accept(this), () -> closePressed.accept(this));
    }
}

