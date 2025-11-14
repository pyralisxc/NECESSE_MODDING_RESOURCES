/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.awt.Shape;
import java.util.Objects;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.SwitchObject;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.CustomTilePosition;
import necesse.level.maps.Level;

public class OpenedDoor {
    public final int tileX;
    public final int tileY;
    public int mobX;
    public int mobY;
    public final boolean isSwitched;

    public OpenedDoor(int tileX, int tileY, int mobX, int mobY, boolean isSwitched) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.mobX = mobX;
        this.mobY = mobY;
        this.isSwitched = isSwitched;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        OpenedDoor that = (OpenedDoor)o;
        return this.tileX == that.tileX && this.tileY == that.tileY && this.mobX == that.mobX && this.mobY == that.mobY && this.isSwitched == that.isSwitched;
    }

    public int hashCode() {
        return Objects.hash(this.tileX, this.tileY, this.mobX, this.mobY, this.isSwitched);
    }

    public boolean isValid(Level level) {
        GameObject object = level.getObject(this.tileX, this.tileY);
        return object.isDoor && object.isSwitched != this.isSwitched;
    }

    public boolean switchedDoorCollides(Level level, Shape collision, CollisionFilter collisionFilter) {
        if (collisionFilter == null) {
            return false;
        }
        GameObject object = level.getObject(this.tileX, this.tileY);
        CustomTilePosition tp = new CustomTilePosition(level, this.tileX, this.tileY, level.getTileID(this.tileX, this.tileY), level.tileLayer.isPlayerPlaced(this.tileX, this.tileY), ((SwitchObject)object).counterID, level.getObjectRotation(this.tileX, this.tileY), level.objectLayer.isPlayerPlaced(this.tileX, this.tileY));
        return collisionFilter.check(collision, tp);
    }

    public boolean entityCollidesWithSwitchedDoor(Level level) {
        return level.entityManager.mobs.streamInRegionsInTileRange(this.tileX * 32 + 16, this.tileY * 32 + 16, 4).filter(m -> !m.removed() && m.canLevelInteract()).anyMatch(m -> this.switchedDoorCollides(level, m.getCollision(), m.getLevelCollisionFilter()));
    }

    public boolean clientCollidesWithSwitchedDoor(Level level) {
        return level.entityManager.players.streamInRegionsInTileRange(this.tileX * 32 + 16, this.tileY * 32 + 16, 4).filter(p -> !p.removed() && p.canLevelInteract()).anyMatch(p -> this.switchedDoorCollides(level, p.getCollision(), p.getLevelCollisionFilter()));
    }

    public void switchDoor(Level level) {
        GameObject object = level.getObject(this.tileX, this.tileY);
        if (object.isDoor && object.isSwitched != this.isSwitched) {
            ((SwitchObject)object).onSwitched(level, this.tileX, this.tileY);
        }
    }
}

