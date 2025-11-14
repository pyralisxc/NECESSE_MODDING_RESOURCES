/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.incursionModifiers;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import necesse.engine.incursionPerkTree.ChanceToMineFullClusterPerk;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameRandom;
import necesse.engine.util.PointHashSet;
import necesse.entity.ObjectDamageResult;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.manager.ObjectDestroyedListenerEntityComponent;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.RockOreObject;
import necesse.level.gameObject.SingleOreRockSmall;
import necesse.level.maps.Level;

public class ChanceToMineFullClusterLevelEvent
extends LevelEvent
implements ObjectDestroyedListenerEntityComponent {
    public ChanceToMineFullClusterLevelEvent() {
        super(true);
        this.shouldSave = true;
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    @Override
    public void onObjectDestroyed(GameObject object, int layerID, int tileX, int tileY, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        if (client != null && this.isValidObject(object, tileX, tileY) && GameRandom.globalRandom.getChance(ChanceToMineFullClusterPerk.CLEAR_CHANCE_ON_ORE_DESTROYED)) {
            LinkedList<Point> openTiles = new LinkedList<Point>();
            PointHashSet tilesCleared = new PointHashSet();
            openTiles.add(new Point(tileX, tileY));
            tilesCleared.add(tileX, tileY);
            while (!openTiles.isEmpty()) {
                Point point = (Point)openTiles.removeFirst();
                for (Point offset : Level.adjacentGettersNotDiagonal) {
                    int nextTileX = point.x + offset.x;
                    int nextTileY = point.y + offset.y;
                    if (tilesCleared.contains(nextTileX, nextTileY)) continue;
                    this.handleTile(openTiles, tilesCleared, nextTileX, nextTileY);
                }
            }
        }
    }

    protected void handleTile(LinkedList<Point> openTiles, PointHashSet tilesCleared, int tileX, int tileY) {
        GameObject object = this.getLevel().getObject(tileX, tileY);
        if (this.isValidObject(object, tileX, tileY)) {
            ObjectDamageResult result = this.getLevel().entityManager.doObjectDamageOverride(0, tileX, tileY, object.objectHealth);
            tilesCleared.add(tileX, tileY);
            if (result.destroyed) {
                openTiles.add(new Point(tileX, tileY));
            }
        }
    }

    protected boolean isValidObject(GameObject object, int tileX, int tileY) {
        if (object.isOre && !this.getLevel().objectLayer.isPlayerPlaced(tileX, tileY) && !this.level.isProtected(tileX, tileY)) {
            if (object instanceof RockOreObject) {
                String droppedOre = ((RockOreObject)object).droppedOre;
                return droppedOre.equals("alchemyshard") || droppedOre.equals("upgradeshard");
            }
            if (object instanceof SingleOreRockSmall) {
                String droppedOre = ((SingleOreRockSmall)object).droppedOre;
                return droppedOre.equals("alchemyshard") || droppedOre.equals("upgradeshard");
            }
        }
        return false;
    }
}

