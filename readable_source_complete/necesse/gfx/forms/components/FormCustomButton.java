/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public abstract class FormCustomButton
extends FormButton
implements FormPositionContainer {
    private FormPosition position;
    private int width;
    private int height;
    private GameMessage[] tooltips;

    public FormCustomButton(int x, int y, int width, int height, GameMessage ... tooltips) {
        this.position = new FormFixedPosition(x, y);
        this.width = width;
        this.height = height;
        this.tooltips = tooltips;
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        GameTooltips tooltips;
        Color drawCol = this.getDrawColor();
        this.draw(drawCol, this.getX(), this.getY(), perspective);
        if (this.isHovering() && (tooltips = this.getTooltips()) != null) {
            GameTooltipManager.addTooltip(tooltips, TooltipLocation.FORM_FOCUS);
        }
    }

    public abstract void draw(Color var1, int var2, int var3, PlayerMob var4);

    @Override
    public List<Rectangle> getHitboxes() {
        return FormCustomButton.singleBox(new Rectangle(this.getX(), this.getY(), this.width, this.height));
    }

    public GameTooltips getTooltips() {
        if (this.tooltips.length != 0) {
            StringTooltips out = new StringTooltips();
            for (GameMessage tooltip : this.tooltips) {
                out.add(tooltip.translate());
            }
            return out;
        }
        return null;
    }

    public void setTooltips(GameMessage ... tooltips) {
        this.tooltips = tooltips;
    }

    @Override
    public FormPosition getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(FormPosition position) {
        this.position = position;
    }
}

