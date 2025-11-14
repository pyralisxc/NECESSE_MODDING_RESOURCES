/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Rectangle;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.client.Client;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormCustomDraw;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.forms.position.FormRelativePosition;
import necesse.gfx.forms.presets.containerComponent.ContainerFormList;
import necesse.gfx.forms.presets.containerComponent.object.FuelContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.OEInventoryContainerForm;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.container.Container;
import necesse.inventory.container.object.FueledIncineratorInventoryContainer;

public class FueledIncineratorInventoryContainerForm<T extends FueledIncineratorInventoryContainer>
extends ContainerFormList<T> {
    protected OEInventoryContainerForm<T> inventoryContainerForm;
    protected FuelContainerForm fuelForm;

    public FueledIncineratorInventoryContainerForm(Client client, T container) {
        super(client, container);
        this.inventoryContainerForm = this.addComponent(new OEInventoryContainerForm<T>(client, (FueledIncineratorInventoryContainer)container){

            @Override
            protected void addSlots(FormFlow flow) {
                super.addSlots(flow);
                flow.next(8);
                this.inventoryForm.addComponent(flow.nextY(new FormCustomDraw(6, 0, this.inventoryForm.getWidth() - 12, this.getInterfaceStyle().progressbar_small_empty.getHeight()){

                    @Override
                    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                        FormComponent.drawWidthComponent(new GameSprite(this.getInterfaceStyle().progressbar_small_empty, 0, 0, this.getInterfaceStyle().progressbar_small_empty.getHeight()), new GameSprite(this.getInterfaceStyle().progressbar_small_empty, 1, 0, this.getInterfaceStyle().progressbar_small_empty.getHeight()), this.getX(), this.getY(), this.width);
                        float progress = ((FueledIncineratorInventoryContainer)(this).container).incineratorObjectEntity.getProcessingProgress();
                        int progressWidth = (int)(progress * (float)this.width);
                        FormComponent.drawWidthComponent(new GameSprite(this.getInterfaceStyle().progressbar_small_full, 0, 0, this.getInterfaceStyle().progressbar_small_full.getHeight()), new GameSprite(this.getInterfaceStyle().progressbar_small_full, 1, 0, this.getInterfaceStyle().progressbar_small_full.getHeight()), this.getX(), this.getY(), progressWidth);
                        if (this.isHovering()) {
                            GameTooltipManager.addTooltip(new StringTooltips((int)(progress * 100.0f) + "%"), TooltipLocation.FORM_FOCUS);
                        }
                    }
                }, 8));
            }
        });
        this.fuelForm = this.addComponent(new FuelContainerForm(client, (Container)container, ((FueledIncineratorInventoryContainer)container).FUEL_START, ((FueledIncineratorInventoryContainer)container).FUEL_END, this.getInterfaceStyle().inventoryslot_icon_fuel, true, null, () -> true, ((FueledIncineratorInventoryContainer)container).incineratorObjectEntity::getFuelProgress));
        this.fuelForm.setPosition(new FormRelativePosition((FormPositionContainer)this.inventoryContainerForm.inventoryForm, -this.fuelForm.getWidth() - this.getInterfaceStyle().formSpacing, -Math.max(0, this.fuelForm.getHeight() - this.inventoryContainerForm.inventoryForm.getHeight())));
    }

    @Override
    public boolean shouldOpenInventory() {
        return true;
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.inventoryContainerForm.onWindowResized(WindowManager.getWindow());
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.fuelForm.setHidden(!this.inventoryContainerForm.isCurrent(this.inventoryContainerForm.inventoryForm));
        super.draw(tickManager, perspective, renderBox);
    }
}

