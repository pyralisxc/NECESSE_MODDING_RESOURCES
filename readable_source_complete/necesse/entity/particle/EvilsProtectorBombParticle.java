/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class EvilsProtectorBombParticle
extends Particle {
    protected static final int[] frameTimes = new int[]{30, 30, 30, 30, 30, 60, 60, 60, 60, 60, 60, 120, 120, 120, 120};
    private final long spawnTime;
    private final long delay;
    private final boolean mirror;

    public EvilsProtectorBombParticle(Level level, float x, float y, long spawnTime, long delay) {
        super(level, x, y, delay + 1500L);
        this.spawnTime = spawnTime;
        this.delay = delay;
        this.mirror = GameRandom.globalRandom.nextBoolean();
    }

    @Override
    public void clientTick() {
        super.clientTick();
        long eventTime = this.getWorldEntity().getTime() - this.spawnTime;
        if (eventTime >= this.delay) {
            long frameTime = eventTime - this.delay;
            int frame = GameUtils.getAnim(frameTime, frameTimes);
            if (frame == -1) {
                this.remove();
            } else if (frame < 10) {
                this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 0.0f, 0.5f);
            }
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        int frame;
        GameLight light = level.getLightLevel(this.getTileX(), this.getTileY());
        int drawX = camera.getDrawX(this.x);
        int drawY = camera.getDrawY(this.y);
        long eventTime = this.getWorldEntity().getTime() - this.spawnTime;
        if (eventTime >= this.delay) {
            long frameTime = eventTime - this.delay;
            frame = GameUtils.getAnim(frameTime, frameTimes);
            if (frame == -1) {
                return;
            }
            int bombDrawY = drawY;
            if (frame < 5) {
                bombDrawY -= 32 - frame * 7;
            }
            int spriteX = frame % 5;
            int spriteY = frame / 5;
            final TextureDrawOptionsEnd drawOptions = MobRegistry.Textures.evilsProtectorBomb.initDraw().sprite(spriteX, spriteY, 128, 192).mirror(this.mirror, false).light(light).posMiddle(drawX, bombDrawY);
            list.add(new EntityDrawable(this){

                @Override
                public void draw(TickManager tickManager) {
                    drawOptions.draw();
                }
            });
        } else {
            frame = 0;
        }
        frame = Math.max(frame - 5, 0);
        if (frame >= 5) {
            return;
        }
        float rotation = 0.0f;
        float sizeMod = 1.0f;
        if (frame == 0) {
            rotation = (float)((double)eventTime / 4.0);
            sizeMod += (float)(Math.sin((double)eventTime / 80.0) / 10.0);
        }
        TextureDrawOptionsEnd shadowOptions = MobRegistry.Textures.evilsProtectorBomb_shadow.initDraw().sprite(frame, 0, 128, 192).mirror(this.mirror, false).rotate(rotation, (int)(64.0f * sizeMod), (int)(96.0f * sizeMod)).size((int)(128.0f * sizeMod), (int)(192.0f * sizeMod)).light(light).posMiddle(drawX, drawY);
        tileList.add(tm -> shadowOptions.draw());
    }
}

