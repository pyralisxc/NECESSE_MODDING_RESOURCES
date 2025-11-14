/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.mobMovement;

import java.awt.geom.Point2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.EmptyConstructorGameRegistry;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.mobMovement.MobMovementCircleLevelPos;
import necesse.entity.mobs.mobMovement.MobMovementCircleRelative;
import necesse.entity.mobs.mobMovement.MobMovementConstant;
import necesse.entity.mobs.mobMovement.MobMovementDiagonalLineFixed;
import necesse.entity.mobs.mobMovement.MobMovementLevelPos;
import necesse.entity.mobs.mobMovement.MobMovementRelative;
import necesse.entity.mobs.mobMovement.MobMovementSpiralLevelPos;
import necesse.entity.mobs.mobMovement.MobMovementSpiralRelative;

public abstract class MobMovement
implements IDDataContainer {
    public final IDData idData = new IDData();
    public static EmptyConstructorGameRegistry<MobMovement> registry = new EmptyConstructorGameRegistry<MobMovement>("MobMovement", Short.MAX_VALUE){

        @Override
        public void registerCore() {
            this.registerClass("constant", MobMovementConstant.class);
            this.registerClass("levelpos", MobMovementLevelPos.class);
            this.registerClass("relative", MobMovementRelative.class);
            this.registerClass("circlerelative", MobMovementCircleRelative.class);
            this.registerClass("circlelevel", MobMovementCircleLevelPos.class);
            this.registerClass("spiralrelative", MobMovementSpiralRelative.class);
            this.registerClass("spirallevel", MobMovementSpiralLevelPos.class);
            this.registerClass("diagonallinefixed", MobMovementDiagonalLineFixed.class);
        }

        @Override
        protected void onRegistryClose() {
        }
    };

    @Override
    public IDData getIDData() {
        return this.idData;
    }

    public MobMovement() {
        registry.applyIDData(this.getClass(), this.idData);
    }

    public abstract void setupPacket(Mob var1, PacketWriter var2);

    public abstract void applyPacket(Mob var1, PacketReader var2);

    public abstract boolean tick(Mob var1);

    public abstract boolean equals(Object var1);

    protected boolean moveTo(Mob mob, float targetX, float targetY, float errorValue) {
        float friction = mob.getFriction();
        float deltaX = targetX - mob.x;
        float deltaY = targetY - mob.y;
        float dist = mob.getDistance(targetX, targetY);
        if (dist > errorValue) {
            Point2D.Float dir = GameMath.normalize(deltaX, deltaY);
            mob.moveX = dir.x;
            mob.moveY = dir.y;
            if (Math.abs(deltaX) < (float)mob.stoppingDistance(friction, Math.abs(mob.dx)) / 2.0f) {
                mob.moveX = 0.0f;
            }
            if (Math.abs(deltaY) < (float)mob.stoppingDistance(friction, Math.abs(mob.dy)) / 2.0f) {
                mob.moveY = 0.0f;
            }
            return false;
        }
        return dist <= errorValue;
    }
}

