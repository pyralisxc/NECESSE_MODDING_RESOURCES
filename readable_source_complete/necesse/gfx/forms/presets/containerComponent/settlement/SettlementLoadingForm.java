/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.settlement;

import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.gameFont.FontOptions;

public class SettlementLoadingForm
extends Form {
    public SettlementLoadingForm(int width, int height) {
        super("loading", width, height);
        FormLocalLabel label = this.addComponent(new FormLocalLabel("ui", "loadingdotdot", new FontOptions(16), 0, width / 2, height / 2, width - 20));
        label.setY(width / 2 - label.getBoundingBox().height / 2);
    }
}

