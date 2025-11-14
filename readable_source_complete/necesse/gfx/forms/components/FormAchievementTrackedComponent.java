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
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FormAchievementTrackedComponent
extends FormComponent
implements FormPositionContainer {
    protected FormPosition position;
    protected int width;
    protected int progressWidth;
    public final Achievement achievement;
    protected boolean isHovering;
    protected FairTypeDrawOptions titleDrawOptions;
    public Color nameColor = new Color(220, 220, 220);

    public FormAchievementTrackedComponent(int x, int y, int width, int progressWidth, Achievement achievement) {
        this.position = new FormFixedPosition(x, y);
        this.width = width;
        this.progressWidth = progressWidth;
        this.achievement = achievement;
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

    protected FairTypeDrawOptions getTitleDrawOptions() {
        GameMessage title = this.achievement.name;
        if (title == null) {
            return null;
        }
        if (this.titleDrawOptions == null || title.hasUpdated()) {
            FairType type = new FairType();
            type.append(new FontOptions(16).outline(), title.translate());
            this.titleDrawOptions = type.getDrawOptions(FairType.TextAlign.LEFT, this.width - 20, true, true);
        }
        return this.titleDrawOptions;
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        int drawY = this.getY() + 4;
        FairTypeDrawOptions titleDrawOptions = this.getTitleDrawOptions();
        titleDrawOptions.draw(this.getX() + 10, drawY, this.nameColor);
        this.achievement.drawProgress(this.getX() + 10, drawY += titleDrawOptions.getBoundingBox().height + 2, this.progressWidth, true);
        if (this.isHovering && !titleDrawOptions.displaysEverything()) {
            GameTooltipManager.addTooltip(new StringTooltips(this.achievement.name.translate()), TooltipLocation.FORM_FOCUS);
        }
    }

    @Override
    public List<Rectangle> getHitboxes() {
        int width = 0;
        int height = 4;
        FairTypeDrawOptions titleDrawOptions = this.getTitleDrawOptions();
        Rectangle titleBoundingBox = titleDrawOptions.getBoundingBox();
        height += titleBoundingBox.height + 2;
        width = Math.max(width, titleBoundingBox.width);
        width = Math.max(width, this.progressWidth);
        return FormAchievementTrackedComponent.singleBox(new Rectangle(this.getX(), this.getY(), width, height += 20));
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

