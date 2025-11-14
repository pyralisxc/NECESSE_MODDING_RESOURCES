/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.awt.Point;
import java.awt.Rectangle;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.inventory.PlayerInventorySlot;
import necesse.level.gameObject.ObjectUsersObject;
import necesse.level.maps.Level;

public abstract class ObjectUserActive {
    private Mob mob;
    public final Level level;
    public final int tileX;
    public final int tileY;
    public final ObjectUsersObject object;

    public ObjectUserActive(Level level, int tileX, int tileY, ObjectUsersObject object) {
        this.level = level;
        this.tileX = tileX;
        this.tileY = tileY;
        this.object = object;
    }

    public void init(Mob mob) {
        if (this.mob != null) {
            throw new IllegalStateException("Cannot initiate ObjectUserActive twice");
        }
        this.mob = mob;
    }

    public final Mob mob() {
        return this.mob;
    }

    public void tick() {
        if (this.isValid()) {
            OEUsers oeUsers;
            this.object.tickUser(this.level, this.tileX, this.tileY, this.mob);
            if (this.level.tickManager().getTick() % 20 == 1 && (oeUsers = this.object.getOEUsersObject(this.level, this.tileX, this.tileY)) != null) {
                oeUsers.updateUserPosition(this.mob);
            }
        } else {
            this.stopUsing();
        }
    }

    public boolean accelerationCancelsUse() {
        return this.object.accelerationCancelsUser(this.level, this.tileX, this.tileY, this.mob);
    }

    public boolean canAttack(PlayerInventorySlot slot, PlayerMob player) {
        return this.object.canUserAttack(this.level, this.tileX, this.tileY, slot, player);
    }

    public boolean canInteract(PlayerInventorySlot slot, PlayerMob player) {
        return this.object.canUserInteract(this.level, this.tileX, this.tileY, slot, player);
    }

    public int getForcedUserDir() {
        return this.object.getForcedUserDir(this.level, this.tileX, this.tileY);
    }

    public abstract void keepUsing();

    public void stopUsing() {
        this.stopUsing(true, 0.0f, 0.0f);
    }

    public void stopUsing(boolean updatePosition, float exitDirX, float exitDirY) {
        this.object.stopUsing(this.level, this.tileX, this.tileY, this.mob, updatePosition, exitDirX, exitDirY);
    }

    public boolean isValid() {
        return this.object.isValidUser(this.level, this.tileX, this.tileY, this.mob);
    }

    public boolean drawsUser() {
        return this.object.drawsUser(this.level, this.tileX, this.tileY, this.mob);
    }

    public boolean preventsUserPushed() {
        return this.object.preventsUserPushed(this.level, this.tileX, this.tileY, this.mob);
    }

    public boolean preventsUserLevelInteract() {
        return this.object.preventsUserLevelInteract(this.level, this.tileX, this.tileY, this.mob);
    }

    public boolean userCanBeTargetedFromAdjacentTiles() {
        return this.object.userCanBeTargetedFromAdjacentTiles(this.level, this.tileX, this.tileY, this.mob);
    }

    public Rectangle getUserCollisionBox(Rectangle defaultCollisionBox) {
        return this.object.getUserCollisionBox(this.level, this.tileX, this.tileY, this.mob, defaultCollisionBox);
    }

    public Rectangle getUserHitBox(Rectangle defaultHitBox) {
        return this.object.getUserHitBox(this.level, this.tileX, this.tileY, this.mob, defaultHitBox);
    }

    public Rectangle getUserSelectBox() {
        return this.object.getUserSelectBox(this.level, this.tileX, this.tileY, this.mob);
    }

    public Point getUserAppearancePos() {
        return this.object.getUserAppearancePos(this.level, this.tileX, this.tileY, this.mob);
    }
}

