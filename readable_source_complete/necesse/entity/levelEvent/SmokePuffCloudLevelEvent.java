/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class SmokePuffCloudLevelEvent
extends LevelEvent {
    public Rectangle rectangle;
    public Color color;

    public SmokePuffCloudLevelEvent() {
    }

    public SmokePuffCloudLevelEvent(Rectangle levelRectangle, Color color) {
        this.rectangle = levelRectangle;
        this.color = color;
    }

    public SmokePuffCloudLevelEvent(int levelX, int levelY, Color color) {
        this(new Rectangle(levelX, levelY, 1, 1), color);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        int x = reader.getNextInt();
        int y = reader.getNextInt();
        int w = reader.getNextShortUnsigned();
        int h = reader.getNextShortUnsigned();
        this.rectangle = new Rectangle(x, y, w, h);
        this.color = new Color(reader.getNextInt());
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.rectangle.x);
        writer.putNextInt(this.rectangle.y);
        writer.putNextShortUnsigned(this.rectangle.width);
        writer.putNextShortUnsigned(this.rectangle.height);
        writer.putNextInt(this.color.getRGB());
    }

    @Override
    public void init() {
        super.init();
        if (this.isClient()) {
            SoundManager.playSound(GameResources.magicbolt3, (SoundEffect)SoundEffect.effect((float)this.rectangle.x + (float)this.rectangle.width / 2.0f, (float)this.rectangle.y + (float)this.rectangle.height / 2.0f));
            for (int i = 0; i < 40; ++i) {
                int x = GameRandom.globalRandom.getIntBetween(this.rectangle.x, this.rectangle.x + this.rectangle.width);
                int y = GameRandom.globalRandom.getIntBetween(this.rectangle.y, this.rectangle.y + this.rectangle.height);
                int startHeight = GameRandom.globalRandom.nextInt(16);
                int endHeight = startHeight + 8 + GameRandom.globalRandom.nextInt(64);
                int lifeTime = GameRandom.globalRandom.getIntBetween(1000, 8000);
                this.level.entityManager.addParticle(x, y, Particle.GType.IMPORTANT_COSMETIC).movesFriction(GameRandom.globalRandom.getFloatBetween(-2.0f, 2.0f), GameRandom.globalRandom.getFloatBetween(-2.0f, 2.0f), 0.1f).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).colorRandom(this.color, 0.1f, 0.2f, 0.2f).heightMoves(startHeight, endHeight).lifeTime(lifeTime).fadesAlphaTimeToCustomAlpha(200, 200, 1.0f).sizeFades(12, 24);
            }
        }
        this.over();
    }

    @Override
    public Point getSaveToRegionPos() {
        return new Point(this.level.regionManager.getRegionCoordByTile(GameMath.getTileCoordinate(this.rectangle.x)), this.level.regionManager.getRegionCoordByTile(GameMath.getTileCoordinate(this.rectangle.y)));
    }
}

