/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.worldStructures.farmHouse;

import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.set.CropSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;
import necesse.level.maps.presets.worldStructures.LandStructurePreset;
import necesse.level.maps.presets.worldStructures.farmHouse.CropFarm1Preset;
import necesse.level.maps.presets.worldStructures.farmHouse.CropFarm2Preset;
import necesse.level.maps.presets.worldStructures.farmHouse.CropFarm3Preset;
import necesse.level.maps.presets.worldStructures.farmHouse.CropFarm4Preset;
import necesse.level.maps.presets.worldStructures.farmHouse.CropFarm5Preset;
import necesse.level.maps.presets.worldStructures.farmHouse.FarmHouse1Preset;
import necesse.level.maps.presets.worldStructures.farmHouse.FarmHouse2Preset;
import necesse.level.maps.presets.worldStructures.farmHouse.FarmHouse3Preset;
import necesse.level.maps.presets.worldStructures.farmHouse.FarmHouse4Preset;
import necesse.level.maps.presets.worldStructures.farmHouse.FarmHouse5Preset;
import necesse.level.maps.presets.worldStructures.farmHouse.FeedingTrough1Preset;
import necesse.level.maps.presets.worldStructures.farmHouse.FeedingTrough2Preset;
import necesse.level.maps.presets.worldStructures.farmHouse.FeedingTrough3Preset;
import necesse.level.maps.presets.worldStructures.farmHouse.FeedingTrough4Preset;

public class GeneratedFarmHousePreset
extends LandStructurePreset {
    public GeneratedFarmHousePreset(GameRandom random, WallSet wallSet, FurnitureSet furnitureSet, CropSet cropSet) {
        super(15, 13);
        LootTable farmersChest = new LootTable(new LootItem(ObjectRegistry.getObjectStringID(cropSet.seedIDs[0]), random.getIntBetween(2, 8)), new LootItem(ItemRegistry.getItemStringID(cropSet.productID), random.getIntBetween(2, 8)), new LootItem("fertilizer", random.getIntBetween(5, 15)));
        Preset farmHouse = random.getOneOf(new FarmHouse1Preset(random, farmersChest), new FarmHouse2Preset(random, farmersChest), new FarmHouse3Preset(random, farmersChest), new FarmHouse4Preset(random, farmersChest, cropSet), new FarmHouse5Preset(random, farmersChest, cropSet));
        this.applyPreset(this.width - farmHouse.width - 1, 2, farmHouse);
        boolean isAbandoned = random.nextBoolean();
        boolean destroyCropFarm = random.getEveryXthChance(4);
        boolean destroyFeedTrough = random.getEveryXthChance(4);
        if (destroyCropFarm && destroyFeedTrough) {
            isAbandoned = true;
        }
        if (!destroyCropFarm) {
            Preset cropFarm = random.getOneOf(new CropFarm1Preset(random, cropSet), new CropFarm2Preset(random, cropSet), new CropFarm3Preset(random, cropSet), new CropFarm4Preset(random, cropSet), new CropFarm5Preset(random, cropSet));
            this.applyPreset(this.width - cropFarm.width - 1, farmHouse.height + 1, cropFarm);
        }
        if (!destroyFeedTrough) {
            Preset feedTrough = random.getOneOf(new FeedingTrough1Preset(random), new FeedingTrough2Preset(random), new FeedingTrough3Preset(random), new FeedingTrough4Preset(random));
            this.applyPreset(this.width - feedTrough.width - farmHouse.width, 2, feedTrough);
        }
        WallSet.wood.replaceWith(wallSet, this);
        FurnitureSet.oak.replaceWith(furnitureSet, this);
        if (isAbandoned) {
            PresetUtils.addDeterioration(this, random, 6);
        } else {
            this.setObject(5, 8, 0);
            this.addMob("farmerhuman", 5, 8, false);
        }
    }
}

