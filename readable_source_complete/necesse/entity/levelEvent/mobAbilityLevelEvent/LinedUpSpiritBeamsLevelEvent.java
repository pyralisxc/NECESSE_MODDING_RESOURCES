/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.WaitForSecondsEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.hostile.bosses.TheCursedCroneMob;
import necesse.entity.particle.CursedCroneSpiritGlyphParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.SpiritBeamProjectile;

public class LinedUpSpiritBeamsLevelEvent
extends MobAbilityLevelEvent {
    protected long nextSpawnTime;
    protected int timeBetweenSpawns;
    protected Point centerPosition;
    protected float angle;
    protected int countSpawned;
    protected int totalSpawns;
    protected float distanceBetweenBeams = 60.0f;

    public LinedUpSpiritBeamsLevelEvent() {
    }

    public LinedUpSpiritBeamsLevelEvent(Mob owner, GameRandom uniqueIDRandom, Point centerPosition, float angle, int timeBetweenSpawns, int totalSpawns) {
        super(owner, uniqueIDRandom);
        this.centerPosition = centerPosition;
        this.timeBetweenSpawns = timeBetweenSpawns;
        this.totalSpawns = totalSpawns;
        this.angle = angle;
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextLong(this.nextSpawnTime);
        writer.putNextInt(this.timeBetweenSpawns);
        writer.putNextInt(this.centerPosition.x);
        writer.putNextInt(this.centerPosition.y);
        writer.putNextFloat(this.angle);
        writer.putNextInt(this.countSpawned);
        writer.putNextInt(this.totalSpawns);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.nextSpawnTime = reader.getNextLong();
        this.timeBetweenSpawns = reader.getNextInt();
        this.centerPosition = new Point(reader.getNextInt(), reader.getNextInt());
        this.angle = reader.getNextFloat();
        this.countSpawned = reader.getNextInt();
        this.totalSpawns = reader.getNextInt();
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.tickSpawns();
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.tickSpawns();
    }

    public void tickSpawns() {
        if (this.nextSpawnTime <= this.getTime()) {
            this.nextSpawnTime = this.getTime() + (long)this.timeBetweenSpawns;
            float totalDistance = this.distanceBetweenBeams * (float)this.totalSpawns;
            float currentDistance = this.distanceBetweenBeams * (float)this.countSpawned;
            Point2D.Float dir = GameMath.getAngleDir(this.angle);
            float startX = (float)this.centerPosition.x - totalDistance * dir.x / 2.0f;
            float startY = (float)this.centerPosition.y - totalDistance * dir.y / 2.0f;
            float x = startX + dir.x * currentDistance;
            float y = startY + dir.y * currentDistance;
            this.chargeUpSpiritBeamGlyph(x, y, this.countSpawned);
            ++this.countSpawned;
            if (this.countSpawned >= this.totalSpawns) {
                this.over();
            }
        }
    }

    public void chargeUpSpiritBeamGlyph(final float x, final float y, final int count) {
        if (this.isClient()) {
            CursedCroneSpiritGlyphParticle particle = new CursedCroneSpiritGlyphParticle(this.level, x, y, 2000L);
            this.level.entityManager.addParticle(particle, Particle.GType.CRITICAL);
        }
        this.level.entityManager.events.addHidden(new WaitForSecondsEvent(0.5f){

            @Override
            public void onWaitOver() {
                LinedUpSpiritBeamsLevelEvent.this.fireSpiritBeam(x, y, count);
            }
        });
    }

    public void fireSpiritBeam(float x, float y, int count) {
        SpiritBeamProjectile spiritBeamProjectile = new SpiritBeamProjectile(this.level, this.owner, x, y - 800.0f, x, y, TheCursedCroneMob.spiritBeamsDamage, 150.0f, 800, false);
        spiritBeamProjectile.resetUniqueID(new GameRandom(this.getUniqueID()).nextSeeded(67 * count));
        this.getLevel().entityManager.projectiles.addHidden(spiritBeamProjectile);
    }
}

