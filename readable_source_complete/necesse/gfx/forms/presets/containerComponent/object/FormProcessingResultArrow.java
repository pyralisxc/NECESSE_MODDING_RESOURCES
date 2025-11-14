/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Rectangle;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.forms.components.FormCustomDraw;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public abstract class FormProcessingResultArrow
extends FormCustomDraw {
    public FormProcessingResultArrow(int x, int y) {
        super(x, y, 32, 32);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.getInterfaceStyle().processing_arrow_empty.initDraw().draw(this.getX(), this.getY());
        ResultArrowState state = this.getState(perspective);
        if (state != null) {
            int leftPadding = 2;
            int rightPadding = 2;
            int width = this.getInterfaceStyle().processing_arrow_full.getWidth() - (leftPadding + rightPadding);
            this.getInterfaceStyle().processing_arrow_full.initDraw().section(leftPadding, leftPadding + (int)((float)width * state.arrowProgress), 0, this.getInterfaceStyle().processing_arrow_full.getHeight()).draw(this.getX() + leftPadding, this.getY());
            if (this.isHovering() && state.tooltips != null) {
                GameTooltipManager.addTooltip(state.tooltips, state.tooltipsBackground, TooltipLocation.FORM_FOCUS);
            }
        }
    }

    public abstract ResultArrowState getState(PlayerMob var1);

    public static class ResultArrowState {
        public float arrowProgress;
        public GameTooltips tooltips;
        public GameBackground tooltipsBackground;

        public ResultArrowState(float arrowProgress, GameTooltips tooltips, GameBackground tooltipsBackground) {
            this.arrowProgress = arrowProgress;
            this.tooltips = tooltips;
            this.tooltipsBackground = tooltipsBackground;
        }

        public ResultArrowState(float arrowProgress, GameTooltips tooltips) {
            this(arrowProgress, tooltips, null);
        }
    }
}

