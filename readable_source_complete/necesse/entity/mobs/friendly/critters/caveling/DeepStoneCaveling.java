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

public class DeepStoneCaveling
extends CavelingMob {
    public DeepStoneCaveling() {
        super(500, 50);
    }

    @Override
    public void init() {
        super.init();
        this.texture = MobRegistry.Textures.deepStoneCaveling;
        this.popParticleColor = new Color(38, 42, 44);
        this.singleRockSmallStringID = "deepcaverocksmall";
        if (this.item == null) {
            this.item = GameRandom.globalRandom.getChance(0.02f) ? new InventoryItem("cavelingscollection") : GameRandom.globalRandom.getOneOf(new InventoryItem("tungstenore", GameRandom.globalRandom.getIntBetween(12, 24)), new InventoryItem("lifequartz", GameRandom.globalRandom.getIntBetween(8, 12)));
        }
    }

    @Override
    public LootTable getLootTable() {
        return super.getLootTable();
    }

    @Override
    public LootTable getCavelingDropsAsLootTable() {
        return new LootTable(new LootItem("tungstenore", 1), new LootItem("lifequartz", 1));
    }
}

