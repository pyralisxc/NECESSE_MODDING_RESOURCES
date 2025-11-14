/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.lootItem;

import java.util.function.BiFunction;
import java.util.function.Function;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ConditionLootItemList;

public class MobConditionLootItemList
extends ConditionLootItemList {
    public MobConditionLootItemList(BiFunction<GameRandom, Mob, Boolean> condition, LootItemInterface ... items) {
        super((GameRandom random, Object[] objects) -> {
            Mob mob = LootTable.expectExtra(Mob.class, objects, 0);
            if (mob != null) {
                return (Boolean)condition.apply((GameRandom)random, mob);
            }
            return false;
        }, items);
    }

    public MobConditionLootItemList(Function<Mob, Boolean> condition, LootItemInterface ... items) {
        super((GameRandom random, Object[] objects) -> {
            Mob mob = LootTable.expectExtra(Mob.class, objects, 0);
            if (mob != null) {
                return (Boolean)condition.apply(mob);
            }
            return false;
        }, items);
    }
}

