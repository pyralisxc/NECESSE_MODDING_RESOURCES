/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.entity.pickup.PickupEntity;
import necesse.inventory.InventoryItem;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;

public class ReturnedObjects {
    public ArrayList<InventoryItem> items = new ArrayList();
    public ArrayList<Mob> mobs = new ArrayList();
    public ArrayList<PickupEntity> pickups = new ArrayList();

    public void returnObjectsToTile(Level level, int tileX, int tileY) {
        Point destination;
        level.regionManager.ensureTileIsLoaded(tileX, tileY);
        for (InventoryItem item : this.items) {
            destination = ReturnedObjects.getTeleportDestinationAroundObject(level, null, tileX, tileY, true);
            ItemPickupEntity itemPickupEntity = item.getPickupEntity(level, destination.x, destination.y);
            level.entityManager.pickups.add(itemPickupEntity);
        }
        for (PickupEntity pickup : this.pickups) {
            pickup.setLevel(level);
            pickup.restore();
            destination = ReturnedObjects.getTeleportDestinationAroundObject(level, null, tileX, tileY, true);
            pickup.setX(destination.x);
            pickup.setY(destination.y);
            level.entityManager.pickups.add(pickup);
        }
        for (Mob mob : this.mobs) {
            mob.setLevel(level);
            mob.restore();
            destination = ReturnedObjects.getTeleportDestinationAroundObject(level, mob::getCollision, tileX, tileY, true);
            mob.setPos(destination.x, destination.y, true);
            level.entityManager.mobs.add(mob);
        }
    }

    public static Point getTeleportDestinationAroundObject(Level level, CollisionGetter collisionGetter, int tileX, int tileY, boolean allowDiagonal) {
        ArrayList<Point> prioritySpawns = new ArrayList<Point>(8);
        ArrayList<Point> fallbackSpawns = new ArrayList<Point>(8);
        level.regionManager.ensureTileIsLoaded(tileX, tileY);
        for (Point tile : level.getLevelObject(tileX, tileY).getMultiTile().getAdjacentTiles(tileX, tileY, allowDiagonal)) {
            int posX = tile.x * 32 + 16;
            int posY = tile.y * 32 + 16;
            if (collisionGetter == null || !level.collides((Shape)collisionGetter.getCollision(posY, posY), new CollisionFilter().mobCollision())) {
                prioritySpawns.add(new Point(posX, posY));
            }
            fallbackSpawns.add(new Point(posX, posY));
        }
        if (!prioritySpawns.isEmpty()) {
            return (Point)GameRandom.globalRandom.getOneOf(prioritySpawns);
        }
        return (Point)GameRandom.globalRandom.getOneOf(fallbackSpawns);
    }

    @FunctionalInterface
    public static interface CollisionGetter {
        public Rectangle getCollision(int var1, int var2);
    }
}

