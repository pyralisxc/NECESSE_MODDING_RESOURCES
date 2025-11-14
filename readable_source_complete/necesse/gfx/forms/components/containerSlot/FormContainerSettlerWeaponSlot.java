/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.containerSlot;

import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.presets.containerComponent.mob.EquipmentForm;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.item.ItemAttackerWeaponItem;

public class FormContainerSettlerWeaponSlot
extends FormContainerSlot {
    private final EquipmentForm equipmentForm;

    public FormContainerSettlerWeaponSlot(Client client, Container container, int containerSlotIndex, int x, int y, EquipmentForm equipmentForm) {
        super(client, container, containerSlotIndex, x, y);
        this.equipmentForm = equipmentForm;
        this.setDecal(this.getInterfaceStyle().inventoryslot_icon_weapon);
    }

    @Override
    public GameTooltips getClearTooltips() {
        return new StringTooltips(Localization.translate("itemtooltip", "weaponslot"));
    }

    @Override
    public GameTooltips getItemTooltip(InventoryItem item, PlayerMob perspective) {
        if (item.item instanceof ItemAttackerWeaponItem) {
            GameBlackboard blackboard = new GameBlackboard().set("perspective", this.equipmentForm.getMob());
            return item.item.getTooltips(item, null, blackboard);
        }
        return super.getItemTooltip(item, perspective);
    }
}

