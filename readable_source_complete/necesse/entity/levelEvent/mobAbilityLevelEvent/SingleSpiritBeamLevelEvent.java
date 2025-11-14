/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Point;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.WaitForSecondsEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.CursedCroneSpiritGlyphParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.SpiritBeamProjectile;

public class SingleSpiritBeamLevelEvent
extends MobAbilityLevelEvent {
    public Point targetPos;
    public GameDamage damage;
    public float timeUntilBeamLandsInSeconds;
    public boolean generateSoulsOnHit;

    public SingleSpiritBeamLevelEvent() {
    }

    public SingleSpiritBeamLevelEvent(Mob owner, GameRandom uniqueIDRandom, Point targetPos, GameDamage damage, float timeUntilBeamLandsInSeconds, boolean generateSoulsOnHit) {
        super(owner, uniqueIDRandom);
        this.targetPos = targetPos;
        this.damage = damage;
        this.timeUntilBeamLandsInSeconds = timeUntilBeamLandsInSeconds;
        this.generateSoulsOnHit = generateSoulsOnHit;
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.targetPos.x);
        writer.putNextInt(this.targetPos.y);
        writer.putNextFloat(this.timeUntilBeamLandsInSeconds);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.targetPos = new Point(reader.getNextInt(), reader.getNextInt());
        this.timeUntilBeamLandsInSeconds = reader.getNextFloat();
    }

    @Override
    public void init() {
        super.init();
        this.chargeUpSpiritBeamGlyph(this.targetPos.x, this.targetPos.y);
        this.over();
    }

    public void chargeUpSpiritBeamGlyph(final int xPos, final int yPos) {
        if (this.isClient()) {
            long lifeTime = (long)(this.timeUntilBeamLandsInSeconds * 4000.0f);
            CursedCroneSpiritGlyphParticle particle = new CursedCroneSpiritGlyphParticle(this.level, xPos, yPos, lifeTime);
            this.level.entityManager.addParticle(particle, Particle.GType.CRITICAL);
        }
        this.level.entityManager.events.addHidden(new WaitForSecondsEvent(this.timeUntilBeamLandsInSeconds){

            @Override
            public void onWaitOver() {
                SingleSpiritBeamLevelEvent.this.fireSpiritBeam(xPos, yPos);
            }
        });
    }

    public void fireSpiritBeam(int xPos, int yPos) {
        if (this.owner != null) {
            SpiritBeamProjectile spiritBeamProjectile = new SpiritBeamProjectile(this.level, this.owner, xPos, yPos - 800, xPos, yPos, this.damage, 150.0f / this.timeUntilBeamLandsInSeconds, 800, this.generateSoulsOnHit);
            spiritBeamProjectile.resetUniqueID(new GameRandom(this.getUniqueID()).nextSeeded(67));
            this.getLevel().entityManager.projectiles.addHidden(spiritBeamProjectile);
        }
    }
}

