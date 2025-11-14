/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameMath;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.GameColor;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;

public abstract class CurrentModifiersForm
extends Form {
    private ListGameTooltips tooltips;
    private boolean isClickRemoving;

    public static ListGameTooltips getTooltips(PlayerMob perspective) {
        ListGameTooltips tooltips = new ListGameTooltips();
        if (perspective != null) {
            tooltips.add(Localization.translate("buffmodifiers", "currentmodifiers"));
            List modTips = perspective.buffManager.getModifierTooltips().stream().map(mf -> mf.toTooltip(true)).collect(Collectors.toList());
            if (modTips.isEmpty()) {
                tooltips.add(new StringTooltips(Localization.translate("bufftooltip", "nomodifiers"), GameColor.YELLOW));
            } else {
                tooltips.addAll(modTips);
            }
        }
        return tooltips;
    }

    public CurrentModifiersForm() {
        super("currentmodifiers", 200, 300);
        this.onDragged(e -> {
            GameWindow window = WindowManager.getWindow();
            e.x = GameMath.limit(e.x, -this.getWidth() + 20, window.getHudWidth() - 20);
            e.y = GameMath.limit(e.y, -this.getHeight() + 20, window.getHudHeight() - 20);
        });
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        super.handleInputEvent(event, tickManager, perspective);
        if (event.isUsed()) {
            return;
        }
        if (event.getID() == -99) {
            boolean mouseOver = this.isMouseOver(event);
            if (event.state && mouseOver) {
                this.isClickRemoving = true;
            } else {
                if (this.isClickRemoving && mouseOver) {
                    this.onRemove();
                }
                this.isClickRemoving = false;
            }
        }
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        ControllerFocus.add(list, area, this, this.getBoundingBox(), currentXOffset, currentYOffset, this.controllerInitialFocusPriority, customNavigationHandler);
    }

    public abstract void onRemove();

    public void update(PlayerMob perspective) {
        if (perspective != null) {
            this.tooltips = CurrentModifiersForm.getTooltips(perspective);
            this.setWidth(this.tooltips.getWidth());
            this.setHeight(this.tooltips.getHeight());
        } else {
            this.setWidth(100);
            this.setHeight(200);
        }
        this.setDraggingBox(new Rectangle(this.getWidth(), this.getHeight()));
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.setX(GameMath.limit(this.getX(), -this.getWidth() + 20, window.getHudWidth() - 20));
        this.setY(GameMath.limit(this.getY(), -this.getHeight() + 20, window.getHudHeight() - 20));
    }

    @Override
    public void drawBase(TickManager tickManager) {
        int padding = GameBackground.itemTooltip.getContentPadding();
        GameBackground.itemTooltip.getDrawOptions(this.getX(), this.getY(), this.getWidth() + padding * 2, this.getHeight() + padding * 2).draw();
    }

    @Override
    public void drawEdge(TickManager tickManager) {
        int padding = GameBackground.itemTooltip.getContentPadding();
        GameBackground.itemTooltip.getEdgeDrawOptions(this.getX(), this.getY(), this.getWidth() + padding * 2, this.getHeight() + padding * 2).draw();
    }

    @Override
    public void drawComponents(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        super.drawComponents(tickManager, perspective, renderBox);
        if (this.tooltips != null) {
            int padding = GameBackground.itemTooltip.getContentPadding();
            this.tooltips.draw(padding, padding, GameColor.DEFAULT_COLOR);
        }
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.update(perspective);
        super.draw(tickManager, perspective, renderBox);
    }
}

