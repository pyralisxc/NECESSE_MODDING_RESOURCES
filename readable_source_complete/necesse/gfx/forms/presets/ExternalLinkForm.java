/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.gfx.GameResources;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormIcon;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.ui.ButtonColor;

public class ExternalLinkForm
extends Form {
    public ExternalLinkForm(GameTexture qrCodeTexture, String link, GameMessage title, Runnable closePressed) {
        super("externalLink", 800, 500);
        FormIcon qrIcon = this.addComponent(new FormIcon(10, 10, 90, 90, qrCodeTexture != null ? qrCodeTexture : GameResources.error, this.getInterfaceStyle().activeTextColor));
        int xAfterQr = qrIcon.getX() + qrIcon.getWidth() + 15;
        FormLocalLabel titleLabel = this.addComponent(new FormLocalLabel(title, new FontOptions(20), -1, xAfterQr, 13));
        FormLabel linkLabel = this.addComponent(new FormLabel(link, new FontOptions(12), -1, xAfterQr, 44));
        FormLocalTextButton copyLinkButton = this.addComponent(new FormLocalTextButton(new LocalMessage("ui", "copylink"), xAfterQr - 1, 70, 175, FormInputSize.SIZE_24, ButtonColor.BASE));
        copyLinkButton.onClicked(e -> WindowManager.getWindow().putClipboard(link));
        this.setWidth((int)(Math.max(titleLabel.getBoundingBox().getMaxX(), linkLabel.getBoundingBox().getMaxX()) + 10.0));
        this.setHeight((int)(qrIcon.getBoundingBox().getMaxY() + 50.0));
        this.addComponent(new FormLocalTextButton("ui", "closebutton", 4, this.getHeight() - 40, this.getWidth() - 8)).onClicked(e -> closePressed.run());
        this.onWindowResized(WindowManager.getWindow());
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }
}

