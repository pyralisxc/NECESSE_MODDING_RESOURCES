/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.CameraShake;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.PouncingSlimeFollowingMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SlimeGreatswordFollowingMob
extends PouncingSlimeFollowingMob {
    public int lifeTime = 8000;

    @Override
    public void handleCollisionHit(Mob target, GameDamage damage, int knockback) {
        super.handleCollisionHit(target, damage, knockback);
        this.remove(0.0f, 0.0f, null, true);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.lifeTime -= 50;
        if (this.lifeTime <= 0) {
            this.remove(0.0f, 0.0f, null, true);
        }
    }

    @Override
    public void onConstructed(Level level) {
        super.onConstructed(level);
    }

    @Override
    public void onSpawned(int posX, int posY) {
        super.onSpawned(posX, posY);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.greatswordSlime, i, 4, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        int spriteX;
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(SlimeGreatswordFollowingMob.getTileCoordinate(x), SlimeGreatswordFollowingMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 26 - 32;
        boolean inLiquid = this.inLiquid(x, y);
        if (inLiquid) {
            spriteX = GameUtils.getAnim(this.getWorldEntity().getTime(), 2, 1000);
        } else {
            spriteX = this.getJumpAnimationFrame(6);
            if (spriteX == 0 && this.isPouncing()) {
                if (this.pounceShake == null || this.pounceShake.isOver(this.getWorldEntity().getLocalTime())) {
                    this.pounceShake = new CameraShake(this.getWorldEntity().getLocalTime(), 1000, 50, 2.0f, 2.0f, true);
                }
                Point2D.Float shake = this.pounceShake.getCurrentShake(this.getWorldEntity().getLocalTime());
                drawX = (int)((float)drawX + shake.x);
                drawY = (int)((float)drawY + shake.y);
                spriteX = GameUtils.getAnim(this.getWorldEntity().getTime(), 2, 200);
            }
        }
        drawY += this.getBobbing(x, y);
        final MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final TextureDrawOptionsEnd options = MobRegistry.Textures.greatswordSlime.initDraw().sprite(spriteX, inLiquid ? 1 : 0, 64).addMaskShader(swimMask).light(light).pos(drawX, drawY += level.getTile(SlimeGreatswordFollowingMob.getTileCoordinate(x), SlimeGreatswordFollowingMob.getTileCoordinate(y)).getMobSinkingAmount(this));
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                swimMask.use();
                options.draw();
                swimMask.stop();
            }
        });
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.greatswordSlime_shadow.initDraw().sprite(spriteX, 0, 64).light(light).pos(drawX, drawY);
        tileList.add(tm -> shadow.draw());
    }
}

