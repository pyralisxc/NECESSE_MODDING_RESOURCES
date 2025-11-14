/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.itemAttacker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketRemoveMob;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ability.IntMobAbility;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.FollowerPosition;
import necesse.entity.mobs.itemAttacker.FollowerTargetCooldown;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.itemAttacker.MobFollower;

public class ServerFollowersManager {
    public final ItemAttackerMob owner;
    private final IntMobAbility summonFocusUniqueIDSetter;
    public FollowerTargetCooldown followerTargetCooldowns;
    private int cleanTargetCooldownCacheTimer;
    private final ArrayList<MobFollower> followers = new ArrayList();
    public Mob summonFocusMob;

    public ServerFollowersManager(ItemAttackerMob owner, IntMobAbility summonFocusUniqueIDSetter) {
        this.owner = owner;
        this.summonFocusUniqueIDSetter = summonFocusUniqueIDSetter;
        this.followerTargetCooldowns = new FollowerTargetCooldown(owner);
    }

    public void setupSpawnPacket(PacketWriter writer) {
        int summonFocusUniqueID = this.owner.summonFocusUniqueID;
        if (this.summonFocusMob != null) {
            summonFocusUniqueID = this.summonFocusMob.getUniqueID();
        }
        if (summonFocusUniqueID != -1) {
            writer.putNextBoolean(true);
            writer.putNextInt(summonFocusUniqueID);
        } else {
            writer.putNextBoolean(false);
        }
    }

    public void applySpawnPacket(PacketReader reader) {
        if (reader.getNextBoolean()) {
            this.owner.summonFocusUniqueID = reader.getNextInt();
            if (this.owner.isServer()) {
                this.summonFocusMob = GameUtils.getLevelMob(this.owner.summonFocusUniqueID, this.owner.getLevel(), false);
            }
        } else {
            this.owner.summonFocusUniqueID = -1;
        }
    }

    public void serverTick() {
        ++this.cleanTargetCooldownCacheTimer;
        if (this.cleanTargetCooldownCacheTimer >= 100) {
            this.cleanTargetCooldownCacheTimer = 0;
            this.followerTargetCooldowns.cleanCache();
        }
        if (this.summonFocusMob != null && (this.summonFocusMob.removed() || !this.summonFocusMob.canBeTargeted(this.owner, this.owner.getPvPOwner()))) {
            this.clearSummonFocus();
        }
        this.tickFollowers();
    }

    public void setSummonFocus(int mobUniqueID, ServerClient client) {
        Mob lastFocus = this.summonFocusMob;
        if (mobUniqueID == -1) {
            this.summonFocusMob = null;
        } else {
            this.summonFocusMob = GameUtils.getLevelMob(mobUniqueID, this.owner.getLevel());
            if (this.summonFocusMob == null && client != null) {
                client.sendPacket(new PacketRemoveMob(mobUniqueID));
            }
        }
        Mob newFocus = this.summonFocusMob;
        if (lastFocus != newFocus) {
            this.summonFocusUniqueIDSetter.runAndSend(newFocus == null ? -1 : newFocus.getUniqueID());
        }
    }

    public void setSummonFocus(Mob mob) {
        for (MobFollower follower : this.followers) {
            follower.mob.onSummonReceivedNewFocus(mob);
        }
        if (mob != null) {
            if (this.summonFocusMob != mob) {
                this.summonFocusMob = mob;
                this.summonFocusUniqueIDSetter.runAndSend(mob.getUniqueID());
            }
        } else if (this.summonFocusMob != null) {
            this.summonFocusMob = null;
            this.summonFocusUniqueIDSetter.runAndSend(-1);
        }
    }

    public void clearSummonFocus() {
        this.setSummonFocus(null);
    }

    public void tickFollowers() {
        HashMap<String, Float> totalSummonedTypes = new HashMap<String, Float>();
        for (MobFollower mobFollower : this.followers) {
            float current = totalSummonedTypes.getOrDefault(mobFollower.summonType, Float.valueOf(0.0f)).floatValue();
            totalSummonedTypes.put(mobFollower.summonType, Float.valueOf(current + mobFollower.spaceTaken));
        }
        LinkedList<MobFollower> removes = new LinkedList<MobFollower>();
        for (MobFollower follower : this.followers) {
            int max;
            float current = totalSummonedTypes.getOrDefault(follower.summonType, Float.valueOf(0.0f)).floatValue();
            if (follower.mob.removed()) {
                totalSummonedTypes.put(follower.summonType, Float.valueOf(current - follower.spaceTaken));
                removes.add(follower);
                continue;
            }
            if (follower.maxSpace != null && current > (float)(max = follower.maxSpace.apply(this.owner).intValue())) {
                totalSummonedTypes.put(follower.summonType, Float.valueOf(current - follower.spaceTaken));
                removes.add(follower);
                continue;
            }
            if (follower.buffType != null && !this.owner.buffManager.hasBuff(follower.buffType)) {
                totalSummonedTypes.put(follower.summonType, Float.valueOf(current - follower.spaceTaken));
                removes.add(follower);
                continue;
            }
            if (follower.updateMob == null) continue;
            follower.updateMob.accept(this.owner, follower.mob);
        }
        HashSet<String> hashSet = new HashSet<String>();
        for (MobFollower follower : removes) {
            this.removeFollower(follower.mob, true, false);
            if (follower.buffType == null) continue;
            hashSet.add(follower.buffType);
        }
        for (String buffType : hashSet) {
            this.updateFollowerBuff(buffType);
        }
    }

