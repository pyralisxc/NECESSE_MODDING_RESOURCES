/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.mobMovement;

import java.awt.geom.Point2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketRequestMobData;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.mobMovement.MobMovement;
import necesse.entity.mobs.mobMovement.MobMovementLevelPos;

public class MobMovementDiagonalLineFixed
extends MobMovement {
    public int targetUniqueID;
    public float centerX;
    public float centerY;
    public float centerDistance;
    public float diagonalAngle;
    protected MobMovementLevelPos mover = new MobMovementLevelPos();
    protected Mob targetMob;
    private long requestTargetMobTime;

    public MobMovementDiagonalLineFixed() {
        this.mover.customMoveAccuracy = 1;
    }

    public MobMovementDiagonalLineFixed(Mob target, float centerX, float centerY, float centerDistance, float diagonalAngle) {
        this();
        this.targetMob = target;
        this.targetUniqueID = target.getUniqueID();
        this.centerX = centerX;
        this.centerY = centerY;
        this.centerDistance = centerDistance;
        this.diagonalAngle = diagonalAngle;
    }

    public MobMovementDiagonalLineFixed(Mob target, float centerX, float centerY, float centerDistance, Point2D.Float diagonalAngleDir) {
        this(target, centerX, centerY, centerDistance, GameMath.getAngle(diagonalAngleDir));
    }

    @Override
    public void setupPacket(Mob mob, PacketWriter writer) {
        writer.putNextInt(this.targetUniqueID);
        writer.putNextFloat(this.centerX);
        writer.putNextFloat(this.centerY);
        writer.putNextFloat(this.centerDistance);
        writer.putNextFloat(this.diagonalAngle);
    }

    @Override
    public void applyPacket(Mob mob, PacketReader reader) {
        this.targetUniqueID = reader.getNextInt();
        this.centerX = reader.getNextFloat();
        this.centerY = reader.getNextFloat();
        this.centerDistance = reader.getNextFloat();
        this.diagonalAngle = reader.getNextFloat();
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
            float angleToTarget = GameMath.fixAngle(GameMath.getAngle(GameMath.normalize(this.targetMob.x - this.centerX, this.targetMob.y - this.centerY)));
            double distanceToTarget = GameMath.getExactDistance(this.targetMob.x, this.targetMob.y, this.centerX, this.centerY);
            float angleDifference = GameMath.getAngleDifference(this.diagonalAngle, angleToTarget);
            float centerAngle = GameMath.fixAngle(angleDifference - 90.0f);
            double targetDistanceFromOffset = distanceToTarget * Math.sin(Math.toRadians(centerAngle));
            float offsetDistance = (float)Math.sqrt(Math.pow(distanceToTarget, 2.0) - Math.pow(targetDistanceFromOffset, 2.0));
            if (angleDifference > 0.0f) {
                offsetDistance = -offsetDistance;
            }
            Point2D.Float keepDistanceDir = GameMath.getAngleDir(this.diagonalAngle);
            float keepDistanceX = keepDistanceDir.x * this.centerDistance;
            float keepDistanceY = keepDistanceDir.y * this.centerDistance;
            Point2D.Float perpDir = GameMath.getPerpendicularDir(keepDistanceDir);
            float finalXOffset = keepDistanceX + perpDir.x * offsetDistance;
            float finalYOffset = keepDistanceY + perpDir.y * offsetDistance;
            this.mover.levelX = this.centerX + finalXOffset;
            this.mover.levelY = this.centerY + finalYOffset;
            return this.mover.tick(mob);
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MobMovementDiagonalLineFixed) {
            MobMovementDiagonalLineFixed other = (MobMovementDiagonalLineFixed)obj;
            return this.targetUniqueID == other.targetUniqueID && this.centerX == other.centerX && this.centerY == other.centerY && this.centerDistance == other.centerDistance && this.diagonalAngle == other.diagonalAngle;
        }
        return false;
    }
}

