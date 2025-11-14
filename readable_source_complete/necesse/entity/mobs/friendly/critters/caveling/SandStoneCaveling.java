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

public class SandStoneCaveling
extends CavelingMob {
    public SandStoneCaveling() {
        super(350, 45);
    }

    @Override
    public void init() {
        super.init();
        this.texture = MobRegistry.Textures.sandStoneCaveling;
        this.popParticleColor = new Color(215, 215, 125);
        this.singleRockSmallStringID = "sandcaverocksmall";
        if (this.item == null) {
            this.item = GameRandom.globalRandom.getChance(0.02f) ? new InventoryItem("cavelingsfoot") : new InventoryItem("quartz", GameRandom.globalRandom.getIntBetween(8, 12));
        }
    }

    @Override
    public LootTable getLootTable() {
        return super.getLootTable();
    }

    @Override
    public LootTable getCavelingDropsAsLootTable() {
        return new LootTable(new LootItem("quartz", 1));
    }
}

