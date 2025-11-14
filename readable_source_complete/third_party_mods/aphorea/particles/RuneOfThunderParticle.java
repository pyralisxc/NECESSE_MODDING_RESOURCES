/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.registries.MobRegistry$Textures
 *  necesse.engine.util.GameRandom
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.particle.Particle
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.texture.TextureDrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd
 *  necesse.gfx.drawables.LevelSortedDrawable
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.level.maps.Level
 *  necesse.level.maps.light.GameLight
 */
package aphorea.particles;

import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class RuneOfThunderParticle
extends Particle {
    private final long spawnTime;
    private final boolean mirror;

    public RuneOfThunderParticle(Level level, float x, float y, long spawnTime) {
        super(level, x, y, 2000L);
        this.spawnTime = spawnTime;
        this.mirror = GameRandom.globalRandom.nextBoolean();
    }

    public void clientTick() {
        super.clientTick();
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 0.0f, 0.5f);
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(this.getX() / 32, this.getY() / 32);
        int drawX = camera.getDrawX(this.x);
        int drawY = camera.getDrawY(this.y);
        long eventTime = this.getWorldEntity().getTime() - this.spawnTime;
        float sizeMod = 1.0f + (float)(Math.sin((double)eventTime / 80.0) / 10.0);
        float rotation = (float)((double)eventTime / 4.0);
        TextureDrawOptionsEnd shadowOptions = MobRegistry.Textures.evilsProtectorBomb_shadow.initDraw().sprite(0, 0, 128, 192).mirror(this.mirror, false).rotate(rotation, (int)(64.0f * sizeMod), (int)(96.0f * sizeMod)).size((int)(128.0f * sizeMod), (int)(192.0f * sizeMod)).light(light).posMiddle(drawX, drawY);
        tileList.add(arg_0 -> RuneOfThunderParticle.lambda$addDrawables$0((TextureDrawOptions)shadowOptions, arg_0));
    }

    private static /* synthetic */ void lambda$addDrawables$0(TextureDrawOptions shadowOptions, TickManager tm) {
        shadowOptions.draw();
    }
}

