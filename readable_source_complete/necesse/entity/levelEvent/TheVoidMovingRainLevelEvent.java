/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent;

import java.util.ArrayList;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.VoidRainAttackEvent;
import necesse.entity.mobs.LevelMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidMob;

public class TheVoidMovingRainLevelEvent
extends LevelEvent {
    public LevelMob<Mob> owner;
    protected int targetSearchTileRange;
    protected float maxOffset;
    protected float attacksPerSecond;
    protected int totalAttacks;
    protected float nextAttackBuffer = 0.0f;
    protected float attacksDone = 0.0f;

    public TheVoidMovingRainLevelEvent() {
    }

    public TheVoidMovingRainLevelEvent(Mob owner, int targetSearchTileRange, float maxOffset, float attacksPerSecond, int totalAttacks) {
        this.owner = owner == null ? null : new LevelMob<Mob>(owner);
        this.targetSearchTileRange = targetSearchTileRange;
        this.maxOffset = maxOffset;
        this.attacksPerSecond = attacksPerSecond;
        this.totalAttacks = totalAttacks;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.owner == null ? -1 : this.owner.uniqueID);
        writer.putNextInt(this.targetSearchTileRange);
        writer.putNextFloat(this.maxOffset);
        writer.putNextFloat(this.attacksPerSecond);
        writer.putNextInt(this.totalAttacks);
        writer.putNextFloat(this.nextAttackBuffer);
        writer.putNextFloat(this.attacksDone);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        int ownerID = reader.getNextInt();
        this.owner = ownerID == -1 ? null : new LevelMob<int>(ownerID);
        this.targetSearchTileRange = reader.getNextInt();
        this.maxOffset = reader.getNextFloat();
        this.attacksPerSecond = reader.getNextFloat();
        this.totalAttacks = reader.getNextInt();
        this.nextAttackBuffer = reader.getNextFloat();
        this.attacksDone = reader.getNextFloat();
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        if (this.isClient()) {
            return;
        }
        Mob owner = null;
        ArrayList<PlayerMob> targets = null;
        this.nextAttackBuffer += this.attacksPerSecond / 1000.0f * delta;
        int maxPlayerIncreases = (int)this.nextAttackBuffer;
        while (this.nextAttackBuffer >= 1.0f && this.attacksDone < (float)this.totalAttacks) {
            this.nextAttackBuffer -= 1.0f;
            this.attacksDone += 1.0f;
            if (owner == null) {
                owner = this.owner.get(this.getLevel());
            }
            if (owner == null) continue;
            Mob target = owner;
            if (owner.isHostile) {
                if (targets == null) {
                    targets = this.getLevel().entityManager.players.getInRegionByTileRange(owner.getTileX(), owner.getTileY(), this.targetSearchTileRange);
                }
                if (!targets.isEmpty()) {
                    target = (Mob)GameRandom.globalRandom.getOneOf(targets);
                    if (maxPlayerIncreases > 0) {
                        float increase = 0.5f * (float)(targets.size() - 1);
                        this.nextAttackBuffer += increase;
                        this.attacksDone -= increase;
                        --maxPlayerIncreases;
                    }
                }
            }
            int targetY = (int)(target.y + GameRandom.globalRandom.getFloatBetween(-this.maxOffset, this.maxOffset));
            int targetX = (int)(target.x + GameRandom.globalRandom.getFloatBetween(-this.maxOffset, this.maxOffset));
            VoidRainAttackEvent e = new VoidRainAttackEvent(owner, targetX, targetY, GameRandom.globalRandom, TheVoidMob.voidRainDamage);
            this.getLevel().entityManager.events.add(e);
        }
        if (this.attacksDone >= (float)this.totalAttacks) {
            this.over();
        }
    }
}

