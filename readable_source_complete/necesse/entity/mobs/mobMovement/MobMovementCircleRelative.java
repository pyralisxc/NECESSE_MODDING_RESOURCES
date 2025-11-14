/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.mobMovement;

import java.awt.geom.Point2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketRequestMobData;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.mobMovement.MobMovementCircle;

public class MobMovementCircleRelative
extends MobMovementCircle {
    public int targetUniqueID;
    public float relativeX;
    public float relativeY;
    protected Mob targetMob;
    private long requestTargetMobTime;

    public MobMovementCircleRelative() {
    }

    public MobMovementCircleRelative(Mob mob, Mob targetMob, int relativeX, int relativeY, int range, float speed, float angleOffset, boolean reversed) {
        super(mob, range, speed, angleOffset, reversed);
        this.targetMob = targetMob;
        this.targetUniqueID = targetMob.getUniqueID();
        this.relativeX = relativeX;
        this.relativeY = relativeY;
    }

    public MobMovementCircleRelative(Mob mob, Mob targetMob, int range, float speed, float angleOffset, boolean reversed) {
        this(mob, targetMob, 0, 0, range, speed, angleOffset, reversed);
    }

    public MobMovementCircleRelative(Mob mob, Mob targetMob, int relativeX, int relativeY, int range, float speed, float offsetCenterX, float offsetCenterY, boolean reversed) {
        super(mob, offsetCenterX, offsetCenterY, range, speed, reversed);
        this.targetMob = targetMob;
        this.targetUniqueID = targetMob.getUniqueID();
        this.relativeX = relativeX;
        this.relativeY = relativeY;
    }

    public MobMovementCircleRelative(Mob mob, Mob targetMob, int range, float speed, float offsetCenterX, float offsetCenterY, boolean reversed) {
        this(mob, targetMob, 0, 0, range, speed, offsetCenterX, offsetCenterY, reversed);
    }

    public MobMovementCircleRelative(Mob mob, Mob targetMob, int relativeX, int relativeY, int range, float speed, boolean reversed) {
        super(mob, targetMob.x + (float)relativeX, targetMob.y + (float)relativeY, range, speed, reversed);
        this.targetMob = targetMob;
        this.targetUniqueID = targetMob.getUniqueID();
        this.relativeX = relativeX;
        this.relativeY = relativeY;
    }

    public MobMovementCircleRelative(Mob mob, Mob targetMob, int range, float speed, boolean reversed) {
        this(mob, targetMob, 0, 0, range, speed, reversed);
    }

    @Override
    public void setupPacket(Mob mob, PacketWriter writer) {
        writer.putNextInt(this.targetUniqueID);
        writer.putNextFloat(this.relativeX);
        writer.putNextFloat(this.relativeY);
        super.setupPacket(mob, writer);
    }

    @Override
    public void applyPacket(Mob mob, PacketReader reader) {
        this.targetUniqueID = reader.getNextInt();
        this.relativeX = reader.getNextFloat();
        this.relativeY = reader.getNextFloat();
        super.applyPacket(mob, reader);
    }

    @Override
    public Point2D.Float getCenterPos() {
        if (this.targetMob == null) {
            return null;
        }
        return new Point2D.Float(this.targetMob.x + this.relativeX, this.targetMob.y + this.relativeY);
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
        return super.tick(mob);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MobMovementCircleRelative) {
            MobMovementCircleRelative other = (MobMovementCircleRelative)obj;
            return this.targetUniqueID == other.targetUniqueID && super.equals(obj);
        }
        return false;
    }
}

