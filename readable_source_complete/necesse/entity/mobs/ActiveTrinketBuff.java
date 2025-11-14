/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.util.ArrayList;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.EquipmentItemEnchant;

public class ActiveTrinketBuff {
    public final InventoryItem item;
    public final TrinketBuff[] buffs;
    public final ActiveBuff[] activeBuffs;
    public final EquipmentItemEnchant enchant;
    public final ArrayList<Integer> disables;

    public ActiveTrinketBuff(InventoryItem item, TrinketBuff[] buffs, EquipmentItemEnchant enchant, ArrayList<Integer> disables) {
        this.item = item;
        this.buffs = buffs;
        this.activeBuffs = new ActiveBuff[buffs.length];
        this.enchant = enchant;
        this.disables = disables;
    }
}

