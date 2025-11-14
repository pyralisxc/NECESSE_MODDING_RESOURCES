/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.registries.BuffRegistry$Debuffs
 *  necesse.engine.util.GameRandom
 *  necesse.entity.Entity
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.projectile.Projectile
 *  necesse.entity.trails.Trail
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.texture.TextureDrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd
 *  necesse.gfx.drawables.EntityDrawable
 *  necesse.gfx.drawables.LevelSortedDrawable
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.level.maps.Level
 *  necesse.level.maps.LevelObjectHit
 *  necesse.level.maps.light.GameLight
 */
package aphorea.projectiles.toolitem;

import aphorea.projectiles.toolitem.GlacialShardSmallProjectile;
import aphorea.utils.AphColors;
import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class GlacialShardBigProjectile
extends Projectile {
    public int projectilesAmount;
    int seed;

    public GlacialShardBigProjectile() {
    }

    public GlacialShardBigProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, int seed) {
        this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.distance = distance;
        this.setDamage(damage);
        this.knockback = knockback;
        this.seed = seed;
    }

    public void init() {
        super.init();
        this.givesLight = false;
        this.height = 14.0f;
        this.trailOffset = -14.0f;
        this.setWidth(14.0f, true);
        this.piercing = 0;
        this.bouncing = 0;
        this.projectilesAmount = 6;
    }

    public Color getParticleColor() {
        return null;
    }

    public Trail getTrail() {
        return new Trail((Projectile)this, this.getLevel(), AphColors.ice, 22.0f, 100, this.getHeight());
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel((Entity)this);
        int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
        int drawY = camera.getDrawY(this.y);
        TextureDrawOptionsEnd options = this.texture.initDraw().light(light).rotate(this.getAngle(), this.texture.getWidth() / 2, 2).pos(drawX, drawY - (int)this.getHeight());
        list.add((LevelSortedDrawable)new EntityDrawable((Entity)this, (TextureDrawOptions)options){
            final /* synthetic */ TextureDrawOptions val$options;
            {
                this.val$options = textureDrawOptions;
                super(arg0);
            }

            public void draw(TickManager tickManager) {
                this.val$options.draw();
            }
        });
        this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle(), this.texture.getWidth() / 2, 2);
    }

    public void onHit(Mob mob, LevelObjectHit object, float x, float y, boolean fromPacket, ServerClient packetSubmitter) {
        super.onHit(mob, object, x, y, fromPacket, packetSubmitter);
        if (this.amountHit() < this.piercing || this.bounced >= this.getTotalBouncing() || !this.canBounce) {
            // empty if block
        }
    }

    public void remove() {
        GameRandom random = new GameRandom((long)this.seed);
        float randomAngle = GameRandom.globalRandom.getFloatBetween(0.0f, (float)Math.PI * 2);
        for (int i = 0; i < this.projectilesAmount; ++i) {
            Projectile projectile = this.getProjectile(randomAngle + (float)Math.PI * 2 * (float)i / (float)this.projectilesAmount);
            projectile.resetUniqueID(random);
            Mob owner = this.getOwner();
            if (!(owner instanceof ItemAttackerMob)) continue;
            ((ItemAttackerMob)owner).addAndSendAttackerProjectile(projectile);
        }
        super.remove();
    }

    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (mob != null) {
            mob.addBuff(new ActiveBuff(BuffRegistry.Debuffs.FROSTBURN, mob, 5000, (Attacker)this), true);
        }
    }

    private Projectile getProjectile(float angle) {
        float targetX = this.x + 100.0f * (float)Math.cos(angle);
        float targetY = this.y + 100.0f * (float)Math.sin(angle);
        GlacialShardSmallProjectile projectile = new GlacialShardSmallProjectile(this.getLevel(), this.getOwner(), this.x, this.y, targetX, targetY, 50.0f, 50, this.getDamage().modDamage(0.5f), this.knockback);
        projectile.resetUniqueID(new GameRandom((long)this.seed));
        return projectile;
    }
}

