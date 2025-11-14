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
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FormIcon
extends FormComponent
implements FormPositionContainer {
    private FormPosition position;
    private int width;
    private int height;
    private GameTexture texture;
    private Color drawColor;
    protected boolean isHovering;

    public FormIcon(int x, int y, int width, int height, GameTexture texture, float grayScaleValue) {
        this(x, y, width, height, texture, new Color(grayScaleValue, grayScaleValue, grayScaleValue));
    }

    public FormIcon(int x, int y, int width, int height, GameTexture texture, Color drawColor) {
        this.position = new FormFixedPosition(x, y);
        this.width = width;
        this.height = height;
        this.texture = texture;
        this.drawColor = drawColor;
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.isMouseMoveEvent()) {
            this.isHovering = this.isMouseOver(event);
        }
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        GameTooltips tooltips;
        GameTexture.overrideBlendQuality = GameTexture.BlendQuality.NEAREST;
        this.texture.initDraw().color(this.drawColor).size(this.width, this.height).draw(this.getX(), this.getY());
        GameTexture.overrideBlendQuality = null;
        if (this.isHovering && (tooltips = this.getTooltips()) != null) {
            GameTooltipManager.addTooltip(tooltips, TooltipLocation.FORM_FOCUS);
        }
    }

    public GameTooltips getTooltips() {
        return null;
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return FormIcon.singleBox(new Rectangle(this.getX(), this.getY(), this.width, this.height));
    }

    @Override
    public FormPosition getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(FormPosition position) {
        this.position = position;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setTexture(GameTexture texture) {
        this.texture = texture;
    }

    public void setGreyScaleValue(float value) {
        this.drawColor = new Color(value, value, value);
    }
}

