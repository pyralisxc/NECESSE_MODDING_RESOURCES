/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.WormMobHead;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.itemAttacker.CheckSlotType;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.FollowingWormMobBody;
import necesse.gfx.camera.GameCamera;
import necesse.inventory.item.Item;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.regionSystem.Region;

public abstract class FollowingWormMobHead<T extends FollowingWormMobBody<B, T>, B extends FollowingWormMobHead<T, B>>
extends WormMobHead<T, B> {
    public Item removeWhenNotInInventoryItem;
    public CheckSlotType removeWhenNotInInventorySlotType;
    private int checkRemoveByMissingItemBuffer;

    public FollowingWormMobHead(int health, float waveLength, float distPerMoveSound, int totalBodyParts, float heightMultiplier, float heightOffset) {
        super(health, waveLength, distPerMoveSound, totalBodyParts, heightMultiplier, heightOffset);
        this.isHostile = false;
        this.shouldSave = false;
        this.canDespawn = false;
        this.isStatic = true;
    }

    @Override
    protected void modifyBodyPart(int index, T bodyPart) {
        super.modifyBodyPart(index, bodyPart);
        ((FollowingWormMobBody)bodyPart).setMasterFollowingConfig(this.followingUniqueID);
    }

    @Override
    public PathDoorOption getPathDoorOption() {
        if (this.getLevel() != null) {
            return this.getLevel().regionManager.SUMMONED_MOB_OPTIONS;
        }
        return null;
    }

    @Override
    public CollisionFilter getLevelCollisionFilter() {
        return super.getLevelCollisionFilter();
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
    public void onUnloading(Region region) {
        Mob followingMob;
        super.onUnloading(region);
        if (this.isServer() && (followingMob = this.getFollowingMob()) != null && !this.isSamePlace(followingMob)) {
            this.onFollowingAnotherLevel(followingMob);
        }
    }

    @Override
    public boolean canPushMob(Mob other) {
        return other.isFollowing() && !other.isMounted();
    }

    @Override
    public void setFollowing(Mob mob, boolean sendUpdatePacket) {
        if (this.isFollowing() && mob == null) {
            super.setFollowing(mob, false);
            this.remove(0.0f, 0.0f, null, true);
        } else {
            super.setFollowing(mob, sendUpdatePacket);
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (!this.isFollowing()) {
            this.remove(0.0f, 0.0f, null, true);
        } else if (this.removeWhenNotInInventoryItem != null) {
            ++this.checkRemoveByMissingItemBuffer;
            if (this.checkRemoveByMissingItemBuffer > 20) {
                this.checkRemoveByMissingItemBuffer = 0;
                ItemAttackerMob followingAttacker = this.getFollowingItemAttacker();
                if (followingAttacker != null && !followingAttacker.hasValidSummonItem(this.removeWhenNotInInventoryItem, this.removeWhenNotInInventorySlotType)) {
                    this.remove(0.0f, 0.0f, null, true);
                }
            }
        }
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
    public float getSpeedModifier() {
        Mob attackOwner;
        if (this.isFollowing() && (attackOwner = this.getAttackOwner()) != null) {
            return (1.0f + (attackOwner.buffManager.getModifier(BuffModifiers.SUMMONS_SPEED).floatValue() - 1.0f) * 0.2f) * super.getSpeedModifier();
        }
        return super.getSpeedModifier();
    }

    @Override
    public boolean onMouseHover(GameCamera camera, PlayerMob perspective, boolean debug) {
        if (!debug) {
            return false;
        }
        return super.onMouseHover(camera, perspective, debug);
    }
}

