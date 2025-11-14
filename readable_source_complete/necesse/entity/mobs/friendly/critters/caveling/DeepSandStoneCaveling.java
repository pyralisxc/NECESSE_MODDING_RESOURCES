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

public class DeepSandStoneCaveling
extends CavelingMob {
    public DeepSandStoneCaveling() {
        super(800, 55);
    }

    @Override
    public void init() {
        super.init();
        this.texture = MobRegistry.Textures.deepSandStoneCaveling;
        this.popParticleColor = new Color(144, 117, 58);
        this.singleRockSmallStringID = "deepsandcaverocksmall";
        if (this.item == null) {
            this.item = GameRandom.globalRandom.getChance(0.02f) ? new InventoryItem("cavelingscollection") : GameRandom.globalRandom.getOneOf(new InventoryItem("ancientfossilore", GameRandom.globalRandom.getIntBetween(12, 24)), new InventoryItem("lifequartz", GameRandom.globalRandom.getIntBetween(8, 12)));
        }
    }

    @Override
    public LootTable getLootTable() {
        return super.getLootTable();
    }

    @Override
    public LootTable getCavelingDropsAsLootTable() {
        return new LootTable(new LootItem("ancientfossilore", 1), new LootItem("lifequartz", 1));
    }
}

