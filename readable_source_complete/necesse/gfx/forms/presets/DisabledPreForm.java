/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import necesse.engine.localization.message.GameMessage;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonTexture;

public class DisabledPreForm
extends Form {
    public DisabledPreForm(int width, GameMessage label, final GameMessage tooltip) {
        super(width, 40);
        this.drawBase = false;
        this.addComponent(new FormLocalLabel(label, new FontOptions(16).color(200, 50, 50), 0, this.getWidth() / 2, 0, width - 4));
        this.addComponent(new FormContentIconButton(this.getWidth() / 2 - 10, 20, FormInputSize.SIZE_20, ButtonColor.BASE, (ButtonTexture)this.getInterfaceStyle().button_help_20, new GameMessage[0]){

            @Override
            public GameTooltips getTooltips(PlayerMob perspective) {
                StringTooltips stringTooltips = new StringTooltips();
                stringTooltips.add(tooltip.translate(), 400);
                return stringTooltips;
            }
        });
    }
}

