/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent;

import java.awt.Color;
import java.awt.Point;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class TeleportFailEvent
extends LevelEvent {
    private int x;
    private int y;

    public TeleportFailEvent() {
    }

    public TeleportFailEvent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public TeleportFailEvent(Mob target) {
        this(target.getX(), target.getY());
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.x = reader.getNextInt();
        this.y = reader.getNextInt();
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.x);
        writer.putNextInt(this.y);
    }

    @Override
    public void init() {
        super.init();
        if (this.isClient()) {
            SoundManager.playSound(GameResources.teleportfail, (SoundEffect)SoundEffect.effect(this.x, this.y).pitch(0.7f));
            for (int i = 0; i < 10; ++i) {
                Particle.GType type = i <= 3 ? Particle.GType.CRITICAL : Particle.GType.COSMETIC;
                this.level.entityManager.addParticle(this.x + (int)(GameRandom.globalRandom.nextGaussian() * 8.0), this.y, type).movesConstant((float)GameRandom.globalRandom.nextGaussian() * 5.0f, (float)GameRandom.globalRandom.nextGaussian() * 5.0f).color(new Color(100, 100, 100)).height(GameRandom.globalRandom.nextInt(40)).lifeTime(600);
            }
        }
        this.over();
    }

    @Override
    public Point getSaveToRegionPos() {
        return new Point(this.level.regionManager.getRegionCoordByTile(GameMath.getTileCoordinate(this.x)), this.level.regionManager.getRegionCoordByTile(GameMath.getTileCoordinate(this.y)));
    }
}

