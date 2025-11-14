/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.registries.BuffRegistry$Debuffs
 *  necesse.engine.sound.SoundPlayer
 *  necesse.entity.Entity
 *  necesse.entity.chains.Chain
 *  necesse.entity.chains.ChainLocation
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.projectile.boomerangProjectile.BoomerangProjectile
 *  necesse.entity.trails.Trail
 *  necesse.gfx.GameResources
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.texture.TextureDrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd
 *  necesse.gfx.drawables.EntityDrawable
 *  necesse.gfx.drawables.LevelSortedDrawable
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.gfx.gameTexture.GameSprite
 *  necesse.level.maps.Level
 *  necesse.level.maps.LevelObjectHit
 *  necesse.level.maps.light.GameLight
 */
package aphorea.projectiles.rune;

import aphorea.registry.AphBuffs;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundPlayer;
import necesse.entity.Entity;
import necesse.entity.chains.Chain;
import necesse.entity.chains.ChainLocation;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.projectile.boomerangProjectile.BoomerangProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class RuneOfSpiderEmpressProjectile
extends BoomerangProjectile {
    private Chain chain;

    public RuneOfSpiderEmpressProjectile() {
    }

    public RuneOfSpiderEmpressProjectile(float x, float y, float angle, GameDamage damage, float projectileSpeed, Mob owner) {
        this.x = x;
        this.y = y;
        this.setAngle(angle);
        this.setDamage(damage);
        this.setOwner(owner);
        this.setDistance(500);
        this.speed = projectileSpeed;
    }

    public void init() {
        super.init();
        this.setWidth(10.0f, true);
        this.height = 18.0f;
        this.piercing = 0;
        this.isSolid = true;
        final Mob owner = this.getOwner();
        if (owner != null) {
            this.chain = new Chain(new ChainLocation(){

                public int getX() {
                    return (int)owner.x;
                }

                public int getY() {
                    return (int)owner.y;
                }

                public boolean removed() {
                    return false;
                }
            }, (ChainLocation)this);
            this.chain.sprite = new GameSprite(GameResources.chains, 5, 0, 32);
            this.chain.height = this.getHeight();
            this.getLevel().entityManager.addChain(this.chain);
        }
    }

    protected void returnToOwner() {
        if (!this.returningToOwner) {
            this.speed *= 2.0f;
        }
        super.returnToOwner();
    }

    protected SoundPlayer playMoveSound() {
        return null;
    }

    public Trail getTrail() {
        return null;
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (!this.removed()) {
            GameLight light = level.getLightLevel((Entity)this);
            int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
            int drawY = camera.getDrawY(this.y) - this.texture.getHeight() / 2;
            TextureDrawOptionsEnd options = this.texture.initDraw().light(light).rotate(this.getAngle(), this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(drawX, drawY - (int)this.getHeight());
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
            this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle(), this.shadowTexture.getHeight() / 2);
        }
    }

    public void remove() {
        if (this.chain != null) {
            this.chain.remove();
        }
        super.remove();
    }

    public void onHit(Mob mob, LevelObjectHit object, float x, float y, boolean fromPacket, ServerClient packetSubmitter) {
        if (mob != null && this.getOwner().isPlayer) {
            mob.addBuff(new ActiveBuff(BuffRegistry.Debuffs.SPIDER_WEB_SLOW, mob, 10000, (Attacker)this), true);
            mob.addBuff(new ActiveBuff(AphBuffs.STUN, mob, 3000, (Attacker)this), true);
        }
        super.onHit(mob, object, x, y, fromPacket, packetSubmitter);
    }
}

