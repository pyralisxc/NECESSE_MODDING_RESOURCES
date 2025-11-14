/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.annotations.ModMethodPatch
 *  necesse.engine.util.GameRandom
 *  necesse.inventory.lootTable.LootItemInterface
 *  necesse.inventory.lootTable.LootTable
 *  necesse.inventory.lootTable.lootItem.LootItem
 *  necesse.level.gameObject.SurfaceGrassObject
 *  necesse.level.maps.Level
 *  net.bytebuddy.asm.Advice$Argument
 *  net.bytebuddy.asm.Advice$OnMethodExit
 *  net.bytebuddy.asm.Advice$Return
 *  net.bytebuddy.asm.Advice$This
 */
package aphorea.patches;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.SurfaceGrassObject;
import necesse.level.maps.Level;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target=SurfaceGrassObject.class, name="getLootTable", arguments={Level.class, int.class, int.class, int.class})
public class GrassLoot {
    @Advice.OnMethodExit
    static void onExit(@Advice.This SurfaceGrassObject grassObject, @Advice.Argument(value=0) Level level, @Advice.Return(readOnly=false) LootTable lootTable) {
        if (GameRandom.globalRandom.getChance(0.002f + (level.getWorldEntity().getDay() < 2 ? 0.003f : 0.0f))) {
            lootTable = new LootTable(new LootItemInterface[]{new LootItem("floralring")});
        }
    }
}

