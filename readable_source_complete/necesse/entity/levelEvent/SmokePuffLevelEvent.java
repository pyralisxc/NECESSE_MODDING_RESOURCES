/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent;

import java.awt.Color;
import java.awt.Point;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SmokePuffParticle;

public class SmokePuffLevelEvent
extends LevelEvent {
    public float x;
    public float y;
    public int res;
    public Color color;

    public SmokePuffLevelEvent() {
    }

    public SmokePuffLevelEvent(float x, float y, int res, Color color) {
        this.x = x;
        this.y = y;
        this.res = res;
        this.color = color;
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.x = reader.getNextFloat();
        this.y = reader.getNextFloat();
        this.res = reader.getNextShortUnsigned();
        this.color = new Color(reader.getNextInt());
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.x);
        writer.putNextFloat(this.y);
        writer.putNextShortUnsigned(this.res);
        writer.putNextInt(this.color.getRGB());
    }

    @Override
    public void init() {
        super.init();
        if (this.isClient()) {
            this.level.entityManager.addParticle(new SmokePuffParticle(this.level, this.x, this.y, this.res, this.color), Particle.GType.IMPORTANT_COSMETIC);
        }
        this.over();
    }

    @Override
    public Point getSaveToRegionPos() {
        return new Point(this.level.regionManager.getRegionCoordByTile(GameMath.getTileCoordinate(this.x)), this.level.regionManager.getRegionCoordByTile(GameMath.getTileCoordinate(this.y)));
    }
}

