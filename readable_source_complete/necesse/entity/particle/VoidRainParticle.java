/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class VoidRainParticle
extends Particle {
    protected static final int[] frameTimes = new int[]{50, 50, 50, 50, 50, 50, 50};
    private final long spawnTime;
    private final long delay;
    private final boolean mirror;

    public VoidRainParticle(Level level, float x, float y, long spawnTime, long delay) {
        super(level, x, y, delay + 1500L);
        this.spawnTime = spawnTime;
        this.delay = delay;
        this.mirror = GameRandom.globalRandom.nextBoolean();
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        int frame;
        int drawX = camera.getDrawX(this.x);
        int drawY = camera.getDrawY(this.y);
        long eventTime = this.getWorldEntity().getTime() - this.spawnTime;
        if (eventTime >= this.delay) {
            long frameTime = eventTime - this.delay;
            frame = GameUtils.getAnim(frameTime, frameTimes);
            if (frame == -1) {
                return;
            }
            int spriteX = frame % 7;
            final TextureDrawOptionsEnd drawOptions = GameResources.voidRainParticle.initDraw().sprite(spriteX, 0, 32, 256).mirror(this.mirror, false).posMiddle(drawX, drawY - 128 + 6);
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
        float rotation = (float)((double)eventTime / 8.0);
        TextureDrawOptionsEnd shadowOptions = GameResources.voidRainTargetParticle.initDraw().sprite(0, 0, 66, 66).mirror(this.mirror, false).rotate(rotation, 33, 33).size(66, 66).alpha((float)Math.sin(rotation / 60.0f)).posMiddle(drawX, drawY);
        tileList.add(tm -> shadowOptions.draw());
    }
}

