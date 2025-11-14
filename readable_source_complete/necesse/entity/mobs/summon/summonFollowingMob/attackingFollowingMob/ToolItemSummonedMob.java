/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.itemAttacker.CheckSlotType;
import necesse.inventory.enchants.ToolItemEnchantment;
import necesse.inventory.item.Item;

public interface ToolItemSummonedMob {
    public void updateDamage(GameDamage var1);

    public void setEnchantment(ToolItemEnchantment var1);

    public void setRemoveWhenNotInInventory(Item var1, CheckSlotType var2);
}

