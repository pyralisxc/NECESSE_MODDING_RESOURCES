/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.containerSlot;

import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.item.armorItem.ArmorItem;

public class FormContainerArmorStandSlot
extends FormContainerSlot {
    public FormContainerArmorStandSlot(Client client, Container container, int containerSlotIndex, int x, int y, ArmorItem.ArmorType armorType) {
        super(client, container, containerSlotIndex, x, y);
        if (armorType == ArmorItem.ArmorType.HEAD) {
            this.setDecal(this.getInterfaceStyle().inventoryslot_icon_helmet);
        }
        if (armorType == ArmorItem.ArmorType.CHEST) {
            this.setDecal(this.getInterfaceStyle().inventoryslot_icon_chestplate);
        }
        if (armorType == ArmorItem.ArmorType.FEET) {
            this.setDecal(this.getInterfaceStyle().inventoryslot_icon_boots);
        }
    }

    @Override
    public GameTooltips getItemTooltip(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = new ListGameTooltips();
        tooltips.add(super.getItemTooltip(item, perspective));
        return tooltips;
    }
}

