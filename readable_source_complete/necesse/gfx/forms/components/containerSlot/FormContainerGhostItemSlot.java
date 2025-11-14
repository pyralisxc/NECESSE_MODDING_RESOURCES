/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.containerSlot;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Input;
import necesse.engine.network.client.Client;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.inventory.InventoryItem;

public class FormContainerGhostItemSlot
extends FormContainerSlot {
    public InventoryItem ghostItem;

    public FormContainerGhostItemSlot(Client client, int containerSlotIndex, int x, int y) {
        super(client, containerSlotIndex, x, y);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        super.draw(tickManager, perspective, renderBox);
        InventoryItem item = this.getContainerSlot().getItem();
        if (item == null) {
            if (this.ghostItem != null) {
                Input input;
                this.ghostItem.drawIcon(perspective, this.getX() + 4, this.getY() + 4, 32, new Color(255, 255, 255, 125));
                if (this.isHovering() && !(input = WindowManager.getWindow().getInput()).isKeyDown(-100) && !input.isKeyDown(-99)) {
                    this.addItemTooltips(this.ghostItem, perspective);
                }
            }
        } else {
            this.ghostItem = null;
        }
    }
}

