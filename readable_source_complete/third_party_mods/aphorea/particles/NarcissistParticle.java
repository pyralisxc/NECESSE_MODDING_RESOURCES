/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.entity.Entity
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.particle.Particle
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.texture.TextureDrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd
 *  necesse.gfx.drawables.EntityDrawable
 *  necesse.gfx.drawables.LevelSortedDrawable
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.level.maps.Level
 */
package aphorea.particles;

import aphorea.levelevents.AphNarcissistEvent;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;

public class NarcissistParticle
extends Particle {
    public static GameTexture texture;
    public float startX;
    public float startY;
    public float moveX;
    public float moveY;
    public float startAngle;
    public Mob owner;

    public NarcissistParticle(Level level, Mob owner, float startX, float startY, float startAngle) {
        super(level, startX, startY, 5000L);
        this.owner = owner;
        this.startX = startX;
        this.startY = startY;
        this.moveX = 0.0f;
        this.moveY = 0.0f;
        this.startAngle = startAngle;
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        int drawX = camera.getDrawX(this.getPosX());
        int drawY = camera.getDrawY(this.getPosY());
        float angleDeg = (float)Math.toDegrees(this.getStartAngle());
        TextureDrawOptionsEnd drawOptions = texture.initDraw().alpha(this.getAlpha()).rotate(angleDeg + 135.0f).posMiddle(drawX, drawY);
        list.add((LevelSortedDrawable)new EntityDrawable((Entity)this, (TextureDrawOptions)drawOptions){
            final /* synthetic */ TextureDrawOptions val$drawOptions;
            {
                this.val$drawOptions = textureDrawOptions;
                super(arg0);
            }

            public void draw(TickManager tickManager) {
                this.val$drawOptions.draw();
            }
        });
    }

    public float getAlpha() {
        return 1.0f - AphNarcissistEvent.easeOutCirc(this.getLifeCyclePercent()) * 0.25f;
    }

    public float getPosX() {
        return AphNarcissistEvent.getX(this.startX, this.startAngle, this.getLifeCyclePercent()) + this.moveX;
    }

    public float getPosY() {
        return AphNarcissistEvent.getY(this.startY, this.startAngle, this.getLifeCyclePercent()) + this.moveY;
    }

    public float getStartAngle() {
        return AphNarcissistEvent.getAngle(this.startAngle, this.getLifeCyclePercent());
    }
}

