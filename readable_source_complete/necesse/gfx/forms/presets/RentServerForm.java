/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import necesse.engine.input.InputEvent;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.platforms.PlatformManager;
import necesse.engine.util.GameUtils;
import necesse.engine.window.GameWindow;
import necesse.gfx.GameResources;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormTextureButton;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.ExternalLinkForm;
import necesse.gfx.gameFont.FontOptions;

public abstract class RentServerForm
extends FormSwitcher {
    public Form infoForm = this.addComponent(new Form("rentServer", 400, 100));

    public RentServerForm() {
        FormFlow flow = new FormFlow(10);
        this.infoForm.addComponent(new FormLocalLabel("ui", "rentserver", new FontOptions(20), 0, this.infoForm.getWidth() / 2, flow.next(30), this.infoForm.getWidth() - 20));
        this.infoForm.addComponent(flow.nextY(new FormLocalLabel("ui", "rentserverinfo", new FontOptions(16), 0, this.infoForm.getWidth() / 2, 0, this.infoForm.getWidth() - 20), 10));
        FormTextureButton logoTexture = new FormTextureButton(this.infoForm.getWidth() / 2, 0, () -> GameResources.shockbyte_logo, this.infoForm.getWidth() - 20, -1);
        logoTexture.xAlign = FairType.TextAlign.CENTER;
        this.infoForm.addComponent(flow.nextY(logoTexture, 10));
        this.infoForm.addComponent(flow.nextY(new FormLocalLabel("ui", "rentservermore1", new FontOptions(16), 0, this.infoForm.getWidth() / 2, 0, this.infoForm.getWidth() - 20), 5));
        this.infoForm.addComponent(flow.nextY(new FormLocalLabel("ui", "rentservermore2", new FontOptions(16), 0, this.infoForm.getWidth() / 2, 0, this.infoForm.getWidth() - 20), 10));
        this.infoForm.addComponent(new FormLocalTextButton("ui", "rentserverbutton", 20, flow.next(40), this.infoForm.getWidth() - 40)).onClicked(e -> {
            if (PlatformManager.getPlatform().canOpenURLs()) {
                GameUtils.openURL("https://shockbyte.com/partner/necesse");
            } else {
                ExternalLinkForm externalLink = new ExternalLinkForm(this.getInterfaceStyle().qr_shockbyte, "https://shockbyte.com/partner/necesse", new LocalMessage("ui", "rentserver"), () -> this.makeCurrent(this.infoForm));
                this.addAndMakeCurrentTemporary(externalLink);
            }
        });
        flow.next(10);
        this.infoForm.addComponent(new FormLocalTextButton("ui", "backbutton", 4, flow.next(40), this.infoForm.getWidth() - 8)).onClicked(e -> this.onBackPressed());
        this.infoForm.setHeight(flow.next());
    }

    public abstract void onBackPressed();

    public void submitEscapeEvent(InputEvent event) {
        if (this.isCurrent(this.infoForm)) {
            this.onBackPressed();
            event.use();
        } else {
            this.makeCurrent(this.infoForm);
            event.use();
        }
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.infoForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }
}

