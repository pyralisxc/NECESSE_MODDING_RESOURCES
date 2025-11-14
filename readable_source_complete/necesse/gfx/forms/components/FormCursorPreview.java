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
import necesse.gfx.GameResources;
import necesse.gfx.Renderer;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;

public class FormCursorPreview
extends FormComponent
implements FormPositionContainer {
    private FormPosition position;
    private Color color;
    private int size;

    public FormCursorPreview(int x, int y, Color color, int size) {
        this.position = new FormFixedPosition(x, y);
        this.setColor(color);
        this.setSize(size);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setSize(int size) {
        this.size = size;
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
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        float zoom = Renderer.getCursorSizeZoom(this.size);
        int res = (int)(32.0f * zoom);
        GameResources.cursors.initDraw().sprite(0, 0, 32).size(res, res).color(this.color).draw(this.getX(), this.getY());
    }

    @Override
    public List<Rectangle> getHitboxes() {
        float zoom = Renderer.getCursorSizeZoom(this.size);
        int res = (int)(32.0f * zoom);
        return FormCursorPreview.singleBox(new Rectangle(this.getX(), this.getY(), res, res));
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

