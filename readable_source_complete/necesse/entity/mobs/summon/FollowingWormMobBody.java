/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.WormMobBody;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.FollowingWormMobHead;
import necesse.gfx.camera.GameCamera;

public class FollowingWormMobBody<T extends FollowingWormMobHead<B, T>, B extends FollowingWormMobBody<T, B>>
extends WormMobBody<T, B> {
    public FollowingWormMobBody(int health) {
        super(health);
        this.isHostile = false;
        this.shouldSave = false;
        this.canDespawn = false;
        this.isStatic = true;
    }

    public void setMasterFollowingConfig(int followingUniqueID) {
        this.followingUniqueID = followingUniqueID;
        this.calculateFoundFollowingMob();
    }

    protected GameMessage getSummonLocalization() {
        return super.getLocalization();
    }

    @Override
    public GameMessage getLocalization() {
        if (this.getLevel() == null) {
            return super.getLocalization();
        }
        Mob followingMob = this.getFollowingMob();
        if (followingMob != null) {
            return new LocalMessage("mob", "spawnedname", "player", followingMob.getDisplayName(), "mob", this.getSummonLocalization());
        }
        return super.getLocalization();
    }

    @Override
    public boolean canTarget(Mob target) {
        ItemAttackerMob followingAttacker = this.getFollowingItemAttacker();
        if (followingAttacker != null && !followingAttacker.isHostile) {
            return target.isHostile || target.getUniqueID() == followingAttacker.getSummonFocusUniqueID();
        }
        return super.canTarget(target);
    }

    @Override
    public boolean canPushMob(Mob other) {
        return other.isFollowing() && !other.isMounted();
    }

    @Override
    public Mob getFirstAttackOwner() {
        Mob followingMob = this.getFollowingMob();
        if (followingMob != null) {
            return followingMob;
        }
        return super.getFirstAttackOwner();
    }

    @Override
    public boolean onMouseHover(GameCamera camera, PlayerMob perspective, boolean debug) {
        if (!debug) {
            return false;
        }
        return super.onMouseHover(camera, perspective, debug);
    }
}

