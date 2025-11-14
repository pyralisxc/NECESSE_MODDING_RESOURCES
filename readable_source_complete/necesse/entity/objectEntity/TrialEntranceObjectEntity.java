/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.util.ArrayList;
import java.util.List;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.objectEntity.PortalObjectEntity;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.desert.DesertBiome;
import necesse.level.maps.biomes.desert.DesertDeepCaveLevel;
import necesse.level.maps.biomes.forest.ForestDeepCaveLevel;
import necesse.level.maps.biomes.plains.PlainsBiome;
import necesse.level.maps.biomes.plains.PlainsDeepCaveLevel;
import necesse.level.maps.biomes.snow.SnowBiome;
import necesse.level.maps.biomes.snow.SnowDeepCaveLevel;
import necesse.level.maps.biomes.swamp.SwampBiome;
import necesse.level.maps.biomes.swamp.SwampDeepCaveLevel;
import necesse.level.maps.biomes.trial.TrialRoomLevel;
import necesse.level.maps.presets.set.TrialRoomSet;
import necesse.level.maps.presets.trialRoomPresets.GenericTrialRoom;
import necesse.level.maps.presets.trialRoomPresets.PressurePlateMazePreset;
import necesse.level.maps.presets.trialRoomPresets.TrialRoomPreset;

public class TrialEntranceObjectEntity
extends PortalObjectEntity {
    public ArrayList<List<InventoryItem>> lootList = new ArrayList();

    public TrialEntranceObjectEntity(Level level, int x, int y) {
        super(level, "trialentrance", x, y, level.getIdentifier(), x, y);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.lootList.clear();
        LoadData lootSave = save.getFirstLoadDataByName("LOOT");
        if (lootSave != null) {
            List<LoadData> inventorySaves = lootSave.getLoadDataByName("INVENTORY");
            for (LoadData inventorySave : inventorySaves) {
                ArrayList<InventoryItem> items = new ArrayList<InventoryItem>();
                List<LoadData> itemSaves = inventorySave.getLoadDataByName("ITEM");
                for (LoadData itemSave : itemSaves) {
                    InventoryItem item = InventoryItem.fromLoadData(itemSave);
                    if (item == null) continue;
                    items.add(item);
                }
                if (items.isEmpty()) continue;
                this.lootList.add(items);
            }
        }
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        if (!this.lootList.isEmpty()) {
            SaveData lootSave = new SaveData("LOOT");
            for (List<InventoryItem> list : this.lootList) {
                if (list.isEmpty()) continue;
                SaveData inventorySave = new SaveData("INVENTORY");
                for (InventoryItem item : list) {
                    SaveData itemSave = new SaveData("ITEM");
                    item.addSaveData(itemSave);
                    inventorySave.addSaveData(itemSave);
                }
                lootSave.addSaveData(inventorySave);
            }
            if (!lootSave.isEmpty()) {
                save.addSaveData(lootSave);
            }
        }
    }

    public void addLootList(List<InventoryItem> items) {
        this.lootList.add(items);
    }

    public List<InventoryItem> getNextLootList() {
        if (this.lootList.isEmpty()) {
            return null;
        }
        return this.lootList.remove(0);
    }

    @Override
    public void use(Server server, ServerClient client) {
        this.destinationIdentifier = new LevelIdentifier(this.getLevel().getIdentifier().stringID + "trial" + this.tileX + "x" + this.tileY);
        this.teleportClientToAroundDestination(client, identifier -> {
            if (identifier.equals(this.destinationIdentifier)) {
                this.getLevel().childLevels.add(this.destinationIdentifier);
                return this.generateTrialLevel(this.getLevel(), this.tileX, this.tileY, this.destinationIdentifier, server);
            }
            return null;
        }, level -> {
            client.setFallbackLevel(this.getLevel(), this.tileX, this.tileY);
            GameObject exit = ObjectRegistry.getObject(ObjectRegistry.getObjectID("trialexit"));
            if (exit != null) {
                exit.placeObject((Level)level, this.destinationTileX, this.destinationTileY, 0, false);
                PortalObjectEntity exitEntity = (PortalObjectEntity)level.entityManager.getObjectEntity(this.destinationTileX, this.destinationTileY);
                if (exitEntity != null) {
                    exitEntity.destinationTileX = this.tileX;
                    exitEntity.destinationTileY = this.tileY;
                    exitEntity.destinationIdentifier = this.getLevel().getIdentifier();
                }
            }
            this.runClearMobs((Level)level, this.destinationTileX, this.destinationTileY);
            return true;
        }, true);
    }

    public Level generateTrialLevel(Level parentLevel, int parentTileX, int parentTileY, LevelIdentifier trialRoomIdentifier, Server server) {
        Biome levelBiome = parentLevel.getBiome(this.tileX, this.tileY);
        TrialRoomSet trialRoomSet = levelBiome instanceof DesertBiome ? (parentLevel instanceof DesertDeepCaveLevel ? TrialRoomSet.deepSandstone : TrialRoomSet.sandStone) : (levelBiome instanceof SnowBiome ? (parentLevel instanceof SnowDeepCaveLevel ? TrialRoomSet.deepSnowStone : TrialRoomSet.snowStone) : (levelBiome instanceof PlainsBiome ? (parentLevel instanceof PlainsDeepCaveLevel ? TrialRoomSet.basalt : TrialRoomSet.granite) : (levelBiome instanceof SwampBiome ? (parentLevel instanceof SwampDeepCaveLevel ? TrialRoomSet.deepSwampStone : TrialRoomSet.swampStone) : (parentLevel instanceof ForestDeepCaveLevel ? TrialRoomSet.deepStone : TrialRoomSet.stone))));
        TrialRoomLevel trialLevel = new TrialRoomLevel(trialRoomIdentifier, server.world.worldEntity);
        trialLevel.setFallbackLevel(parentLevel, parentTileX, parentTileY);
        GameRandom random = new GameRandom(trialRoomIdentifier.stringID.hashCode());
        TrialRoomPreset[] presets = new TrialRoomPreset[]{new PressurePlateMazePreset(random, trialRoomSet, this::getNextLootList), new GenericTrialRoom(random, trialRoomSet, this::getNextLootList)};
        TrialRoomPreset preset = presets[random.getIntBetween(0, presets.length - 1)];
        preset.applyToLevel(trialLevel, 0, 0);
        this.destinationTileX = preset.exitTileX;
        this.destinationTileY = preset.exitTileY;
        return trialLevel;
    }
}

