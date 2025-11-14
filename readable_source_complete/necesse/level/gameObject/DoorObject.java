/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Rectangle;
import necesse.engine.localization.Localization;
import necesse.entity.ObjectDamageResult;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.level.gameObject.SwitchObject;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.CustomTilePosition;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.RegionType;

public class DoorObject
extends SwitchObject {
    public DoorObject(Rectangle collision, int counterID, boolean isOpen) {
        super(collision, counterID, isOpen);
        this.stackSize = 500;
        this.regionType = RegionType.DOOR;
    }

    @Override
    public void onWireUpdate(Level level, int layerID, int tileX, int tileY, int wireID, boolean active) {
        if (!level.isServer()) {
            return;
        }
        if (active) {
            if (!this.isSwitched) {
                this.onSwitched(level, tileX, tileY);
            }
        } else if (this.isSwitched) {
            this.onSwitched(level, tileX, tileY);
        }
    }

    public boolean isOpen(Level level, int tileX, int tileY, int rotation) {
        return this.isSwitched;
    }

    public boolean isForceClosed(Level level, int tileX, int tileY) {
        return false;
    }

    @Override
    public boolean pathCollidesIfOpen(Level level, int tileX, int tileY, CollisionFilter collisionFilter, Rectangle mobCollision) {
        if (collisionFilter == null) {
            return false;
        }
        CustomTilePosition tp = new CustomTilePosition(level, tileX, tileY, level.getTileID(tileX, tileY), level.tileLayer.isPlayerPlaced(tileX, tileY), this.counterID, level.getObjectRotation(tileX, tileY), level.objectLayer.isPlayerPlaced(tileX, tileY));
        return collisionFilter.check(mobCollision, tp);
    }

    @Override
    public boolean pathCollidesIfBreakDown(Level level, int tileX, int tileY, CollisionFilter collisionFilter, Rectangle mobCollision) {
        return this.pathCollidesIfOpen(level, tileX, tileY, collisionFilter, mobCollision);
    }

    @Override
    public void onPathOpened(Level level, int tileX, int tileY, Attacker attacker) {
        this.onSwitched(level, tileX, tileY);
    }

    @Override
    public boolean onPathBreakDown(Level level, int tileX, int tileY, int damage, Attacker attacker, int hitX, int hitY) {
        ObjectDamageResult result = level.entityManager.doObjectDamage(0, tileX, tileY, damage, this.toolTier, attacker, null, true, hitX, hitY);
        if (result != null && result.destroyed) {
            this.onSwitched(level, tileX, tileY);
            for (ItemPickupEntity entity : result.itemsDropped) {
                entity.remove();
            }
        }
        return result != null && result.destroyed;
    }

    @Override
    public double getBreakDownPathCost(Level level, int x, int y) {
        return 40.0;
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        if (player.isServerClient()) {
            player.getServerClient().newStats.doors_used.increment(1);
        }
        super.interact(level, x, y, player);
    }

    @Override
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", this.isSwitched ? "closetip" : "opentip");
    }
}