    public Stream<MobFollower> streamFollowers() {
        return this.followers.stream();
    }

    public void clearFollowers() {
        ArrayList<MobFollower> removes = new ArrayList<MobFollower>(this.followers);
        HashSet<String> buffUpdates = new HashSet<String>();
        for (MobFollower follower : removes) {
            this.removeFollower(follower.mob, true, false);
            if (follower.buffType == null) continue;
            buffUpdates.add(follower.buffType);
        }
        for (String buffType : buffUpdates) {
            this.updateFollowerBuff(buffType);
        }
    }

    public void removeFollower(Mob mob, boolean sendUpdatePacket) {
        this.removeFollower(mob, sendUpdatePacket, true);
    }

    public void removeFollower(Mob mob, boolean sendUpdatePacket, boolean updateBuffs) {
        mob.setFollowing(null, sendUpdatePacket);
        MobFollower follower = this.followers.stream().filter(m -> m.mob == mob).findFirst().orElse(null);
        if (follower != null) {
            this.followers.remove(follower);
            if (updateBuffs) {
                this.updateFollowerBuff(follower.buffType);
            }
        }
    }

    public void addFollower(String summonType, Mob mob, FollowPosition position, String buffType, float spaceTaken, int maxSpace, BiConsumer<ItemAttackerMob, Mob> updateMob, boolean sendUpdatePacket) {
        this.addFollower(summonType, mob, position, buffType, spaceTaken, p -> maxSpace, updateMob, sendUpdatePacket);
    }

    public void addFollower(String summonType, Mob mob, FollowPosition position, String buffType, float spaceTaken, Function<ItemAttackerMob, Integer> maxSpace, BiConsumer<ItemAttackerMob, Mob> updateMob, boolean sendUpdatePacket) {
        mob.setFollowing(this.owner, sendUpdatePacket);
        this.followers.add(new MobFollower(summonType, mob, position, buffType, spaceTaken, maxSpace, updateMob));
        this.updateFollowerBuff(buffType);
    }

    public void updateFollowerBuff(String buffType) {
        if (buffType == null) {
            return;
        }
        double totalSpace = this.followers.stream().filter(f -> f.buffType != null && f.buffType.equals(buffType)).mapToDouble(f -> f.spaceTaken).sum();
        if ((totalSpace = Math.ceil(totalSpace)) <= 0.0) {
            this.owner.buffManager.removeBuff(buffType, true);
        } else {
            this.owner.buffManager.removeBuff(buffType, false);
            ActiveBuff ab = new ActiveBuff(buffType, (Mob)this.owner, 0, null);
            long i = 1L;
            while ((double)i < totalSpace) {
                ab.addStack(0, null);
                ++i;
            }
            this.owner.buffManager.addBuff(ab, true);
        }
    }

    public float getFollowerCount(String summonType) {
        return (float)this.followers.stream().filter(m -> m.summonType.equals(summonType)).mapToDouble(m -> m.spaceTaken).sum();
    }

    public int getUniqueFollowerCount() {
        ArrayList<String> uniqueSummons = new ArrayList<String>();
        for (MobFollower follower : this.followers) {
            if (uniqueSummons.contains(follower.mob.getStringID())) continue;
            uniqueSummons.add(follower.mob.getStringID());
        }
        return uniqueSummons.size();
    }

    public boolean isFollower(Mob mob) {
        return this.followers.stream().anyMatch(m -> m.mob == mob);
    }

    public FollowerPosition getFollowerPos(Mob mob, Mob targetMob, FollowerPosition currentPos) {
        MobFollower thisFollower = this.followers.stream().filter(m -> m.mob == mob).findFirst().orElse(null);
        if (thisFollower != null) {
            int index = 0;
            int total = 0;
            for (MobFollower follower : this.followers) {
                if (follower.mob == mob) {
                    index = total;
                }
                if (follower.position != thisFollower.position) continue;
                ++total;
            }
            return thisFollower.position.getRelativePos(targetMob, currentPos, index, total);
        }
        return null;
    }
}

