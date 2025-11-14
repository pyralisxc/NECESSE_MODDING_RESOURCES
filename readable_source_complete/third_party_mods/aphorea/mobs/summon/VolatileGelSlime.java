/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.engine.util.GameRandom
 *  necesse.engine.util.GameUtils
 *  necesse.entity.levelEvent.LevelEvent
 *  necesse.entity.levelEvent.explosionEvent.BombExplosionEvent
 *  necesse.entity.levelEvent.explosionEvent.ExplosionEvent
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.MobDrawable
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.ai.behaviourTree.AINode
 *  necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI
 *  necesse.entity.mobs.ai.behaviourTree.trees.PlayerFollowerCollisionChaserAI
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob
 *  necesse.entity.particle.FleshParticle
 *  necesse.entity.particle.Particle
 *  necesse.entity.particle.Particle$GType
 *  necesse.gfx.GameResources
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.DrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.level.maps.Level
 *  necesse.level.maps.light.GameLight
 */
package aphorea.mobs.summon;

import aphorea.registry.AphBuffs;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.explosionEvent.BombExplosionEvent;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFollowerCollisionChaserAI;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class VolatileGelSlime
extends AttackingFollowingMob {
    public static GameTexture texture;
    int explosionTime = 0;
    int maxExplosionTime = GameRandom.globalRandom.getIntBetween(17, 23);

    public VolatileGelSlime() {
        super(5);
        this.setSpeed(60.0f);
        this.setFriction(2.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -21, 28, 28);
    }

    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI((Mob)this, (AINode)new PlayerFollowerCollisionChaserAI(512, null, 0, 1000, 640, 64));
    }

    public void serverTick() {
        super.serverTick();
        if (!this.removed() && this.explosionTime > 0) {
            ++this.explosionTime;
            if (this.explosionTime >= this.maxExplosionTime) {
                this.doExplosion();
            }
        }
    }

    public void clientTick() {
        super.clientTick();
        if (!this.removed() && this.explosionTime > 0) {
            ++this.explosionTime;
        }
    }

    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        if (texture != null) {
            for (int i = 0; i < 4; ++i) {
                this.getLevel().entityManager.addParticle((Particle)new FleshParticle(this.getLevel(), texture, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
            }
        }
    }

    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(this.getTileX(), this.getTileY());
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 51;
        Point sprite = this.getAnimSprite(x, y, this.getDir());
        drawY += this.getBobbing(x, y);
        TextureDrawOptionsEnd drawOptions = texture.initDraw().sprite(sprite.x, sprite.y, 64).color(light.getFloatRed(), light.getFloatGreen() * (1.0f - (float)this.explosionTime / (float)this.maxExplosionTime), light.getFloatBlue() * (1.0f - (float)this.explosionTime / (float)this.maxExplosionTime)).pos(drawX, drawY += this.getLevel().getTile(this.getTileX(), this.getTileY()).getMobSinkingAmount((Mob)this));
        list.add(new MobDrawable((DrawOptions)drawOptions){
            final /* synthetic */ DrawOptions val$drawOptions;
            {
                this.val$drawOptions = drawOptions;
            }

            public void draw(TickManager tickManager) {
                this.val$drawOptions.draw();
            }
        });
        if (!this.isWaterWalking()) {
            this.addShadowDrawables(tileList, level, x, y, light, camera);
        }
    }

    public Point getAnimSprite(int x, int y, int dir) {
        int animTime = 200;
        int spriteX = this.inLiquid(x, y) ? 4 + GameUtils.getAnim((long)this.getWorldEntity().getTime(), (int)2, (int)animTime) : GameUtils.getAnim((long)this.getWorldEntity().getTime(), (int)4, (int)animTime);
        return new Point(spriteX, dir);
    }

    public boolean canCollisionHit(Mob target) {
        return false;
    }

    public void collidedWith(Mob other) {
        super.collidedWith(other);
        if (this.explosionTime <= 0 && !this.removed() && other.canBeTargeted((Mob)this, this.getPvPOwner()) && other.canBeHit((Attacker)this)) {
            ActiveBuff buff = new ActiveBuff(AphBuffs.STICKY, other, 2000, (Attacker)this);
            other.addBuff(buff, true);
            this.explosionTime = 1;
        }
    }

    public void doExplosion() {
        this.spawnDeathParticles(GameRandom.globalRandom.getFloatBetween(-600.0f, 600.0f), GameRandom.globalRandom.getFloatBetween(-600.0f, 600.0f));
        this.remove();
        if (this.summonDamage != null) {
            BombExplosionEvent event = new BombExplosionEvent(this.x, this.y, 140, this.summonDamage, false, false, 0.0f, this.getFollowingMob());
            this.getLevel().entityManager.events.add((LevelEvent)event);
        }
    }

    public static class VolatileGelExplosion
    extends ExplosionEvent
    implements Attacker {
        public VolatileGelExplosion() {
            this(0.0f, 0.0f, new GameDamage(0.0f), null);
        }

        public VolatileGelExplosion(float x, float y, GameDamage damage, Mob owner) {
            super(x, y, 140, damage, false, 0.0f, owner);
        }

        protected void playExplosionEffects() {
            SoundManager.playSound((GameSound)GameResources.explosionHeavy, (SoundEffect)SoundEffect.effect((float)this.x, (float)this.y).volume(2.5f).pitch(1.5f));
            this.level.getClient().startCameraShake(this.x, this.y, 300, 40, 3.0f, 3.0f, true);
        }

        protected boolean canHitMob(Mob target) {
            return super.canHitMob(target) && (target == this.ownerMob || target.canBeTargeted(this.ownerMob, ((PlayerMob)this.ownerMob).getNetworkClient()));
        }
    }
}

