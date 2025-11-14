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

public class SwampStoneCaveling
extends CavelingMob {
    public SwampStoneCaveling() {
        super(300, 45);
    }

    @Override
    public void init() {
        super.init();
        this.texture = MobRegistry.Textures.swampStoneCaveling;
        this.popParticleColor = new Color(50, 62, 48);
        this.singleRockSmallStringID = "swampcaverocksmall";
        if (this.item == null) {
            this.item = GameRandom.globalRandom.getChance(0.02f) ? new InventoryItem("cavelingsfoot") : new InventoryItem("ivyore", GameRandom.globalRandom.getIntBetween(12, 24));
        }
    }

    @Override
    public LootTable getLootTable() {
        return super.getLootTable();
    }

    @Override
    public LootTable getCavelingDropsAsLootTable() {
        return new LootTable(new LootItem("ivyore", 1));
    }
}

