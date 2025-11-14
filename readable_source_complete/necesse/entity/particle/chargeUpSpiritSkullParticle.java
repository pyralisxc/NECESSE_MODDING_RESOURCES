/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class chargeUpSpiritSkullParticle
extends Particle {
    public chargeUpSpiritSkullParticle(Level level, float x, float y, long lifeTime) {
        super(level, x, y, lifeTime);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        int angle = GameRandom.globalRandom.nextInt(360);
        Point2D.Float dir = GameMath.getAngleDir(angle);
        float range = GameRandom.globalRandom.getFloatBetween(15.0f, 25.0f);
        float startX = this.x + dir.x * range;
        float startY = this.y + 4.0f;
        float endHeight = 29.0f;
        float startHeight = endHeight + dir.y * range;
        float speed = dir.x * range * 250.0f / (float)this.lifeTime;
        Color color1 = new Color(0, 107, 109);
        Color color2 = new Color(0, 138, 117);
        Color color3 = new Color(0, 191, 163);
        Color color = GameRandom.globalRandom.getOneOf(color1, color2, color3);
        this.getLevel().entityManager.addParticle(startX, startY + 110.0f, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(10, 16).rotates().heightMoves(startHeight + 100.0f, endHeight + 100.0f).movesConstant(-speed, 0.0f).color(color).fadesAlphaTime(100, 200).lifeTime((int)this.lifeTime);
    }
}

