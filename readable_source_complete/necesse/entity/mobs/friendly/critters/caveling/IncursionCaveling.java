/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.critters.caveling;

import java.awt.Color;
import necesse.engine.registries.IncursionPerksRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.friendly.critters.caveling.CavelingMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.incursion.IncursionData;

public class IncursionCaveling
extends CavelingMob {
    public IncursionCaveling() {
        super(3500, 70);
    }

    @Override
    public void init() {
        super.init();
        this.texture = MobRegistry.Textures.incursionCaveling;
        this.popParticleColor = new Color(38, 42, 44);
        this.singleRockSmallStringID = "incursioncavelingcaverocks";
        this.preventLootMultiplier = false;
        if (this.item == null) {
            this.item = this.getLootDrop();
        }
    }

    @Override
    public LootTable getLootTable() {
        return super.getLootTable();
    }

    @Override
    public LootTable getCavelingDropsAsLootTable() {
        return new LootTable(new LootItem("altardust", 1), new LootItem("upgradeshard", 1), new LootItem("alchemyshard", 1));
    }

    public InventoryItem getLootDrop() {
        IncursionData incursionData;
        TicketSystemList cavelingDrops = new TicketSystemList();
        cavelingDrops.addObject(30, new InventoryItem("altardust", GameRandom.globalRandom.getIntBetween(12, 25)));
        cavelingDrops.addObject(30, new InventoryItem("upgradeshard", GameRandom.globalRandom.getIntBetween(8, 12)));
        cavelingDrops.addObject(30, new InventoryItem("alchemyshard", GameRandom.globalRandom.getIntBetween(8, 12)));
        if (this.getLevel().isIncursionLevel && (incursionData = ((IncursionLevel)this.getLevel()).incursionData) != null && incursionData.currentIncursionPerkIDs.contains(IncursionPerksRegistry.CAVELINGS_DROP_BETTER_LOOT.getID())) {
            String randomTierOneEssenceID = GameRandom.globalRandom.getOneOf("shadowessence", "cryoessence", "bioessence", "primordialessence");
            String randomTierTwoEssenceID = GameRandom.globalRandom.getOneOf("slimeessence", "bloodessence", "spideressence", "omnicrystal");
            cavelingDrops.addObject(40, new InventoryItem(randomTierOneEssenceID, GameRandom.globalRandom.getIntBetween(5, 10)));
            cavelingDrops.addObject(40, new InventoryItem(randomTierTwoEssenceID, GameRandom.globalRandom.getIntBetween(4, 8)));
        }
        return (InventoryItem)cavelingDrops.getRandomObject(GameRandom.globalRandom);
    }
}

