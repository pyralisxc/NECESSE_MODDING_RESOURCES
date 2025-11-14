/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Rectangle;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.client.Client;
import necesse.engine.window.GameWindow;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.forms.position.FormRelativePosition;
import necesse.gfx.forms.presets.containerComponent.ContainerFormList;
import necesse.gfx.forms.presets.containerComponent.object.FuelContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.OEInventoryContainerForm;
import necesse.inventory.container.Container;
import necesse.inventory.container.object.FueledRefrigeratorInventoryContainer;

public class FueledRefrigeratorInventoryContainerForm<T extends FueledRefrigeratorInventoryContainer>
extends ContainerFormList<T> {
    protected OEInventoryContainerForm<T> inventoryContainerForm;
    protected FuelContainerForm fuelForm;

    public FueledRefrigeratorInventoryContainerForm(Client client, T container) {
        super(client, container);
        this.inventoryContainerForm = this.addComponent(new OEInventoryContainerForm<T>(client, container));
        this.fuelForm = this.addComponent(new FuelContainerForm(client, (Container)container, ((FueledRefrigeratorInventoryContainer)container).FUEL_START, ((FueledRefrigeratorInventoryContainer)container).FUEL_END, this.getInterfaceStyle().inventoryslot_icon_cooling_box_fuel, true, null, () -> true, ((FueledRefrigeratorInventoryContainer)container).refrigeratorObjectEntity::getFuelProgress));
        this.fuelForm.setPosition(new FormRelativePosition((FormPositionContainer)this.inventoryContainerForm.inventoryForm, -this.fuelForm.getWidth() - this.getInterfaceStyle().formSpacing, -Math.max(0, this.fuelForm.getHeight() - this.inventoryContainerForm.inventoryForm.getHeight())));
    }

    @Override
    public boolean shouldOpenInventory() {
        return true;
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.inventoryContainerForm.onWindowResized(window);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.fuelForm.setHidden(!this.inventoryContainerForm.isCurrent(this.inventoryContainerForm.inventoryForm));
        super.draw(tickManager, perspective, renderBox);
    }
}

