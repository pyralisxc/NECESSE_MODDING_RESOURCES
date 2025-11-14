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
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.quest.Quest;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.drawOptions.DrawOptionsBox;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.fairType.FairTypeDrawOptionsContainer;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FormQuestComponent
extends FormComponent
implements FormPositionContainer {
    protected FormPosition position;
    protected int width;
    public final NetworkClient client;
    public final Quest quest;
    public boolean outlined = false;
    public boolean showTitle = true;
    public boolean showDescription = true;
    public boolean showReward = true;
    public boolean showHandIn = true;
    protected FairTypeDrawOptionsContainer titleDrawOptions;
    protected FairTypeDrawOptionsContainer descDrawOptions;
    protected FairTypeDrawOptionsContainer rewardDrawOptions;
    protected FairTypeDrawOptionsContainer handInDrawOptions;
    protected boolean isHovering;
    public Color titleColor;
    public Color textColor;

    public FormQuestComponent(int x, int y, int width, NetworkClient client, Quest quest) {
        this.titleColor = this.getInterfaceStyle().highlightTextColor;
        this.textColor = this.getInterfaceStyle().activeTextColor;
        this.position = new FormFixedPosition(x, y);
        this.width = width;
        this.client = client;
        this.quest = quest;
        this.titleDrawOptions = new FairTypeDrawOptionsContainer(() -> {
            GameMessage title = this.quest.getTitle();
            if (title == null) {
                return null;
            }
            FairType type = new FairType().append(new FontOptions(16).outline(this.outlined), title.translate());
            return type.getDrawOptions(FairType.TextAlign.LEFT, this.width - 20, true, 1, true, null, true);
        });
        this.descDrawOptions = new FairTypeDrawOptionsContainer(() -> {
            GameMessage description = this.quest.getDescription();
            if (description == null) {
                return null;
            }
            FairType type = new FairType().append(new FontOptions(this.showTitle ? 12 : 16).outline(this.outlined), description.translate());
            return type.getDrawOptions(FairType.TextAlign.LEFT, this.width - 20, true, true);
        });
        this.rewardDrawOptions = new FairTypeDrawOptionsContainer(() -> {
            FairType type = this.quest.getRewardType(this.client, this.outlined);
            if (type != null) {
                return type.getDrawOptions(FairType.TextAlign.LEFT, this.width - 20, true, true);
            }
            return null;
        });
        this.handInDrawOptions = new FairTypeDrawOptionsContainer(() -> {
            FairType type = this.quest.getHandInType(this.client, this.outlined);
            if (type != null) {
                return type.getDrawOptions(FairType.TextAlign.LEFT, this.width - 20, true, true);
            }
            return null;
        });
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        FairTypeDrawOptions handInDrawOptions;
        FairTypeDrawOptions rewardDrawOptions;
        FairTypeDrawOptions descDrawOptions;
        FairTypeDrawOptions titleDrawOptions;
        if (event.isMouseMoveEvent()) {
            this.isHovering = this.isMouseOver(event);
            if (this.isHovering) {
                event.useMove();
            }
        }
        int drawY = this.getY();
        if (this.showTitle && (titleDrawOptions = this.titleDrawOptions.get()) != null) {
            titleDrawOptions.handleInputEvent(this.getX(), drawY, event);
            drawY += titleDrawOptions.getBoundingBox().height + 2;
        }
        if (this.showDescription && (descDrawOptions = this.descDrawOptions.get()) != null) {
            descDrawOptions.handleInputEvent(this.getX(), drawY, event);
            drawY += descDrawOptions.getBoundingBox().height + 2;
        }
        DrawOptionsBox progressDrawBox = this.quest.getProgressDrawBox(this.client, this.getX(), drawY, this.width - 20, this.textColor, false);
        drawY += progressDrawBox.getBoundingBox().height;
        if (this.showReward && (rewardDrawOptions = this.rewardDrawOptions.get()) != null) {
            rewardDrawOptions.handleInputEvent(this.getX(), drawY, event);
            drawY += rewardDrawOptions.getBoundingBox().height + 2;
        }
        if (this.showHandIn && (handInDrawOptions = this.handInDrawOptions.get()) != null) {
            handInDrawOptions.handleInputEvent(this.getX(), drawY, event);
            drawY += handInDrawOptions.getBoundingBox().height + 2;
        }
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
    }

    public void updateDrawables() {
        this.titleDrawOptions.reset();
        this.descDrawOptions.reset();
        this.rewardDrawOptions.reset();
        this.handInDrawOptions.reset();
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        FairTypeDrawOptions handInDrawOptions;
        FairTypeDrawOptions rewardDrawOptions;
        FairTypeDrawOptions descDrawOptions;
        FairTypeDrawOptions titleDrawOptions;
        ListGameTooltips tooltips = new ListGameTooltips();
        int drawY = this.getY();
        if (this.showTitle && (titleDrawOptions = this.titleDrawOptions.get()) != null) {
            titleDrawOptions.draw(this.getX(), drawY, this.titleColor);
            drawY += titleDrawOptions.getBoundingBox().height + 2;
            if (this.isHovering && !titleDrawOptions.displaysEverything()) {
                tooltips.add(this.quest.getTitle().translate());
            }
        }
        if (this.showDescription && (descDrawOptions = this.descDrawOptions.get()) != null) {
            descDrawOptions.draw(this.getX(), drawY, this.textColor);
            drawY += descDrawOptions.getBoundingBox().height + 2;
        }
        DrawOptionsBox progressDrawBox = this.quest.getProgressDrawBox(this.client, this.getX(), drawY, this.width - 20, this.textColor, false);
        progressDrawBox.draw();
        drawY += progressDrawBox.getBoundingBox().height;
        if (this.showReward && (rewardDrawOptions = this.rewardDrawOptions.get()) != null) {
            rewardDrawOptions.draw(this.getX(), drawY, this.textColor);
            drawY += rewardDrawOptions.getBoundingBox().height + 2;
        }
        if (this.showHandIn && (handInDrawOptions = this.handInDrawOptions.get()) != null) {
            handInDrawOptions.draw(this.getX(), drawY, this.textColor);
            drawY += handInDrawOptions.getBoundingBox().height + 2;
        }
        if (!tooltips.isEmpty()) {
            GameTooltipManager.addTooltip(tooltips, TooltipLocation.FORM_FOCUS);
        }
    }

    @Override
    public List<Rectangle> getHitboxes() {
        FairTypeDrawOptions handInDrawOptions;
        Rectangle box;
        FairTypeDrawOptions rewardDrawOptions;
        FairTypeDrawOptions descDrawOptions;
        Rectangle box2;
        FairTypeDrawOptions titleDrawOptions;
        int width = 0;
        int height = 0;
        if (this.showTitle && (titleDrawOptions = this.titleDrawOptions.get()) != null) {
            box2 = titleDrawOptions.getBoundingBox();
            height += box2.height + 2;
            width = Math.max(width, box2.width);
        }
        if (this.showDescription && (descDrawOptions = this.descDrawOptions.get()) != null) {
            box2 = descDrawOptions.getBoundingBox();
            height += box2.height + 2;
            width = Math.max(width, box2.width);
        }
        Rectangle progressBox = this.quest.getProgressDrawBox(this.client, this.getX(), this.getY() + height, this.width - 20, this.textColor, false).getBoundingBox();
        height += progressBox.height;
        width = Math.max(width, progressBox.width);
        if (this.showReward && (rewardDrawOptions = this.rewardDrawOptions.get()) != null) {
            box = rewardDrawOptions.getBoundingBox();
            height += box.height + 2;
            width = Math.max(width, box.width);
        }
        if (this.showHandIn && (handInDrawOptions = this.handInDrawOptions.get()) != null) {
            box = handInDrawOptions.getBoundingBox();
            height += box.height + 2;
            width = Math.max(width, box.width);
        }
        return FormQuestComponent.singleBox(new Rectangle(this.getX(), this.getY(), width, height));
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

