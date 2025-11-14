/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Rectangle;
import java.util.function.Supplier;
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
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.BooleanCustomAction;

public class FuelContainerForm
extends Form {
    private FormLocalCheckBox keepRunningCheckbox;
    private Supplier<Boolean> keepRunningGetter;
    private Supplier<Float> fuelProgressGetter;

    public FuelContainerForm(Client client, Container container, int containerInventoryStart, int containerInventoryEnd, GameTexture decal, boolean alwaysOn, BooleanCustomAction setKeepRunning, Supplier<Boolean> keepRunningGetter, final Supplier<Float> fuelProgressGetter) {
        super(120, 0);
        this.keepRunningGetter = keepRunningGetter;
        this.fuelProgressGetter = fuelProgressGetter;
        int columns = 2;
        int inventorySize = containerInventoryEnd - containerInventoryStart + 1;
        int rows = (inventorySize + columns - 1) / columns;
        this.setHeight(rows * 40 + 60);
        if (!alwaysOn && setKeepRunning != null) {
            this.keepRunningCheckbox = this.addComponent(new FormLocalCheckBox("ui", "fuelkeeprunning", 5, this.getHeight() - 10, keepRunningGetter.get(), this.getWidth() - 10));
            this.keepRunningCheckbox.onClicked(e -> setKeepRunning.runAndSend(((FormCheckBox)e.from).checked));
            Rectangle boundingBox = this.keepRunningCheckbox.getBoundingBox();
            this.keepRunningCheckbox.setPosition(this.getWidth() / 2 - boundingBox.width / 2, this.keepRunningCheckbox.getY());
            this.setHeight(this.getHeight() + boundingBox.height);
        }
        this.addComponent(new FormLocalLabel("ui", "fuel", new FontOptions(16), 0, this.getWidth() / 2, 5));
        this.addComponent(new FormCustomDraw(this.getWidth() / 2 - 40, 26, 80, this.getInterfaceStyle().progressbar_small_empty.getHeight()){

            @Override
            public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                FormComponent.drawWidthComponent(new GameSprite(this.getInterfaceStyle().progressbar_small_empty, 0, 0, this.getInterfaceStyle().progressbar_small_empty.getHeight()), new GameSprite(this.getInterfaceStyle().progressbar_small_empty, 1, 0, this.getInterfaceStyle().progressbar_small_empty.getHeight()), this.getX(), this.getY(), this.width);
                float progress = ((Float)fuelProgressGetter.get()).floatValue();
                int progressWidth = (int)(progress * (float)this.width);
                FormComponent.drawWidthComponent(new GameSprite(this.getInterfaceStyle().progressbar_small_full, 0, 0, this.getInterfaceStyle().progressbar_small_full.getHeight()), new GameSprite(this.getInterfaceStyle().progressbar_small_full, 1, 0, this.getInterfaceStyle().progressbar_small_full.getHeight()), this.getX(), this.getY(), progressWidth);
                if (this.isHovering()) {
                    GameTooltipManager.addTooltip(new StringTooltips((int)(progress * 100.0f) + "%"), TooltipLocation.FORM_FOCUS);
                }
            }
        });
        for (int i = containerInventoryStart; i <= containerInventoryEnd; ++i) {
            int index = i - containerInventoryStart;
            int column = index % columns;
            int row = index / columns;
            int slotsInRow = Math.min(inventorySize - row * columns, columns);
            int xOffset = slotsInRow * 20;
            FormContainerSlot containerSlot = new FormContainerSlot(client, container, i, this.getWidth() / 2 + column * 40 - xOffset, row * 40 + 40);
            if (decal != null) {
                containerSlot.setDecal(decal);
            }
            this.addComponent(containerSlot);
        }
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (this.keepRunningCheckbox != null && this.keepRunningCheckbox.checked != this.keepRunningGetter.get()) {
            this.keepRunningCheckbox.checked = this.keepRunningGetter.get();
        }
        super.draw(tickManager, perspective, renderBox);
    }
}

