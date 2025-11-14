/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class LiquidBlobParticle
extends Particle {
    public GameTexture texture;
    public int spriteRes;

    public LiquidBlobParticle(Level level, float x, float y, GameTexture texture, int spriteRes) {
        super(level, x, y, 1000L);
        this.texture = texture;
        this.spriteRes = spriteRes;
    }

    @Override
    public void remove() {
        super.remove();
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        float life = this.getLifeCyclePercent();
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.getX()) - this.spriteRes / 2;
        int drawY = camera.getDrawY(this.getY()) - this.spriteRes / 2;
        int frames = this.texture.getWidth() / this.spriteRes;
        final TextureDrawOptionsEnd options = this.texture.initDraw().sprite((int)(life * (float)frames), 0, this.spriteRes).light(light).pos(drawX, drawY);
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }
}

