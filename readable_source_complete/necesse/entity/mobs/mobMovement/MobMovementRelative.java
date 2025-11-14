/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.mobMovement;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketRequestMobData;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.mobMovement.MobMovement;
import necesse.entity.mobs.mobMovement.MobMovementLevelPos;

public class MobMovementRelative
extends MobMovement {
    public int targetUniqueID;
    public float relativeX;
    public float relativeY;
    public boolean imitateDir;
    public boolean stopWhenColliding;
    protected MobMovementLevelPos mover = new MobMovementLevelPos();
    protected Mob targetMob;
    private long requestTargetMobTime;

    public MobMovementRelative() {
    }

    public MobMovementRelative(Mob target, float relativeX, float relativeY, boolean imitateDir, boolean stopWhenColliding) {
        this();
        this.targetMob = target;
        this.targetUniqueID = target.getUniqueID();
        this.relativeX = relativeX;
        this.relativeY = relativeY;
        this.imitateDir = imitateDir;
        this.stopWhenColliding = stopWhenColliding;
    }

    public MobMovementRelative(Mob target, float relativeX, float relativeY) {
        this(target, relativeX, relativeY, false, false);
    }

    public MobMovementRelative(Mob target, boolean stopWhenColliding) {
        this(target, 0.0f, 0.0f, false, stopWhenColliding);
    }

    @Override
    public void setupPacket(Mob mob, PacketWriter writer) {
        writer.putNextInt(this.targetUniqueID);
        writer.putNextFloat(this.relativeX);
        writer.putNextFloat(this.relativeY);
        writer.putNextBoolean(this.imitateDir);
        writer.putNextBoolean(this.stopWhenColliding);
    }

    @Override
    public void applyPacket(Mob mob, PacketReader reader) {
        this.targetUniqueID = reader.getNextInt();
        this.relativeX = reader.getNextFloat();
        this.relativeY = reader.getNextFloat();
        this.imitateDir = reader.getNextBoolean();
        this.stopWhenColliding = reader.getNextBoolean();
    }

    @Override
    public boolean tick(Mob mob) {
        if (this.targetMob == null || this.targetMob.getUniqueID() != this.targetUniqueID) {
            this.targetMob = GameUtils.getLevelMob(this.targetUniqueID, mob.getLevel());
            if (this.targetMob == null && mob.isClient() && this.requestTargetMobTime < mob.getWorldEntity().getTime()) {
                mob.getLevel().getClient().network.sendPacket(new PacketRequestMobData(this.targetUniqueID));
                this.requestTargetMobTime = mob.getWorldEntity().getTime() + 1000L;
            }
        }
        if (this.targetMob != null) {
            this.mover.levelX = this.targetMob.x + this.relativeX;
            this.mover.levelY = this.targetMob.y + this.relativeY;
            if (this.stopWhenColliding && this.targetMob.getCollision().intersects(mob.getCollision())) {
                if (this.imitateDir && this.targetMob.dx == 0.0f && this.targetMob.dy == 0.0f && !mob.isAttacking) {
                    mob.setDir(this.targetMob.getDir());
                }
                return true;
            }
            if (this.mover.tick(mob)) {
                if (this.imitateDir && this.targetMob.dx == 0.0f && this.targetMob.dy == 0.0f && !mob.isAttacking) {
                    mob.setDir(this.targetMob.getDir());
                }
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MobMovementRelative) {
            MobMovementRelative other = (MobMovementRelative)obj;
            return this.targetUniqueID == other.targetUniqueID && this.relativeX == other.relativeX && this.relativeY == other.relativeY;
        }
        return false;
    }

    public String toString() {
        String mobString = this.targetMob == null ? "" + this.targetUniqueID : this.targetMob.getStringID() + "@" + this.targetUniqueID;
        return "MobMovementRelative@" + Integer.toHexString(this.hashCode()) + "[" + mobString + ", " + GameMath.toDecimals(this.relativeX, 2) + ", " + GameMath.toDecimals(this.relativeY, 2) + "]";
    }
}

