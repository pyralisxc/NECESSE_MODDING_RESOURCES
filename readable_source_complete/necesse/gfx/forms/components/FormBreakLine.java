/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;

public class FormBreakLine
extends FormComponent
implements FormPositionContainer {
    public static int ALIGN_BEGINNING = -1;
    public static int ALIGN_MID = 0;
    public static int ALIGN_END = 1;
    private FormPosition position;
    public int length;
    public int align;
    public boolean horizontal;
    public Color color;

    public FormBreakLine(int align, int x, int y, int length, boolean horizontal) {
        this.color = this.getInterfaceStyle().activeTextColor;
        this.align = align;
        this.position = new FormFixedPosition(x, y);
        this.length = length;
        this.horizontal = horizontal;
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        int offset = this.length / 2;
        if (this.align == ALIGN_BEGINNING) {
            offset = 0;
        } else if (this.align == ALIGN_END) {
            offset = this.length;
        }
        if (this.horizontal) {
            Renderer.initQuadDraw(this.length, 2).color(this.color).draw(this.getX() - offset, this.getY());
        } else {
            Renderer.initQuadDraw(2, this.length).color(this.color).draw(this.getX(), this.getY() - offset);
        }
    }

    @Override
    public List<Rectangle> getHitboxes() {
        int offset = this.length / 2;
        if (this.align == ALIGN_BEGINNING) {
            offset = 0;
        } else if (this.align == ALIGN_END) {
            offset = this.length;
        }
        if (this.horizontal) {
            return FormBreakLine.singleBox(new Rectangle(this.getX() - offset, this.getY(), this.length, 2));
        }
        return FormBreakLine.singleBox(new Rectangle(this.getX(), this.getY() - offset, 2, this.length));
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
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

