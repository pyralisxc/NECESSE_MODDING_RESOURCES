/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.AscendedBoltProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.AscendedBowProjectileToolItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class AscendedBowBoltProjectile
extends AscendedBoltProjectile {
    protected boolean toggledOn = true;

    public AscendedBowBoltProjectile() {
    }

    public AscendedBowBoltProjectile(Level level, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, Mob owner, boolean toggledOn) {
        super(level, x, y, targetX, targetY, speed, distance, damage, owner);
        this.toggledOn = toggledOn;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextBoolean(this.toggledOn);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.toggledOn = reader.getNextBoolean();
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), this.toggledOn ? AscendedBowProjectileToolItem.toggledOnColor : AscendedBowProjectileToolItem.toggledOffColor, 11.0f, 200, this.getHeight());
    }

    @Override
    protected void spawnDeathParticles() {
        GameRandom random = GameRandom.globalRandom;
        float anglePerParticle = 72.0f;
        for (int i = 0; i < 5; ++i) {
            int angle = (int)((float)i * anglePerParticle + random.nextFloat() * anglePerParticle);
            float dx = (float)(Math.sin(Math.toRadians(angle)) + (double)this.dx) * 20.0f;
            float dy = (float)(Math.cos(Math.toRadians(angle)) + (double)this.dy) * 20.0f;
            this.getLevel().entityManager.addParticle(this, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.ascendedParticle.sprite(0, 0, 20)).sizeFades(10, 20).color(this.toggledOn ? Color.WHITE : AscendedBowProjectileToolItem.toggledOffOverlayColor).ignoreLight(true).heightMoves(this.getHeight(), this.getHeight() - 30.0f).movesFriction(dx * random.getFloatBetween(1.0f, 3.0f), dy * random.getFloatBetween(1.0f, 3.0f), 0.8f).lifeTime(1500);
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int centerDistance = this.texture.getHeight() / 2;
        int drawX = camera.getDrawX(this.x) - centerDistance;
        int drawY = camera.getDrawY(this.y) - centerDistance;
        int anim = GameUtils.getAnim(this.getWorldEntity().getTime(), 4, 400);
        final TextureDrawOptionsEnd options = this.texture.initDraw().sprite(anim, 0, 32).light(light.minLevelCopy(150.0f)).color(this.toggledOn ? Color.WHITE : AscendedBowProjectileToolItem.toggledOffOverlayColor).rotate(this.getAngle() - 45.0f, centerDistance, centerDistance).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        TextureDrawOptionsEnd shadowOptions = this.shadowTexture.initDraw().sprite(anim, 0, 32).light(light.minLevelCopy(150.0f)).rotate(this.getAngle() - 45.0f, centerDistance, centerDistance).color(this.toggledOn ? Color.WHITE : AscendedBowProjectileToolItem.toggledOffOverlayColor).pos(drawX, drawY);
        tileList.add(tm -> shadowOptions.draw());
    }
}

