/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.trialRoomPresets;

import java.util.function.Supplier;
import necesse.engine.GameLog;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.CoinPileObject;
import necesse.level.maps.Level;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.TrialRoomSet;

public class TrialRoomPreset
extends Preset {
    public int exitTileX;
    public int exitTileY;

    public TrialRoomPreset(int exitTileX, int exitTileY) {
        super(50, 50);
        this.clearOtherWires = true;
        this.exitTileX = exitTileX;
        this.exitTileY = exitTileY;
    }

    public void replaceSet(TrialRoomSet trialRoomSet) {
        this.replaceSet(trialRoomSet, this);
    }

    public void replaceSet(TrialRoomSet trialRoomSet, Preset preset) {
        TrialRoomSet defaultSet = TrialRoomSet.stone;
        TrialRoomSet set = trialRoomSet == null ? TrialRoomSet.stone : trialRoomSet;
        defaultSet.replaceWith(set, preset);
    }

    public void addCoinPilesToPreset(GameRandom random) {
        CoinPileObject coinObject = (CoinPileObject)ObjectRegistry.getObject("coin");
        this.addCustomApplyAreaEach(0, 0, this.width, this.height, 0, (level, levelX, levelY, dir, blackboard) -> {
            if (level.getObjectID(levelX, levelY) == coinObject.getID()) {
                int coinAmount = GameRandom.getIntBetween(random, 50, 100);
                coinObject.setCoins(coinAmount, level, levelX, levelY);
            }
            return null;
        });
    }

    public void addLoot(Iterable<InventoryItem> items, Level level, int levelX, int levelY) {
        if (items == null) {
            GameLog.warn.println("Could not find loot items for " + level.getIdentifier() + " at tile " + levelX + "x" + levelY);
            return;
        }
        ObjectEntity objEnt = level.entityManager.getObjectEntity(levelX, levelY);
        if (objEnt != null && objEnt.implementsOEInventory()) {
            for (InventoryItem item : items) {
                ((OEInventory)((Object)objEnt)).getInventory().addItem(null, null, item, "addloot", null);
            }
        }
    }

    public void addLoot(Supplier<? extends Iterable<InventoryItem>> lootGenerator, int tileX, int tileY) {
        this.addCustomApply(tileX, tileY, 0, (level, levelX, levelY, dir, blackboard) -> {
            Iterable items = (Iterable)lootGenerator.get();
            this.addLoot(items, level, levelX, levelY);
            return null;
        });
    }
}

