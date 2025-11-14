/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.containerSlot;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Control;
import necesse.engine.input.InputEvent;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketContainerAction;
import necesse.engine.screenHudManager.UniqueScreenFloatText;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.forms.components.FormTypingComponent;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerAction;
import necesse.inventory.container.ContainerActionResult;

public class FormContainerToolbarSlot
extends FormContainerSlot {
    private PlayerMob player;

    public FormContainerToolbarSlot(Client client, Container container, int containerSlotIndex, int x, int y, PlayerMob player) {
        super(client, container, containerSlotIndex, x, y);
        this.player = player;
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        this.handleMouseMoveEvent(event);
        if (this.player.isInventoryExtended()) {
            if (event.state) {
                this.handleActionInputEvents(event);
            }
        } else if (!this.player.hotbarLocked && event.state) {
            ContainerAction action = null;
            if (event.getID() == -100 && this.isMouseOver(event)) {
                if (!WindowManager.getWindow().isKeyDown(340) || !FormTypingComponent.appendItemToTyping(this.getContainerSlot().getItem())) {
                    if (Control.INV_LOCK.isDown() && this.canCurrentlyLockItem()) {
                        action = ContainerAction.TOGGLE_LOCKED;
                    } else {
                        this.player.setSelectedSlot(this.getContainerSlot().getInventorySlot());
                    }
                }
                event.use();
            } else if (event.getID() == -99 && this.isMouseOver(event)) {
                action = Control.INV_LOCK.isDown() && this.canCurrentlyLockItem() ? ContainerAction.TOGGLE_LOCKED : ContainerAction.RIGHT_CLICK_ACTION;
                event.use();
            }
            if (action != null) {
                ContainerActionResult result = this.getContainer().applyContainerAction(this.containerSlotIndex, action);
                this.client.network.sendPacket(new PacketContainerAction(this.containerSlotIndex, action, result.value));
                if (result.error != null) {
                    GameWindow window = WindowManager.getWindow();
                    Renderer.hudManager.addElement(new UniqueScreenFloatText(window.mousePos().hudX, window.mousePos().hudY, result.error, new FontOptions(16).outline(), "slotError"));
                }
                if ((result.value != 0 || result.error != null) && event.shouldSubmitSound()) {
                    this.playTickSound();
                }
            }
        }
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        if (this.player.isInventoryExtended()) {
            ControllerFocus.add(list, area, this, this.getBoundingBox(), currentXOffset, currentYOffset, this.controllerInitialFocusPriority, customNavigationHandler);
        }
    }

    @Override
    public boolean canCurrentlyQuickTrash() {
        return this.player.isInventoryExtended();
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.isSelected = this.player.getSelectedSlot() == this.getContainerSlot().getInventorySlot();
        super.draw(tickManager, perspective, renderBox);
    }

    @Override
    public void drawDecal(PlayerMob perspective) {
        super.drawDecal(perspective);
        int inventorySlot = this.getContainerSlot().getInventorySlot();
        String key = Integer.toString((inventorySlot + 1) % 10);
        FontManager.bit.drawString(this.getX() + 4, this.getY() + 4, key, new FontOptions(10).color(this.getInterfaceStyle().inactiveButtonTextColor).forceNonPixelFont());
    }

    @Override
    public Color getDrawColor() {
        if (this.player.getSelectedSlot() == this.getContainerSlot().getInventorySlot()) {
            return this.getInterfaceStyle().highlightElementColor;
        }
        if (!this.player.isInventoryExtended()) {
            return this.getInterfaceStyle().unfocusedElementColor;
        }
        return super.getDrawColor();
    }
}

