/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.registries.ItemRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class FormItemPreview
extends FormComponent
implements FormPositionContainer {
    public boolean useHoverMoveEvents = true;
    private boolean isHovering;
    protected FormEventsHandler<FormInputEvent<FormItemPreview>> changedHoverEvents;
    private FormPosition position;
    private InventoryItem item;
    public int size;

    public FormItemPreview(int x, int y, int size, Item item) {
        this.position = new FormFixedPosition(x, y);
        this.size = size;
        this.changedHoverEvents = new FormEventsHandler();
        this.setItem(item);
    }

    public FormItemPreview(int x, int y, int size, String itemStringID) {
        this(x, y, size, ItemRegistry.getItem(itemStringID));
    }

    public FormItemPreview(int x, int y, Item item) {
        this(x, y, 32, item);
    }

    public FormItemPreview(int x, int y, String itemStringID) {
        this(x, y, 32, itemStringID);
    }

    public FormItemPreview onChangedHover(FormEventListener<FormInputEvent<FormItemPreview>> listener) {
        this.changedHoverEvents.addListener(listener);
        return this;
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.isMouseMoveEvent()) {
            boolean nextIsMouseOver = this.isMouseOver(event);
            if (this.isHovering != nextIsMouseOver) {
                this.isHovering = nextIsMouseOver;
                FormInputEvent<FormItemPreview> fEvent = new FormInputEvent<FormItemPreview>(this, event);
                this.changedHoverEvents.onEvent(fEvent);
            }
            if (nextIsMouseOver && this.useHoverMoveEvents) {
                event.useMove();
            }
        }
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        if (this.allowControllerFocus()) {
            ControllerFocus.add(list, area, this, this.getBoundingBox(), currentXOffset, currentYOffset, this.controllerInitialFocusPriority, customNavigationHandler);
        }
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        InventoryItem drawItem = this.getDrawItem(perspective);
        if (drawItem != null) {
            drawItem.drawIcon(perspective, this.getX(), this.getY(), this.size, null);
        } else {
            this.drawEmpty(tickManager, perspective, renderBox);
        }
        if (this.isHovering()) {
            this.addTooltips(drawItem, perspective);
        }
    }

    public void drawEmpty(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
    }

    public boolean isHovering() {
        return this.isHovering || this.isControllerFocus();
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return FormItemPreview.singleBox(new Rectangle(this.getX(), this.getY(), this.size, this.size));
    }

    public boolean allowControllerFocus() {
        return false;
    }

    public InventoryItem getDrawItem(PlayerMob perspective) {
        return this.item;
    }

    public void addTooltips(InventoryItem item, PlayerMob perspective) {
    }

    public FormItemPreview setItem(Item item) {
        this.item = item != null ? new InventoryItem(item) : null;
        return this;
    }

    public FormItemPreview setSize(int size) {
        this.size = size;
        return this;
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

