/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.critters.caveling;

import java.awt.Color;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.critters.caveling.CavelingMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class DeepSnowStoneCaveling
extends CavelingMob {
    public DeepSnowStoneCaveling() {
        super(600, 50);
    }

    @Override
    public void init() {
        super.init();
        this.texture = MobRegistry.Textures.deepSnowStoneCaveling;
        this.popParticleColor = new Color(49, 56, 60);
        this.singleRockSmallStringID = "deepsnowcaverocksmall";
        if (this.item == null) {
            this.item = GameRandom.globalRandom.getChance(0.02f) ? new InventoryItem("cavelingscollection") : GameRandom.globalRandom.getOneOf(new InventoryItem("glacialore", GameRandom.globalRandom.getIntBetween(12, 24)), new InventoryItem("lifequartz", GameRandom.globalRandom.getIntBetween(8, 12)));
        }
    }

    @Override
    public LootTable getLootTable() {
        return super.getLootTable();
    }

    @Override
    public LootTable getCavelingDropsAsLootTable() {
        return new LootTable(new LootItem("glacialore", 1), new LootItem("lifequartz", 1));
    }
}

