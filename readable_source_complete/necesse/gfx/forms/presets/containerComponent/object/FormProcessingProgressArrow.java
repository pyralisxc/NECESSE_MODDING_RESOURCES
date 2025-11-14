/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Rectangle;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.GameColor;
import necesse.gfx.forms.components.FormCustomDraw;
import necesse.gfx.forms.presets.containerComponent.object.ProcessingHelp;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.SpacerGameTooltip;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FormProcessingProgressArrow
extends FormCustomDraw {
    protected ProcessingHelp help;

    public FormProcessingProgressArrow(int x, int y, ProcessingHelp help) {
        super(x, y, 32, 32);
        this.help = help;
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        GameTooltips currentRecipeTooltip;
        this.getInterfaceStyle().processing_arrow_empty.initDraw().draw(this.getX(), this.getY());
        if (this.help.isProcessing()) {
            float progress = this.help.getProcessingProgress();
            int leftPadding = 2;
            int rightPadding = 2;
            int width = this.getInterfaceStyle().processing_arrow_full.getWidth() - (leftPadding + rightPadding);
            this.getInterfaceStyle().processing_arrow_full.initDraw().section(leftPadding, leftPadding + (int)((float)width * progress), 0, this.getInterfaceStyle().processing_arrow_full.getHeight()).draw(this.getX() + leftPadding, this.getY());
            if (this.isHovering()) {
                ListGameTooltips tooltips = new ListGameTooltips();
                GameTooltips currentRecipeTooltip2 = this.help.getCurrentRecipeTooltip(perspective);
                if (currentRecipeTooltip2 != null) {
                    tooltips.add(currentRecipeTooltip2);
                    tooltips.add(new SpacerGameTooltip(5));
                }
                tooltips.add((int)(progress * 100.0f) + "%");
                if (currentRecipeTooltip2 != null) {
                    GameTooltipManager.addTooltip(tooltips, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
                } else {
                    GameTooltipManager.addTooltip(tooltips, TooltipLocation.FORM_FOCUS);
                }
            }
        } else if (this.isHovering() && (currentRecipeTooltip = this.help.getCurrentRecipeTooltip(perspective)) != null) {
            ListGameTooltips tooltips = new ListGameTooltips(currentRecipeTooltip);
            if (this.help.needsFuel()) {
                tooltips.add(new StringTooltips(Localization.translate("ui", "needfuel"), GameColor.RED));
            }
            GameTooltipManager.addTooltip(tooltips, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
        }
    }
}

