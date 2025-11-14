/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.containerSlot;

import necesse.engine.network.client.Client;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.containerSlot.FormContainerArmorSlot;
import necesse.gfx.forms.presets.containerComponent.mob.EquipmentForm;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.container.Container;
import necesse.inventory.item.armorItem.ArmorItem;

public class FormContainerSettlerArmorSlot
extends FormContainerArmorSlot {
    private EquipmentForm equipmentForm;

    public FormContainerSettlerArmorSlot(Client client, Container container, int containerSlotIndex, int x, int y, ArmorItem.ArmorType armorType, boolean isCosmetic, EquipmentForm equipmentForm) {
        super(client, containerSlotIndex, x, y, armorType, isCosmetic);
        this.equipmentForm = equipmentForm;
    }

    @Override
    public Mob getEquippedMob(PlayerMob perspective) {
        return this.equipmentForm.getMob();
    }

    @Override
    public ListGameTooltips getSetBonusTooltips(GameBlackboard blackboard) {
        return this.equipmentForm.getEquipmentManager().getSetBonusBuffTooltip(blackboard);
    }
}

