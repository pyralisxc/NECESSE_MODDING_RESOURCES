/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.util.GameMath
 *  necesse.engine.util.GameRandom
 *  necesse.engine.util.LineHitbox
 *  necesse.entity.mobs.Mob
 */
package aphorea.levelevents;

import aphorea.buffs.Trinkets.Shield.SpinelShieldBuff;
import aphorea.levelevents.ProjectileHitboxEffectEvent;
import java.awt.geom.Point2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LineHitbox;
import necesse.entity.mobs.Mob;

public abstract class ProjectileShieldLevelEvent
extends ProjectileHitboxEffectEvent {
    public float angle;

    public ProjectileShieldLevelEvent() {
    }

    public ProjectileShieldLevelEvent(Mob owner, float angle, GameRandom uniqueIDRandom) {
        super(owner, uniqueIDRandom);
        this.hitsObjects = false;
        this.angle = angle;
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.angle);
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.angle = reader.getNextFloat();
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.angle = this.getUpdatedAngle(this.owner, this.angle);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.angle = this.getUpdatedAngle(this.owner, this.angle);
    }

    public abstract float getMaxDelta();

    public float getUpdatedAngle(Mob mob, float lastAngle) {
        float targetAngle = SpinelShieldBuff.getInitialAngle(mob);
        float delta = ProjectileShieldLevelEvent.normalizeAngle(targetAngle - lastAngle);
        if (delta > this.getMaxDelta()) {
            delta = this.getMaxDelta();
        }
        if (delta < -this.getMaxDelta()) {
            delta = -this.getMaxDelta();
        }
        return ProjectileShieldLevelEvent.normalizeAngle(lastAngle + delta);
    }

    private static float normalizeAngle(float angle) {
        while ((double)angle <= -Math.PI) {
            angle += (float)Math.PI * 2;
        }
        while ((double)angle > Math.PI) {
            angle -= (float)Math.PI * 2;
        }
        return angle;
    }

    public LineHitbox getShieldHitBox(float range, float width, float frontOffset, float rangeOffset) {
        float dirX = (float)Math.cos(this.angle);
        float dirY = (float)Math.sin(this.angle);
        Point2D.Float dir = GameMath.getPerpendicularDir((float)dirX, (float)dirY);
        return new LineHitbox(this.owner.x + dir.x * rangeOffset + dirX * frontOffset, this.owner.y + dir.y * rangeOffset + dirY * frontOffset, dir.x, dir.y, width, range);
    }
}

