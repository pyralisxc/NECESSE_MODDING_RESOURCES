/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Rectangle;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormCustomDraw;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.forms.position.FormRelativePosition;
import necesse.gfx.forms.presets.containerComponent.object.CraftingStationContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.container.Container;
import necesse.inventory.container.object.FueledCraftingStationContainer;

public class FueledCraftingStationContainerForm<T extends FueledCraftingStationContainer>
extends CraftingStationContainerForm<T> {
    protected Form fuelForm;
    private FormLocalCheckBox keepRunningCheckbox;

    public FueledCraftingStationContainerForm(Client client, T container) {
        super(client, container);
        int columns = 2;
        int inventorySize = ((FueledCraftingStationContainer)container).INVENTORY_END - ((FueledCraftingStationContainer)container).INVENTORY_START + 1;
        int rows = (inventorySize + columns - 1) / columns;
        this.fuelForm = new Form(120, rows * 40 + 60);
        if (!((FueledCraftingStationContainer)container).objectEntity.alwaysOn) {
            this.keepRunningCheckbox = this.fuelForm.addComponent(new FormLocalCheckBox("ui", "fuelkeeprunning", 5, this.fuelForm.getHeight() - 10, ((FueledCraftingStationContainer)container).objectEntity.keepRunning, this.fuelForm.getWidth() - 10));
            this.keepRunningCheckbox.onClicked(e -> container.setKeepRunning.runAndSend(((FormCheckBox)e.from).checked));
            Rectangle boundingBox = this.keepRunningCheckbox.getBoundingBox();
            this.keepRunningCheckbox.setPosition(this.fuelForm.getWidth() / 2 - boundingBox.width / 2, this.keepRunningCheckbox.getY());
            this.fuelForm.setHeight(this.fuelForm.getHeight() + boundingBox.height);
        }
        this.fuelForm.setPosition(new FormRelativePosition((FormPositionContainer)this.craftingForm, -this.fuelForm.getWidth() - this.getInterfaceStyle().formSpacing, 0));
        this.fuelForm.addComponent(new FormLocalLabel("ui", "fuel", new FontOptions(16), 0, this.fuelForm.getWidth() / 2, 5));
        this.fuelForm.addComponent(new FormCustomDraw(this.fuelForm.getWidth() / 2 - 40, 26, 80, this.getInterfaceStyle().progressbar_small_empty.getHeight(), (FueledCraftingStationContainer)container){
            final /* synthetic */ FueledCraftingStationContainer val$container;
            {
                this.val$container = fueledCraftingStationContainer;
                super(x, y, width, height);
            }

            @Override
            public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                FormComponent.drawWidthComponent(new GameSprite(this.getInterfaceStyle().progressbar_small_empty, 0, 0, this.getInterfaceStyle().progressbar_small_empty.getHeight()), new GameSprite(this.getInterfaceStyle().progressbar_small_empty, 1, 0, this.getInterfaceStyle().progressbar_small_empty.getHeight()), this.getX(), this.getY(), this.width);
                float progress = 0.0f;
                if (this.val$container.objectEntity.isFueled()) {
                    progress = this.val$container.objectEntity.getFuelProgressLeft();
                    int progressWidth = (int)(progress * (float)this.width);
                    FormComponent.drawWidthComponent(new GameSprite(this.getInterfaceStyle().progressbar_small_full, 0, 0, this.getInterfaceStyle().progressbar_small_full.getHeight()), new GameSprite(this.getInterfaceStyle().progressbar_small_full, 1, 0, this.getInterfaceStyle().progressbar_small_full.getHeight()), this.getX(), this.getY(), progressWidth);
                }
                if (this.isHovering()) {
                    GameTooltipManager.addTooltip(new StringTooltips((int)(progress * 100.0f) + "%"), TooltipLocation.FORM_FOCUS);
                }
            }
        });
        for (int i = ((FueledCraftingStationContainer)container).INVENTORY_START; i <= ((FueledCraftingStationContainer)container).INVENTORY_END; ++i) {
            int index = i - ((FueledCraftingStationContainer)container).INVENTORY_START;
            int column = index % columns;
            int row = index / columns;
            int slotsInRow = Math.min(inventorySize - row * columns, columns);
            int xOffset = slotsInRow * 20;
            this.fuelForm.addComponent(new FormContainerSlot(client, (Container)container, i, this.fuelForm.getWidth() / 2 + column * 40 - xOffset, row * 40 + 40)).setDecal(this.getInterfaceStyle().inventoryslot_icon_fuel);
        }
    }

    @Override
    protected void init() {
        super.init();
        this.getManager().addComponent(this.fuelForm);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.fuelForm.setHidden(!this.isCurrent(this.craftingForm));
        if (this.keepRunningCheckbox != null && this.keepRunningCheckbox.checked != ((FueledCraftingStationContainer)this.container).objectEntity.keepRunning) {
            this.keepRunningCheckbox.checked = ((FueledCraftingStationContainer)this.container).objectEntity.keepRunning;
        }
        super.draw(tickManager, perspective, renderBox);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (this.fuelForm != null) {
            this.getManager().removeComponent(this.fuelForm);
            this.fuelForm = null;
        }
    }
}

