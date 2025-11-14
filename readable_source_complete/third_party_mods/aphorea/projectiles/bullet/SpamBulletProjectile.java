/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.registries.BuffRegistry$Debuffs
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.engine.util.GameRandom
 *  necesse.entity.Entity
 *  necesse.entity.levelEvent.LevelEvent
 *  necesse.entity.levelEvent.explosionEvent.ExplosionEvent
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.MobHitCooldowns
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.particle.Particle
 *  necesse.entity.particle.Particle$GType
 *  necesse.entity.projectile.bulletProjectile.BulletProjectile
 *  necesse.entity.trails.Trail
 *  necesse.gfx.GameResources
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.DrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd
 *  necesse.gfx.drawables.EntityDrawable
 *  necesse.gfx.drawables.LevelSortedDrawable
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.toolItem.ToolItem
 *  necesse.level.maps.Level
 *  necesse.level.maps.LevelObjectHit
 *  necesse.level.maps.light.GameLight
 */
package aphorea.projectiles.bullet;

import aphorea.registry.AphBuffs;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.bulletProjectile.BulletProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class SpamBulletProjectile
extends BulletProjectile {
    ToolItem toolItem;
    InventoryItem item;
    private long spawnTime;
    private int type;
    private boolean clockWise;
    public AphAreaList areaList = new AphAreaList(new AphArea(100.0f, 0.5f, AphColors.green).setHealingArea(2));

    public SpamBulletProjectile() {
    }

    public SpamBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, ToolItem toolItem, InventoryItem item, Mob owner) {
        super(x, y, targetX, targetY, speed, distance, damage, knockback, owner);
        this.toolItem = toolItem;
        this.item = item;
    }

    public void init() {
        super.init();
        this.setWidth(10.0f);
        this.height = 18.0f;
        this.heightBasedOnDistance = true;
        this.trailOffset = 0.0f;
        this.spawnTime = this.getWorldEntity().getTime();
        GameRandom gameRandom = new GameRandom((long)this.getUniqueID());
        this.type = gameRandom.getIntBetween(0, 4);
        this.clockWise = gameRandom.nextBoolean();
        if (this.type == 1 || this.type == 4) {
            this.doesImpactDamage = false;
        }
        if (this.type == 2) {
            this.bouncing = 10;
            this.piercing = 10;
        } else {
            this.canBounce = false;
        }
    }

    public Trail getTrail() {
        return null;
    }

    protected Color getWallHitColor() {
        return AphColors.spinel;
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (!this.removed()) {
            GameLight light = level.getLightLevel((Entity)this);
            int textureRes = 32;
            int halfTextureRes = textureRes / 2;
            int drawX = camera.getDrawX(this.x) - halfTextureRes;
            int drawY = camera.getDrawY(this.y) - halfTextureRes;
            TextureDrawOptionsEnd options = this.texture.initDraw().sprite(this.type, 0, textureRes).light(light).rotate(this.getAngle(), halfTextureRes, halfTextureRes).pos(drawX, drawY - (int)this.getHeight());
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
            TextureDrawOptionsEnd shadowOptions = this.shadowTexture.initDraw().sprite(this.type, 0, textureRes).light(light).rotate(this.getAngle(), halfTextureRes, halfTextureRes).pos(drawX, drawY);
            tileList.add(arg_0 -> SpamBulletProjectile.lambda$addDrawables$0((TextureDrawOptions)shadowOptions, arg_0));
        }
    }

    public float getAngle() {
        return (float)(this.getWorldEntity().getTime() - this.spawnTime) * (float)(this.clockWise ? 1 : -1);
    }

    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (this.type == 0 && mob != null) {
            mob.buffManager.addBuff(new ActiveBuff(AphBuffs.STICKY, mob, 5.0f, null), false);
        } else if (this.type == 1) {
            FirePoolGroundEffectEvent event = new FirePoolGroundEffectEvent(this.getOwner(), (int)x, (int)y, new GameRandom((long)GameRandom.getNewUniqueID()));
            this.getLevel().entityManager.events.add((LevelEvent)event);
        } else if (this.type == 3) {
            this.areaList.execute(this.getOwner(), x, y, 1.0f, this.item, this.toolItem, false);
        } else if (this.type == 4) {
            SpamBulletExplosion event = new SpamBulletExplosion(x, y, this.getDamage(), this.getOwner());
            this.getLevel().entityManager.events.add((LevelEvent)event);
        }
    }

    private static /* synthetic */ void lambda$addDrawables$0(TextureDrawOptions shadowOptions, TickManager tm) {
        shadowOptions.draw();
    }

    public static class FirePoolGroundEffectEvent
    extends GroundEffectEvent {
        protected int tickCounter;
        protected MobHitCooldowns hitCooldowns;
        protected FirePoolParticle particle;

        public FirePoolGroundEffectEvent() {
        }

        public FirePoolGroundEffectEvent(Mob owner, int x, int y, GameRandom uniqueIDRandom) {
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
                this.particle = new FirePoolParticle(this.level, this.x, this.y, 1000L);
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
            if (clientSubmitted || !target.buffManager.hasBuff(BuffRegistry.Debuffs.ON_FIRE)) {
                target.addBuff(new ActiveBuff(BuffRegistry.Debuffs.ON_FIRE, target, 5000, (Attacker)this), true);
            }
            target.isServerHit(new GameDamage(1.0f), (float)this.x, (float)this.y, 0.0f, (Attacker)this.getAttackOwner());
        }

        public void hitObject(LevelObjectHit hit) {
        }

        public void clientTick() {
            ++this.tickCounter;
            if (this.tickCounter > 20) {
                this.over();
            } else {
                super.clientTick();
            }
        }

        public void serverTick() {
            ++this.tickCounter;
            if (this.tickCounter > 20) {
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

    public static class SpamBulletExplosion
    extends ExplosionEvent
    implements Attacker {
        public SpamBulletExplosion() {
            this(0.0f, 0.0f, new GameDamage(0.0f), null);
        }

        public SpamBulletExplosion(float x, float y, GameDamage damage, Mob owner) {
            super(x, y, 100, damage, false, 0.0f, owner);
        }

        protected void playExplosionEffects() {
            SoundManager.playSound((GameSound)GameResources.explosionHeavy, (SoundEffect)SoundEffect.effect((float)this.x, (float)this.y).volume(0.8f).pitch(1.5f));
            this.level.getClient().startCameraShake(this.x, this.y, 300, 40, 0.5f, 0.5f, true);
        }

        protected boolean canHitMob(Mob target) {
            return super.canHitMob(target) && target.canBeTargeted(this.ownerMob, this.ownerMob.isPlayer ? ((PlayerMob)this.ownerMob).getNetworkClient() : null);
        }
    }

    public static class FirePoolParticle
    extends Particle {
        public static GameTexture texture;
        public int gel = GameRandom.globalRandom.nextInt(4);

        public FirePoolParticle(Level level, float x, float y, long lifeTime) {
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
            tileList.add(arg_0 -> FirePoolParticle.lambda$addDrawables$0((DrawOptions)options, arg_0));
        }

        private static /* synthetic */ void lambda$addDrawables$0(DrawOptions options, TickManager tm) {
            options.draw();
        }
    }
}

