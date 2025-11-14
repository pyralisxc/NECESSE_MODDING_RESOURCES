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

public class StoneCaveling
extends CavelingMob {
    public StoneCaveling() {
        super(200, 40);
    }

    @Override
    public void init() {
        super.init();
        this.texture = MobRegistry.Textures.stoneCaveling;
        this.popParticleColor = new Color(105, 105, 105);
        this.singleRockSmallStringID = "caverocksmall";
        if (this.item == null) {
            this.item = GameRandom.globalRandom.getChance(0.02f) ? new InventoryItem("cavelingsfoot") : new InventoryItem("goldore", GameRandom.globalRandom.getIntBetween(12, 24));
        }
    }

    @Override
    public LootTable getLootTable() {
        return super.getLootTable();
    }

    @Override
    public LootTable getCavelingDropsAsLootTable() {
        return new LootTable(new LootItem("goldore", 1));
    }
}

