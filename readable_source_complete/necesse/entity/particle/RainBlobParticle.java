/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class RainBlobParticle
extends Particle {
    public GameTextureSection texture;
    public int spriteRes;
    public Color color;
    public float alpha;

    public RainBlobParticle(Level level, float x, float y, GameTextureSection texture, int spriteRes, Color color, float alpha) {
        super(level, x, y, 200L);
        this.texture = texture;
        this.spriteRes = spriteRes;
        this.color = color;
        this.alpha = (float)color.getAlpha() * alpha / 255.0f;
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
        final TextureDrawOptionsEnd options = this.texture.sprite((int)(life * (float)frames), 0, this.spriteRes).initDraw().color(this.color).alpha(this.alpha).light(light).pos(drawX, drawY);
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }
}

