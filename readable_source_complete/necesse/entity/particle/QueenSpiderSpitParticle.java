/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.GlobalData;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.QueenSpiderSpitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class QueenSpiderSpitParticle
extends Particle {
    public int sprite = GameRandom.globalRandom.nextInt(2);
    public boolean mirror = GameRandom.globalRandom.nextBoolean();

    public QueenSpiderSpitParticle(Level level, float x, float y, long lifeTime) {
        super(level, x, y, lifeTime);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(this.getTileX(), this.getTileY());
        int drawX = camera.getDrawX(this.getX());
        int drawY = camera.getDrawY(this.getY());
        long remainingLifeTime = this.getRemainingLifeTime();
        float alpha = 1.0f;
        if (remainingLifeTime < 500L) {
            alpha = (float)remainingLifeTime / 500.0f;
        }
        TextureDrawOptionsEnd options = MobRegistry.Textures.queenSpider_spit.initDraw().sprite(this.sprite, 0, 64).mirror(this.mirror, false).light(light).alpha(alpha).posMiddle(drawX, drawY);
        tileList.add(tm -> {
            if (GlobalData.debugActive()) {
                Rectangle hitBox = QueenSpiderSpitEvent.hitBox;
                Renderer.drawShape(new Rectangle(this.getX() + hitBox.x, this.getY() + hitBox.y, hitBox.width, hitBox.height), camera, false, 1.0f, 0.0f, 0.0f, 1.0f);
            }
            options.draw();
        });
    }
}

