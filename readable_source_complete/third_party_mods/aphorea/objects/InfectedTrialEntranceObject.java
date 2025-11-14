/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.server.Server
 *  necesse.engine.util.GameRandom
 *  necesse.engine.util.LevelIdentifier
 *  necesse.entity.objectEntity.ObjectEntity
 *  necesse.entity.objectEntity.TrialEntranceObjectEntity
 *  necesse.inventory.InventoryItem
 *  necesse.level.gameObject.TrialEntranceObject
 *  necesse.level.maps.Level
 *  necesse.level.maps.presets.set.TrialRoomSet
 */
package aphorea.objects;

import aphorea.levels.InfectedTrialRoomLevel;
import aphorea.presets.trial.InfectedTrialRoom;
import java.util.List;
import necesse.engine.network.server.Server;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.TrialEntranceObjectEntity;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.TrialEntranceObject;
import necesse.level.maps.Level;
import necesse.level.maps.presets.set.TrialRoomSet;

public class InfectedTrialEntranceObject
extends TrialEntranceObject {
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new InfectedTrialEntranceObjectEntity(level, x, y);
    }

    public static class InfectedTrialEntranceObjectEntity
    extends TrialEntranceObjectEntity {
        public InfectedTrialEntranceObjectEntity(Level level, int x, int y) {
            super(level, x, y);
        }

        public Level generateTrialLevel(Level parentLevel, int parentTileX, int parentTileY, LevelIdentifier trialRoomIdentifier, Server server) {
            InfectedTrialRoomLevel trialLevel = new InfectedTrialRoomLevel(trialRoomIdentifier, server.world.worldEntity);
            trialLevel.setFallbackLevel(parentLevel, parentTileX, parentTileY);
            GameRandom random = new GameRandom((long)trialRoomIdentifier.stringID.hashCode());
            InfectedTrialRoom preset = new InfectedTrialRoom(random, TrialRoomSet.deepStone, this::getNextLootList);
            preset.applyToLevel((Level)trialLevel, 0, 0);
            this.destinationTileX = preset.exitTileX;
            this.destinationTileY = preset.exitTileY;
            return trialLevel;
        }

        public List<InventoryItem> getNextLootList() {
            System.out.println(this.lootList.isEmpty());
            return super.getNextLootList();
        }
    }
}

