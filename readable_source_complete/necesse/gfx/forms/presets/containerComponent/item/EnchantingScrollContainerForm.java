/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.item;

import java.awt.Rectangle;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.client.Client;
import necesse.engine.registries.ItemRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.containerSlot.FormContainerEnchantSlot;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.Container;
import necesse.inventory.container.item.EnchantingScrollContainer;

public class EnchantingScrollContainerForm<T extends EnchantingScrollContainer>
extends ContainerForm<T> {
    public FormLocalTextButton enchantButton;

    public EnchantingScrollContainerForm(Client client, T container) {
        super(client, 200, 300, container);
        FormFlow flow = new FormFlow(10);
        this.addComponent(flow.nextY(new FormLocalLabel(ItemRegistry.getLocalization(ItemRegistry.getItemID("enchantingscroll")), new FontOptions(20), 0, this.getWidth() / 2, 10, this.getWidth() - 20), 10));
        if (((EnchantingScrollContainer)container).enchantment != null && ((EnchantingScrollContainer)container).scrollType != null) {
            this.addComponent(flow.nextY(new FormLocalLabel(((EnchantingScrollContainer)container).scrollType.enchantTip.apply(((EnchantingScrollContainer)container).enchantment), new FontOptions(16), 0, this.getWidth() / 2, 10, this.getWidth() - 20), 10));
        }
        this.addComponent(flow.nextY(new FormContainerEnchantSlot(client, (Container)container, ((EnchantingScrollContainer)container).ENCHANT_SLOT, this.getWidth() / 2 - 20, 10), 10));
        int enchantButtonWidth = Math.min(150, this.getWidth() - 20);
        this.enchantButton = this.addComponent(flow.nextY(new FormLocalTextButton("ui", "mageconfirm", this.getWidth() / 2 - enchantButtonWidth / 2, 60, enchantButtonWidth, FormInputSize.SIZE_24, ButtonColor.BASE), 10));
        this.enchantButton.onClicked(e -> container.enchantButton.runAndSend());
        this.setHeight(flow.next());
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.enchantButton.setActive(((EnchantingScrollContainer)this.container).canEnchant());
        super.draw(tickManager, perspective, renderBox);
    }
}

