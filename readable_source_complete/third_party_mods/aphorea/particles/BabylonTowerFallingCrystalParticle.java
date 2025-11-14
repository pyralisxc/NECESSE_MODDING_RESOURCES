/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.util.GameRandom
 *  necesse.engine.util.GameUtils
 *  necesse.entity.Entity
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
 *  necesse.level.maps.light.GameLight
 */
package aphorea.particles;

import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.Entity;
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
import necesse.level.maps.light.GameLight;

public class BabylonTowerFallingCrystalParticle
extends Particle {
    protected static final int[] frameTimes = new int[]{30, 30, 30, 30, 30, 60, 60, 60, 60, 60, 60, 120, 120, 120, 120};
    private final long spawnTime;
    private final long delay;
    private final boolean mirror;
    public static GameTexture projectileTexture;
    public static GameTexture shadowTexture;

    public BabylonTowerFallingCrystalParticle(Level level, float x, float y, long spawnTime, long delay) {
        super(level, x, y, delay + 1500L);
        this.spawnTime = spawnTime;
        this.delay = delay;
        this.mirror = GameRandom.globalRandom.nextBoolean();
    }

    public void clientTick() {
        super.clientTick();
        long eventTime = this.getWorldEntity().getTime() - this.spawnTime;
        if (eventTime >= this.delay) {
            long frameTime = eventTime - this.delay;
            int frame = GameUtils.getAnim((long)frameTime, (int[])frameTimes);
            if (frame == -1) {
                this.remove();
            } else if (frame < 10) {
                this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 0.0f, 0.5f);
            }
        }
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        int frame;
        GameLight light = level.getLightLevel(this.getX() / 32, this.getY() / 32);
        int drawX = camera.getDrawX(this.x);
        int drawY = camera.getDrawY(this.y);
        long eventTime = this.getWorldEntity().getTime() - this.spawnTime;
        if (eventTime >= this.delay) {
            long frameTime = eventTime - this.delay;
            frame = GameUtils.getAnim((long)frameTime, (int[])frameTimes);
            if (frame == -1) {
                return;
            }
            int bombDrawY = drawY;
            if (frame < 5) {
                bombDrawY -= 32 - frame * 7;
            }
            int spriteX = frame % 5;
            int spriteY = frame / 5;
            TextureDrawOptionsEnd drawOptions = projectileTexture.initDraw().sprite(spriteX, spriteY, 128, 192).mirror(this.mirror, false).posMiddle(drawX, bombDrawY);
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
        } else {
            frame = 0;
        }
        frame = Math.max(frame - 5, 0);
        if (frame < 5) {
            float rotation = 0.0f;
            float sizeMod = 1.0f;
            if (frame == 0) {
                rotation = (float)((double)eventTime / 4.0);
                sizeMod += (float)(Math.sin((double)eventTime / 80.0) / 10.0);
            }
            TextureDrawOptionsEnd shadowOptions = shadowTexture.initDraw().sprite(frame, 0, 128, 192).mirror(this.mirror, false).rotate(rotation, (int)(64.0f * sizeMod), (int)(96.0f * sizeMod)).size((int)(128.0f * sizeMod), (int)(192.0f * sizeMod)).light(light).posMiddle(drawX, drawY);
            tileList.add(arg_0 -> BabylonTowerFallingCrystalParticle.lambda$addDrawables$0((TextureDrawOptions)shadowOptions, arg_0));
        }
    }

    private static /* synthetic */ void lambda$addDrawables$0(TextureDrawOptions shadowOptions, TickManager tm) {
        shadowOptions.draw();
    }
}

