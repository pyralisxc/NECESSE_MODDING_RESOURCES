/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.ArmorBuff;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.EquipmentItemEnchant;

public class ActiveArmorBuff {
    public final InventoryItem item;
    public final ArmorBuff[] buffs;
    public final ActiveBuff[] activeBuffs;
    public final EquipmentItemEnchant enchant;

    public ActiveArmorBuff(InventoryItem item, ArmorBuff[] buffs, EquipmentItemEnchant enchant) {
        this.item = item;
        this.buffs = buffs;
        this.activeBuffs = new ActiveBuff[buffs.length];
        this.enchant = enchant;
    }
}

