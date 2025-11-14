/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormButtonToggle;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameTexture.GameTexture;

public class FormHorizontalToggle
extends FormButtonToggle
implements FormPositionContainer {
    private FormPosition position;

    public FormHorizontalToggle(int x, int y) {
        this.position = new FormFixedPosition(x, y);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        Color drawCol = this.getDrawColor();
        if (this.isToggled()) {
            GameTexture texture = this.isHovering() ? this.getInterfaceStyle().toggle_on.active : this.getInterfaceStyle().toggle_on.highlighted;
            texture.initDraw().color(drawCol).draw(this.getX(), this.getY());
        } else {
            GameTexture texture = this.isHovering() ? this.getInterfaceStyle().toggle_off.active : this.getInterfaceStyle().toggle_off.highlighted;
            texture.initDraw().color(drawCol).draw(this.getX(), this.getY());
        }
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return FormHorizontalToggle.singleBox(new Rectangle(this.getX(), this.getY(), 32, 16));
    }

    @Override
    public boolean isMouseOver(InputEvent event) {
        Point mousePoint = new Point(event.pos.hudX, event.pos.hudY);
        return new Rectangle(this.getX(), this.getY(), 32, 16).contains(mousePoint);
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

