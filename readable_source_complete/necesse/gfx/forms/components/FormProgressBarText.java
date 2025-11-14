/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.achievements.Achievement;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FormProgressBarText
extends FormComponent
implements FormPositionContainer {
    private FormPosition position;
    private GameMessage tooltip;
    private boolean isHovering;
    private int width;
    public int currentProgress;
    public int totalProgress;
    public Color completeColor;
    public Color incompleteColor;

    public FormProgressBarText(int x, int y, int totalProgress, int width) {
        this.completeColor = this.getInterfaceStyle().successTextColor;
        this.incompleteColor = this.getInterfaceStyle().errorTextColor;
        this.position = new FormFixedPosition(x, y);
        this.totalProgress = totalProgress;
        this.width = width;
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.isMouseMoveEvent()) {
            this.isHovering = this.isMouseOver(event);
            if (this.isHovering) {
                event.useMove();
            }
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
        if (this.totalProgress == 0) {
            Achievement.drawProgressbar(this.getX(), this.getY() + 6, this.width, 5, 1.0f);
        } else {
            float progress = GameMath.limit(this.totalProgress == 0 ? 1.0f : (float)this.currentProgress / (float)this.totalProgress, 0.0f, 1.0f);
            Achievement.drawProgressbarText(this.getX(), this.getY(), this.width, 5, progress, this.getText(), this.getTextColor());
        }
        if (this.isHovering() && (tooltips = this.getTooltips()) != null) {
            GameTooltipManager.addTooltip(tooltips, TooltipLocation.FORM_FOCUS);
        }
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return FormProgressBarText.singleBox(new Rectangle(this.getX(), this.getY(), this.width, 16));
    }

    public boolean isHovering() {
        return this.isHovering;
    }

    public String getText() {
        return this.currentProgress + "/" + this.totalProgress;
    }

    public Color getTextColor() {
        if (this.currentProgress >= this.totalProgress) {
            return this.completeColor;
        }
        return this.incompleteColor;
    }

    public GameTooltips getTooltips() {
        if (this.tooltip != null) {
            return new StringTooltips(this.tooltip.translate());
        }
        return null;
    }

    public void setTooltip(GameMessage tooltip) {
        this.tooltip = tooltip;
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

