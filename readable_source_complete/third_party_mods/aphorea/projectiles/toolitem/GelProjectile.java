/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.util.GameRandom
 *  necesse.entity.Entity
 *  necesse.entity.levelEvent.LevelEvent
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.MobHitCooldowns
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.particle.Particle
 *  necesse.entity.particle.Particle$GType
 *  necesse.entity.projectile.Projectile
 *  necesse.entity.trails.Trail
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.DrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd
 *  necesse.gfx.drawables.EntityDrawable
 *  necesse.gfx.drawables.LevelSortedDrawable
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.level.maps.Level
 *  necesse.level.maps.LevelObjectHit
 *  necesse.level.maps.light.GameLight
 */
package aphorea.projectiles.toolitem;

import aphorea.registry.AphBuffs;
import aphorea.utils.AphColors;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class GelProjectile
extends Projectile {
    public GelProjectile() {
    }

    public GelProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
        this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.distance = distance;
        this.setDamage(damage);
        this.knockback = knockback;
    }

    public void init() {
        super.init();
        this.givesLight = false;
        this.height = 18.0f;
        this.trailOffset = -14.0f;
        this.setWidth(16.0f, true);
        this.piercing = 0;
        this.bouncing = 0;
    }

    public Color getParticleColor() {
        return AphColors.gel;
    }

    public Trail getTrail() {
        return new Trail((Projectile)this, this.getLevel(), AphColors.gel, 26.0f, 500, this.getHeight());
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

    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (mob != null) {
            ActiveBuff ab = new ActiveBuff(AphBuffs.STICKY, mob, 1.0f, (Attacker)this.getOwner());
            mob.addBuff(ab, true);
        }
    }

    public void remove() {
        Mob owner;
        if (this.isServer() && (owner = this.getOwner()) != null && !owner.removed()) {
            GelProjectileGroundEffectEvent event = new GelProjectileGroundEffectEvent(owner, (int)this.x, (int)this.y, GameRandom.globalRandom);
            this.getLevel().entityManager.events.add((LevelEvent)event);
        }
        super.remove();
    }

    public static class GelProjectileGroundEffectEvent
    extends GroundEffectEvent {
        protected int tickCounter;
        protected MobHitCooldowns hitCooldowns;
        protected GelProjectileParticle particle;

        public GelProjectileGroundEffectEvent() {
        }

        public GelProjectileGroundEffectEvent(Mob owner, int x, int y, GameRandom uniqueIDRandom) {
            super(owner, x, y, uniqueIDRandom);
        }

        public void setupSpawnPacket(PacketWriter writer) {
            super.setupSpawnPacket(writer);
        }

        public void applySpawnPacket(PacketReader reader) {
            super.applySpawnPacket(reader);
        }

        public void init() {
            super.init();
            this.tickCounter = 0;
            this.hitCooldowns = new MobHitCooldowns();
            if (this.isClient()) {
                this.particle = new GelProjectileParticle(this.level, this.x, this.y, 5000L);
                this.level.entityManager.addParticle((Particle)this.particle, true, Particle.GType.CRITICAL);
            }
        }

        public Shape getHitBox() {
            int width = 40;
            int height = 30;
            return new Rectangle(this.x - width / 2, this.y - height / 2, width, height);
        }

        public void clientHit(Mob mob) {
        }

        public void serverHit(Mob target, boolean clientSubmitted) {
            if (clientSubmitted || !target.buffManager.hasBuff(AphBuffs.STICKY)) {
                target.addBuff(new ActiveBuff(AphBuffs.STICKY, target, 1000, (Attacker)this), true);
            }
        }

        public void hitObject(LevelObjectHit hit) {
        }

        public void clientTick() {
            ++this.tickCounter;
            if (this.tickCounter > 100) {
                this.over();
            } else {
                super.clientTick();
            }
        }

        public void serverTick() {
            ++this.tickCounter;
            if (this.tickCounter > 100) {
                this.over();
            } else {
                super.serverTick();
            }
        }

        public void over() {
            super.over();
            if (this.particle != null) {
                this.particle.despawnNow();
            }
        }
    }

    public static class GelProjectileParticle
    extends Particle {
        public static GameTexture texture;
        public int gel = GameRandom.globalRandom.nextInt(4);

        public GelProjectileParticle(Level level, float x, float y, long lifeTime) {
            super(level, x, y, lifeTime);
        }

        public void despawnNow() {
            if (this.getRemainingLifeTime() > 500L) {
                this.lifeTime = 500L;
                this.spawnTime = this.getWorldEntity().getLocalTime();
            }
        }

        public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
            GameLight light = level.getLightLevel(this.getX() / 32, this.getY() / 32);
            int drawX = camera.getDrawX(this.getX()) - 48;
            int drawY = camera.getDrawY(this.getY()) - 48;
            long remainingLifeTime = this.getRemainingLifeTime();
            float alpha = 1.0f;
            if (remainingLifeTime < 500L) {
                alpha = Math.max(0.0f, (float)remainingLifeTime / 500.0f);
            }
            TextureDrawOptionsEnd options = texture.initDraw().sprite(this.gel, 0, 96).light(light).alpha(alpha).pos(drawX, drawY);
            tileList.add(arg_0 -> GelProjectileParticle.lambda$addDrawables$0((DrawOptions)options, arg_0));
        }

        private static /* synthetic */ void lambda$addDrawables$0(DrawOptions options, TickManager tm) {
            options.draw();
        }
    }
}

