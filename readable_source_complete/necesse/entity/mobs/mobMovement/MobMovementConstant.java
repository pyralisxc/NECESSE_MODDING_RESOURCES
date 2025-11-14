/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.mobMovement;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.mobMovement.MobMovement;

public class MobMovementConstant
extends MobMovement {
    private float moveX;
    private float moveY;

    public MobMovementConstant() {
    }

    public MobMovementConstant(float moveX, float moveY) {
        this();
        this.moveX = moveX;
        this.moveY = moveY;
    }

    @Override
    public void setupPacket(Mob mob, PacketWriter writer) {
        writer.putNextFloat(this.moveX);
        writer.putNextFloat(this.moveY);
    }

    @Override
    public void applyPacket(Mob mob, PacketReader reader) {
        this.moveX = reader.getNextFloat();
        this.moveY = reader.getNextFloat();
    }

    @Override
    public boolean tick(Mob mob) {
        mob.moveX = this.moveX;
        mob.moveY = this.moveY;
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MobMovementConstant) {
            MobMovementConstant other = (MobMovementConstant)obj;
            return this.moveX == other.moveX && this.moveY == other.moveY;
        }
        return false;
    }
}

