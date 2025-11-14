/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

import java.awt.Point;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.Mob;
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

public class MobAfterimageParticle
extends Particle {
    private final Point sprite;
    private final int dir;
    private final int xOffset;
    private int yOffset;
    private final GameTexture mobGameTexture;
    private final int spriteRes;

    public MobAfterimageParticle(Level level, Mob mob, GameTexture mobGameTexture, int spriteRes, int lifetime) {
        super(level, mob.getX(), mob.getY(), lifetime);
        this.sprite = mob.getAnimSprite();
        this.mobGameTexture = mobGameTexture;
        this.spriteRes = spriteRes;
        this.dir = mob.getDir();
        this.xOffset = -32;
        this.yOffset = -51;
        this.yOffset += mob.getBobbing();
        this.yOffset += mob.getLevel().getTile(mob.getX() / spriteRes, mob.getY() / spriteRes).getMobSinkingAmount(mob);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        float life = this.getLifeCyclePercent();
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = this.getX() - camera.getX() + this.xOffset;
        int drawY = this.getY() - camera.getY() + this.yOffset;
        float alpha = Math.max(0.0f, 0.5f - life / 2.0f);
        final TextureDrawOptionsEnd drawOptions = this.mobGameTexture.initDraw().sprite(this.sprite.x, this.sprite.y, this.spriteRes).light(light).alpha(alpha).pos(drawX, drawY);
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
    }
}

