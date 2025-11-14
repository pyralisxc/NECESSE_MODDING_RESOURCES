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

public class SpinelShieldParticle
extends Particle {
    public static GameTexture texture;
    public float angle;
    public Mob owner;

    public SpinelShieldParticle(Level level, Mob owner, float angle) {
        super(level, owner.x, owner.y, 50L);
        this.owner = owner;
        this.angle = angle;
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        int offsetX = (int)(Math.cos(this.angle) * 40.0);
        int offsetY = (int)(Math.sin(this.angle) * 40.0);
        int drawX = camera.getDrawX(this.owner.x) + offsetX;
        int drawY = camera.getDrawY(this.owner.y) + offsetY;
        TextureDrawOptionsEnd drawOptions = texture.initDraw().alpha(0.5f).rotate((float)Math.toDegrees(this.angle)).posMiddle(drawX, drawY);
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
}

