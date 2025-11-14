/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.mob;

import java.awt.Rectangle;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormTooltipsDraw;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;

public abstract class EquipmentModifiersForm
extends Form {
    private FormContentBox content;
    private FormTooltipsDraw tooltips;

    public static ListGameTooltips getTooltips(Mob mob) {
        ListGameTooltips tooltips = new ListGameTooltips();
        if (mob != null) {
            tooltips.add(Localization.translate("buffmodifiers", "currentmodifiers"));
            List modTips = mob.buffManager.getModifierTooltips().stream().map(mf -> mf.toTooltip(true)).collect(Collectors.toList());
            if (modTips.isEmpty()) {
                tooltips.add(new StringTooltips(Localization.translate("bufftooltip", "nomodifiers"), GameColor.YELLOW));
            } else {
                tooltips.addAll(modTips);
            }
        }
        return tooltips;
    }

    public EquipmentModifiersForm(String name, int width, int height) {
        super(name, width, height);
        this.content = this.addComponent(new FormContentBox(0, 0, width, height));
        this.tooltips = this.content.addComponent(new FormTooltipsDraw(4, 4, null));
    }

    public abstract Mob getMob();

    public void update() {
        this.tooltips.setTooltips(EquipmentModifiersForm.getTooltips(this.getMob()));
        this.content.setContentBox(new Rectangle(this.tooltips.getWidth() + 8, this.tooltips.getHeight() + 8));
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.update();
        super.draw(tickManager, perspective, renderBox);
    }
}

