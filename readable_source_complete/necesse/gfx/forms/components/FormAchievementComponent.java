/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Rectangle;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import necesse.engine.achievements.Achievement;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FormAchievementComponent
extends FormComponent
implements FormPositionContainer {
    protected FormPosition position;
    protected int width;
    public final Achievement achievement;
    protected FairTypeDrawOptions descDrawOptions = null;
    protected boolean isHovering;

    public FormAchievementComponent(int x, int y, int width, Achievement achievement) {
        this.position = new FormFixedPosition(x, y);
        this.width = width;
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
        ControllerFocus.add(list, area, this, this.getBoundingBox(), currentXOffset, currentYOffset, this.controllerInitialFocusPriority, customNavigationHandler);
    }

    protected FairTypeDrawOptions getDescDrawOptions() {
        if (this.descDrawOptions == null) {
            FairType type = new FairType();
            type.append(new FontOptions(12), this.achievement.description.translate());
            this.descDrawOptions = type.getDrawOptions(FairType.TextAlign.LEFT, this.width - 60, true, true);
        }
        return this.descDrawOptions;
    }

    public void updateDrawables() {
        this.descDrawOptions = null;
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        ListGameTooltips tooltips = new ListGameTooltips();
        this.achievement.drawIcon(this.getX() + 2, this.getY() + 4);
        String fullName = this.achievement.name.translate();
        FontOptions nameOptions = new FontOptions(16).color(this.getInterfaceStyle().highlightTextColor);
        String cutName = GameUtils.maxString(fullName, nameOptions, this.width - 50);
        FontManager.bit.drawString(this.getX() + 50, this.getY() + 4, cutName, nameOptions);
        if (this.isHovering && !fullName.equals(cutName)) {
            tooltips.add(fullName);
        }
        int descHeight = this.getDescDrawOptions().getBoundingBox().height;
        this.getDescDrawOptions().draw(this.getX() + 50, this.getY() + 20, this.getInterfaceStyle().activeTextColor);
        int progressY = this.getY() + 20 + descHeight + 2;
        this.achievement.drawProgress(this.getX() + 55, progressY, this.width - 60, false);
        if (this.achievement.isCompleted() && this.isHovering && this.achievement.getTimeCompleted() > 0L) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime timeUTC = LocalDateTime.ofInstant(Instant.ofEpochSecond(this.achievement.getTimeCompleted()), ZoneId.systemDefault());
            String dateString = formatter.format(timeUTC);
            tooltips.add(Localization.translate("achievement", "completedon", "date", dateString));
        }
        if (!tooltips.isEmpty()) {
            GameTooltipManager.addTooltip(tooltips, TooltipLocation.FORM_FOCUS);
        }
    }

    @Override
    public List<Rectangle> getHitboxes() {
        int descHeight = this.getDescDrawOptions().getBoundingBox().height;
        return FormAchievementComponent.singleBox(new Rectangle(this.getX(), this.getY(), this.width, 20 + descHeight + 2 + 20));
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

