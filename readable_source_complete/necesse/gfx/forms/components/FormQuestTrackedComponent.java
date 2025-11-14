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
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FormQuestTrackedComponent
extends FormComponent
implements FormPositionContainer {
    protected FormPosition position;
    protected int width;
    protected int progressWidth;
    public final NetworkClient client;
    public final Quest quest;
    protected FairTypeDrawOptionsContainer titleDrawOptions;
    protected FairTypeDrawOptionsContainer descDrawOptions;
    protected FairTypeDrawOptionsContainer rewardDrawOptions;
    protected FairTypeDrawOptionsContainer handInDrawOptions;
    protected boolean isHovering;
    public Color textColor = new Color(220, 220, 220);

    public FormQuestTrackedComponent(int x, int y, int width, int progressWidth, NetworkClient client, Quest quest) {
        this.position = new FormFixedPosition(x, y);
        this.width = width;
        this.progressWidth = progressWidth;
        this.client = client;
        this.quest = quest;
        this.titleDrawOptions = new FairTypeDrawOptionsContainer(() -> {
            GameMessage title = this.quest.getTitle();
            if (title == null) {
                return null;
            }
            FairType type = new FairType().append(new FontOptions(16).outline(), title.translate());
            return type.getDrawOptions(FairType.TextAlign.LEFT, this.width - 20, true, true);
        });
        this.descDrawOptions = new FairTypeDrawOptionsContainer(() -> {
            GameMessage description = this.quest.getDescription();
            if (description == null) {
                return null;
            }
            FairType type = new FairType().append(new FontOptions(12).outline(), description.translate());
            return type.getDrawOptions(FairType.TextAlign.LEFT, this.width - 20, true, true);
        });
        this.rewardDrawOptions = new FairTypeDrawOptionsContainer(() -> {
            FairType type = this.quest.getRewardType(this.client, true);
            if (type != null) {
                return type.getDrawOptions(FairType.TextAlign.LEFT, this.width - 20, true, true);
            }
            return null;
        });
        this.handInDrawOptions = new FairTypeDrawOptionsContainer(() -> {
            FairType type = this.quest.getHandInType(this.client, true);
            if (type != null) {
                return type.getDrawOptions(FairType.TextAlign.LEFT, this.width - 20, true, true);
            }
            return null;
        });
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        FairTypeDrawOptions handInDrawOptions;
        FairTypeDrawOptions descDrawOptions;
        if (event.isMouseMoveEvent()) {
            this.isHovering = this.isMouseOver(event);
            if (this.isHovering) {
                event.useMove();
            }
        }
        int drawY = this.getY() + 4;
        FairTypeDrawOptions titleDrawOptions = this.titleDrawOptions.get();
        if (titleDrawOptions != null) {
            titleDrawOptions.handleInputEvent(this.getX(), drawY, event);
            drawY += titleDrawOptions.getBoundingBox().height + 2;
        }
        if ((descDrawOptions = this.descDrawOptions.get()) != null) {
            descDrawOptions.handleInputEvent(this.getX(), drawY, event);
            drawY += descDrawOptions.getBoundingBox().height + 2;
        }
        DrawOptionsBox progressDrawBox = this.quest.getProgressDrawBox(this.client, this.getX(), drawY, this.progressWidth, this.textColor, true);
        drawY += progressDrawBox.getBoundingBox().height;
        FairTypeDrawOptions rewardDrawOptions = this.rewardDrawOptions.get();
        if (rewardDrawOptions != null) {
            rewardDrawOptions.handleInputEvent(this.getX(), drawY, event);
            drawY += rewardDrawOptions.getBoundingBox().height + 2;
        }
        if ((handInDrawOptions = this.handInDrawOptions.get()) != null) {
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

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        FairTypeDrawOptions handInDrawOptions;
        FairTypeDrawOptions descDrawOptions;
        int drawY = this.getY() + 4;
        FairTypeDrawOptions titleDrawOptions = this.titleDrawOptions.get();
        if (titleDrawOptions != null) {
            titleDrawOptions.draw(this.getX(), drawY, this.textColor);
            drawY += titleDrawOptions.getBoundingBox().height + 2;
        }
        if ((descDrawOptions = this.descDrawOptions.get()) != null) {
            descDrawOptions.draw(this.getX(), drawY, this.textColor);
            drawY += descDrawOptions.getBoundingBox().height + 2;
        }
        DrawOptionsBox progressDrawBox = this.quest.getProgressDrawBox(this.client, this.getX(), drawY, this.progressWidth, this.textColor, true);
        progressDrawBox.draw();
        drawY += progressDrawBox.getBoundingBox().height;
        FairTypeDrawOptions rewardDrawOptions = this.rewardDrawOptions.get();
        if (rewardDrawOptions != null) {
            rewardDrawOptions.draw(this.getX(), drawY, this.textColor);
            drawY += rewardDrawOptions.getBoundingBox().height + 2;
        }
        if ((handInDrawOptions = this.handInDrawOptions.get()) != null) {
            handInDrawOptions.draw(this.getX(), drawY, this.textColor);
            drawY += handInDrawOptions.getBoundingBox().height + 2;
        }
        if (this.isHovering && titleDrawOptions != null && !titleDrawOptions.displaysEverything()) {
            GameTooltipManager.addTooltip(new StringTooltips(this.quest.getTitle().translate()), TooltipLocation.FORM_FOCUS);
        }
    }

    @Override
    public List<Rectangle> getHitboxes() {
        FairTypeDrawOptions handInDrawOptions;
        FairTypeDrawOptions descDrawOptions;
        int width = 0;
        int height = 4;
        FairTypeDrawOptions titleDrawOptions = this.titleDrawOptions.get();
        if (titleDrawOptions != null) {
            Rectangle box = titleDrawOptions.getBoundingBox();
            height += box.height + 2;
            width = Math.max(width, box.width);
        }
        if ((descDrawOptions = this.descDrawOptions.get()) != null) {
            Rectangle box = descDrawOptions.getBoundingBox();
            height += box.height + 2;
            width = Math.max(width, box.width);
        }
        Rectangle progressBox = this.quest.getProgressDrawBox(this.client, this.getX(), this.getY() + height, this.progressWidth - 20, this.textColor, true).getBoundingBox();
        height += progressBox.height;
        width = Math.max(width, progressBox.width);
        FairTypeDrawOptions rewardDrawOptions = this.rewardDrawOptions.get();
        if (rewardDrawOptions != null) {
            Rectangle box = rewardDrawOptions.getBoundingBox();
            height += box.height + 2;
            width = Math.max(width, box.width);
        }
        if ((handInDrawOptions = this.handInDrawOptions.get()) != null) {
            Rectangle box = handInDrawOptions.getBoundingBox();
            height += box.height + 2;
            width = Math.max(width, box.width);
        }
        return FormQuestTrackedComponent.singleBox(new Rectangle(this.getX(), this.getY(), width, height));
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

