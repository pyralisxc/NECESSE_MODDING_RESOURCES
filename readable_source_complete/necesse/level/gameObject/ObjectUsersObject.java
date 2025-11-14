/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ObjectUserMob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.inventory.PlayerInventorySlot;
import necesse.level.maps.Level;

public interface ObjectUsersObject {
    default public OEUsers getOEUsersObject(Level level, int tileX, int tileY) {
        ObjectEntity oe = level.entityManager.getObjectEntity(tileX, tileY);
        if (oe instanceof OEUsers) {
            return (OEUsers)((Object)oe);
        }
        return null;
    }

    default public Stream<ObjectUserMob> streamObjectUsers(Level level, int tileX, int tileY) {
        OEUsers oeUsers = this.getOEUsersObject(level, tileX, tileY);
        if (oeUsers != null) {
            return oeUsers.streamUsers(level).filter(m -> m instanceof ObjectUserMob).map(m -> (ObjectUserMob)((Object)m));
        }
        return Stream.empty();
    }

    default public List<ObjectUserMob> getObjectUsers(Level level, int tileX, int tileY) {
        return this.streamObjectUsers(level, tileX, tileY).collect(Collectors.toList());
    }

    default public boolean accelerationCancelsUser(Level level, int tileX, int tileY, Mob mob) {
        return true;
    }

    default public boolean canUserAttack(Level level, int tileX, int tileY, PlayerInventorySlot slot, PlayerMob player) {
        return true;
    }

    default public boolean canUserInteract(Level level, int tileX, int tileY, PlayerInventorySlot slot, PlayerMob player) {
        return true;
    }

    default public int getForcedUserDir(Level level, int tileX, int tileY) {
        return -1;
    }

    default public void tickUser(Level level, int tileX, int tileY, Mob mob) {
    }

    public void stopUsing(Level var1, int var2, int var3, Mob var4, boolean var5, float var6, float var7);

    public void updateUserToExitPos(Level var1, int var2, int var3, Mob var4, float var5, float var6);

    public boolean drawsUser(Level var1, int var2, int var3, Mob var4);

    public boolean preventsUserPushed(Level var1, int var2, int var3, Mob var4);

    public boolean preventsUserLevelInteract(Level var1, int var2, int var3, Mob var4);

    public boolean userCanBeTargetedFromAdjacentTiles(Level var1, int var2, int var3, Mob var4);

    public Rectangle getUserCollisionBox(Level var1, int var2, int var3, Mob var4, Rectangle var5);

    public Rectangle getUserHitBox(Level var1, int var2, int var3, Mob var4, Rectangle var5);

    public Rectangle getUserSelectBox(Level var1, int var2, int var3, Mob var4);

    public Point getUserAppearancePos(Level var1, int var2, int var3, Mob var4);

    default public boolean isValidUser(Level level, int tileX, int tileY, Mob mob) {
        return true;
    }
}

